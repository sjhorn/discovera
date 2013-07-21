package com.hornmicro.util

import static com.hornmicro.util.CoreServices.*
import static com.hornmicro.util.CoreServices.FSEventStreamCreateFlags.*
import static com.hornmicro.util.CoreServices.FSEventStreamEventFlags.*
import groovy.transform.ToString

import java.nio.file.Path
import java.nio.file.Paths

import com.hornmicro.util.CoreServices.FSEventStreamEventFlags;
import com.hornmicro.util.CoreServices.FSEventStreamRef
import com.sun.jna.NativeLong
import com.sun.jna.Pointer

// Chose calling OSX direct instead of java.nio stuff
// as it is still too slow for file level events 
// OSX 10.6+ api is richer then the java7 stuff as 
// it send file level events. not just folder level.
public class WatchFolder implements CoreServices.FSEventStreamCallback {
	public enum EVENT {
		CREATE,
		DELETE,
		RENAME,
		MODIFY
	}
	
	@ToString
	public class EventData {
		EVENT event
		Path path
		Path originalPath
	}

	final FSEventStreamRef stream
	final Closure callback
	
	public WatchFolder(Path folder, Closure callback) {
		final Pointer[] values = [ CFStringRef.toCFString(folder.toAbsolutePath().toString()).getPointer() ] as Pointer[]
		final CoreServices.CFArrayRef pathsToWatch = CORESERVICES.CFArrayCreate(null, values, CFIndex.valueOf(1), null)
		final double latency = 0.2
		stream = CORESERVICES.FSEventStreamCreate(
			Pointer.NULL,
			this,
			Pointer.NULL,
			pathsToWatch,
			kFSEventStreamEventIdSinceNow,
			latency,
			kFSEventStreamCreateFlagNoDefer | kFSEventStreamCreateFlagFileEvents
		)
		this.callback = callback
	}
	
	public void processEvents() {
		final CFRunLoopRef runLoop = CORESERVICES.CFRunLoopGetCurrent()
		final CFStringRef runLoopMode = CFStringRef.toCFString("kCFRunLoopDefaultMode")
		CORESERVICES.FSEventStreamScheduleWithRunLoop(stream, runLoop, runLoopMode)
		CORESERVICES.FSEventStreamStart(stream)
		CORESERVICES.CFRunLoopRun()
	}
	
	@Override
	public void invoke(FSEventStreamRef streamRef, Pointer clientCallBackInfo,
			NativeLong numEvents, Pointer eventPaths, Pointer eventFlags,
			Pointer eventIds) {
			
		Path fromPath
		for (String folderName : eventPaths.getStringArray(0, numEvents.intValue())) {
			int eventFlag = eventFlags.getInt(0)
			
//			System.out.print(folderName + " signaled");
//			for(FSEventStreamEventFlags flag: FSEventStreamEventFlags.values()) {
//				if( (eventFlags.getInt(0) & flag.getFlag()) != 0) {
//					System.out.println(" "+flag.name());
//				}
//			}
			
			// Rename
			if ((eventFlag & kFSEventStreamEventFlagItemRenamed.getFlag()) && !fromPath) {
				fromPath = Paths.get(folderName)
				continue
			} else if ((eventFlag & kFSEventStreamEventFlagItemRenamed.getFlag()) && fromPath) {
				callback?.call(new EventData(event:EVENT.RENAME, path: Paths.get(folderName), originalPath: fromPath))
				fromPath == null
				continue
			}
			
			
			if ((eventFlag & kFSEventStreamEventFlagItemCreated.getFlag())) {
				
				// Create
				callback?.call(new EventData(event:EVENT.CREATE, path: Paths.get(folderName)))
			} else if ((eventFlag & kFSEventStreamEventFlagItemRemoved.getFlag())) {
			
				// Delete
				callback?.call(new EventData(event:EVENT.DELETE, originalPath: Paths.get(folderName)))
			} else if (eventFlag & (
					kFSEventStreamEventFlagItemInodeMetaMod.getFlag() |
					kFSEventStreamEventFlagItemChangeOwner.getFlag() |
					kFSEventStreamEventFlagItemXattrMod.getFlag() |
					kFSEventStreamEventFlagItemModified.getFlag() )) {
				
				// Modify
				callback?.call(new EventData(event:EVENT.MODIFY, path: Paths.get(folderName)))
			}
		}
		
		// Moved out of our watch path (most likely to trash)
		if(fromPath != null) {
			callback?.call(new EventData(event:EVENT.RENAME, originalPath: fromPath))
		}
	}

	public static void main(String[] args) throws IOException {
		new WatchFolder(Paths.get("/private/tmp"), { EventData data ->
			println "Got ${data}"
		}).processEvents()
	}
}