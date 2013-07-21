package com.hornmicro.util;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

// 
// Mostly borrowed ideas from com.barbarysoftware.jna.CarbonAPI
// Updated to use CoreServices and kept in one java file 
// http://code.google.com/p/barbarywatchservice/
//
public interface CoreServices extends Library {
	CoreServices CORESERVICES = (CoreServices) Native.loadLibrary("CoreServices", CoreServices.class);
	
	public class CFAllocatorRef extends PointerByReference { }
	public class CFArrayRef extends PointerByReference { }
	public class CFIndex extends NativeLong {
	    private static final long serialVersionUID = 0;

	    public static CFIndex valueOf(int i) {
	        CFIndex idx = new CFIndex();
	        idx.setValue(i);
	        return idx;
	    }
	}
	public class CFRunLoopRef extends PointerByReference { }
	public class CFStringRef extends PointerByReference {

	    public static CFStringRef toCFString(String s) {
	        final char[] chars = s.toCharArray();
	        int length = chars.length;
	        return CORESERVICES.CFStringCreateWithCharacters(null, chars, CFIndex.valueOf(length));
	    }

	}
	public class FSEventStreamRef extends PointerByReference { }
	
	
	CFArrayRef CFArrayCreate(
            CFAllocatorRef allocator, // always set to Pointer.NULL
            Pointer[] values,
            CFIndex numValues,
            Void callBacks // always set to Pointer.NULL
    );

    CFStringRef CFStringCreateWithCharacters(
            Void alloc, //  always pass NULL
            char[] chars,
            CFIndex numChars
    );

    public FSEventStreamRef FSEventStreamCreate(
            Pointer v, // always use Pointer.NULL
            FSEventStreamCallback callback,
            Pointer context,  // always use Pointer.NULL
            CFArrayRef pathsToWatch,
            long sinceWhen, // use -1 for events since now
            double latency, // in seconds
            int flags // 0 is good for now

    );

    boolean FSEventStreamStart(FSEventStreamRef streamRef);

    void FSEventStreamStop(FSEventStreamRef streamRef);

    void FSEventStreamScheduleWithRunLoop(FSEventStreamRef streamRef, CFRunLoopRef runLoop, CFStringRef runLoopMode);

    CFRunLoopRef CFRunLoopGetCurrent();

    void CFRunLoopRun();

    void CFRunLoopStop(CFRunLoopRef rl);

    public interface FSEventStreamCallback extends Callback {
        void invoke(FSEventStreamRef streamRef, Pointer clientCallBackInfo, NativeLong numEvents, Pointer eventPaths, Pointer eventFlags, Pointer eventIds);
    }
    
    public enum FSEventStreamEventFlags {
    	   kFSEventStreamEventFlagNone(0x00000000),
    	   kFSEventStreamEventFlagMustScanSubDirs(0x00000001),
    	   kFSEventStreamEventFlagUserDropped(0x00000002),
    	   kFSEventStreamEventFlagKernelDropped(0x00000004),
    	   kFSEventStreamEventFlagEventIdsWrapped(0x00000008),
    	   kFSEventStreamEventFlagHistoryDone(0x00000010),
    	   kFSEventStreamEventFlagRootChanged(0x00000020),
    	   kFSEventStreamEventFlagMount(0x00000040),
    	   kFSEventStreamEventFlagUnmount(0x00000080),
    	   
    	   kFSEventStreamEventFlagItemCreated(0x00000100),
    	   kFSEventStreamEventFlagItemRemoved(0x00000200),
    	   kFSEventStreamEventFlagItemInodeMetaMod(0x00000400),
    	   kFSEventStreamEventFlagItemRenamed(0x00000800),
    	   kFSEventStreamEventFlagItemModified(0x00001000),
    	   kFSEventStreamEventFlagItemFinderInfoMod(0x00002000),
    	   kFSEventStreamEventFlagItemChangeOwner(0x00004000),
    	   kFSEventStreamEventFlagItemXattrMod(0x00008000),
    	   kFSEventStreamEventFlagItemIsFile(0x00010000),
    	   kFSEventStreamEventFlagItemIsDir(0x00020000),
    	   kFSEventStreamEventFlagItemIsSymlink(0x00040000);
    	   
    	   private final int flag;
    	   private FSEventStreamEventFlags(int flag) {
    		   this.flag = flag;
    	   }
    	   public int getFlag() {
    		   return flag;
    	   }
    };
    
    public class FSEventStreamCreateFlags {
    	public static final int kFSEventStreamCreateFlagNone = 0x00000000;
    	public static final int kFSEventStreamCreateFlagUseCFTypes = 0x00000001;
    	public static final int kFSEventStreamCreateFlagNoDefer = 0x00000002;
    	public static final int kFSEventStreamCreateFlagWatchRoot = 0x00000004;
    	public static final int kFSEventStreamCreateFlagIgnoreSelf = 0x00000008;
    	public static final int kFSEventStreamCreateFlagFileEvents = 0x00000010;
    };
    
    public static final int kFSEventStreamEventIdSinceNow = -1;
}
