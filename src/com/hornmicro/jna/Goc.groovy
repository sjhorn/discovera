package com.hornmicro.jna

import static com.hornmicro.jna.ObjectiveC.Utils.*
import com.hornmicro.jna.NSAutoreleasePool

import com.hornmicro.jna.ApplicationServices.AppleEvent
import com.hornmicro.jna.ApplicationServices.AEDesc
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.Structure

class Goc {
	
	static int stringToDescType(String id) {
		return  ((id.charAt(0) as int) << 24) + ((id.charAt(1) as int ) <<16) + 
			((id.charAt(2) as int) <<8) + (id.charAt(3) as int)
	}
	static main(args) {
		
//		NSMutableArray nsma = new NSMutableArray().array()
//		nsma.invoke(addObject: nsStringPtr("one"))
//		nsma.invoke(addObject: nsStringPtr("two"))
//		nsma.invoke(addObject: nsStringPtr("test"))
//		println nsma.count()
//		println nsma.lastObject()
//		println nsma.invoke(objectAtIndex: 1)
		 
		//Native.loadLibrary("ScriptingBridge", Library.class)
		
		//Pointer pool = msgSend(msgSend(cls("NSAutoreleasePool"), "alloc"), "init")
        
		NSAutoreleasePool pool = new NSAutoreleasePool().alloc().init()
		
		
		List<File> files = [new File("/tmp/one.txt"), new File("/tmp/two.txt")]
		
		NSAppleEventDescriptor urlListDescr = new NSAppleEventDescriptor().listDescriptor()
		
		//Pointer NSAppleEventDescriptorClass = cls("NSAppleEventDescriptor")
		
		files.eachWithIndex { File file, int idx ->
			NSURL nsURL = new NSURL().invoke(fileURLWithPath: nsStringPtr(file.absolutePath))
			NSAppleEventDescriptor descr = new NSAppleEventDescriptor().invoke(
				descriptorWithDescriptorType: stringToDescType("furl"),
				data: nsURL.absoluteString().invoke(dataUsingEncoding: 4)
			)
            urlListDescr.invoke(insertDescriptor:descr, atIndex: idx+1)
		}
		
		
		ProcessSerialNumber psn = getFinderPSN()
		NSAppleEventDescriptor targetDesc = new NSAppleEventDescriptor().invoke(
			descriptorWithDescriptorType: stringToDescType("psn "),
			bytes: psn,
			length: psn.size()
		)
		NSAppleEventDescriptor descriptor = new NSAppleEventDescriptor().invoke(
			appleEventWithEventClass: stringToDescType("core"),
			eventID: stringToDescType("delo"),
			targetDescriptor: targetDesc,
			returnID: -1, 		// kAutoGenerateReturnID
			transactionID: 0 	// kAnyTransactionID
		)
		
		descriptor.invoke(
			setDescriptor: urlListDescr,
			forKeyword: stringToDescType("----")
		)

		AppleEvent replyEvent = new AppleEvent()
		AppleEvent event = new AppleEvent(descriptor.aeDesc())
		int replyErr = ApplicationServices.INSTANCE.AESendMessage(
			event,
			replyEvent,
			3, // kAEWaitReply
			new NativeLong(-1) // kAEDefaultTimeout
		)
		
		if(replyErr != 0  ) { // noErr 
			println "error 1 (sending apple event)"
			return
		}
		
		AEDesc replyAEDesc = new AEDesc()
		replyErr = ApplicationServices.INSTANCE.AEGetParamDesc(
			replyEvent, stringToDescType("----"), stringToDescType("****"), replyAEDesc);
		
		if (replyErr != 0) {
			println "error 2 [${replyErr}](getting apple event result/event not actioned)"
			return
		}
		NSAppleEventDescriptor replyDesc = new NSAppleEventDescriptor().alloc().invoke(
			initWithAEDescNoCopy: replyAEDesc
		).autorelease()
		
		if(replyDesc.descriptorType() != stringToDescType("list") || 
			replyDesc.numberOfItems() != files.size()) {
			println "error 3 (not all items trashed)"
		}
		pool.drain()
		println "All good!"
	}
	
	static ProcessSerialNumber getFinderPSN() {
		ProcessSerialNumber psn = new ProcessSerialNumber(0,0)
		
		Pointer appsEnumerator = msgSend(msgSend(msgSend(cls("NSWorkspace"), "sharedWorkspace"), "launchedApplications"), "objectEnumerator")
		Pointer appInfoDict
		while( (appInfoDict = msgSend(appsEnumerator, "nextObject")).peer ) {
			Pointer NSApplicationBundleIdentifier = nsStringPtr("NSApplicationBundleIdentifier")
			if( nsStringToString(msgSend(appInfoDict, "objectForKey:", NSApplicationBundleIdentifier)) == "com.apple.finder") {
				psn.highLongOfPSN = msgSendInt(msgSend(appInfoDict, "objectForKey:", nsStringPtr("NSApplicationProcessSerialNumberHigh")), "longValue")
				psn.lowLongOfPSN = msgSendInt(msgSend(appInfoDict, "objectForKey:", nsStringPtr("NSApplicationProcessSerialNumberLow")), "longValue")
				break
			}
		}
		return psn
	}
		
	public static class ProcessSerialNumber extends Structure {
		public int highLongOfPSN 
		public int lowLongOfPSN
		
		public ProcessSerialNumber() {
			super()
		}
		public ProcessSerialNumber(int highLongOfPSN, int lowLongOfPSN) {
			super();
			this.highLongOfPSN = highLongOfPSN;
			this.lowLongOfPSN = lowLongOfPSN;
		}

		protected List getFieldOrder() {
			return Arrays.asList(["highLongOfPSN", "lowLongOfPSN"] as String[])
		}		 
	}
	
}
