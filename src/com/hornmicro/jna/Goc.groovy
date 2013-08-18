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
		//println "hello world"
//		Pointer nsMutableArrayClass = cls("NSMutableArray")
//		Thread.sleep(10000)
//		
//		Pointer nsMutableArray = msgSend(nsMutableArrayClass, "array")
//		msgSend(nsMutableArray, "addObject:", nsStringPtr("one"))
//		msgSend(nsMutableArray, "addObject:", nsStringPtr("two"))
//		msgSend(nsMutableArray, "addObject:", nsStringPtr("three"))
//		msgSend(nsMutableArray, "addObject:", nsStringPtr("four"))
//		
//		println msgSendInt(nsMutableArray, "count")
//		println nsStringToString(msgSend(nsMutableArray, "lastObject"))
		
		NSMutableArray nsma = new NSMutableArray().array()
		nsma.invoke(addObject: nsStringPtr("one"))
		nsma.invoke(addObject: nsStringPtr("two"))
		nsma.invoke(addObject: nsStringPtr("test"))
		println nsma.count()
		println nsma.lastObject()
		println nsma.invoke(objectAtIndex: 1)
		 
		//Native.loadLibrary("ScriptingBridge", Library.class)
		
		//Pointer pool = msgSend(msgSend(cls("NSAutoreleasePool"), "alloc"), "init")
        /*
		ObjectiveCProxy pool = new NSAutoreleasePool().alloc().init()
		
		List<File> files = [new File("/tmp/one.txt"), new File("/tmp/two.txt")]
		
		ObjectiveCProxy urlListDescr = new NSAppleEventDescriptor().listDescriptor()
		
		Pointer NSAppleEventDescriptorClass = cls("NSAppleEventDescriptor")
		
		files.eachWithIndex { File file, int idx ->
			ObjectiveCProxy nsURL = new NSURL().invoke(fileURLWithPath: nsStringPtr(file.absolutePath))
			ObjectiveCProxy descr = new NSAppleEventDescriptor().invoke(
				descriptorWithDescriptorType: stringToDescType("furl"),
				data: nsURL.absoluteString().invoke(dataUsingEncoding: 4)
			)
            urlListDescr.invoke(insertDescriptor:descr, atIndex: idx+1)
		}
		ProcessSerialNumber psn = getFinderPSN()
		Pointer targetDesc = msgSend(
			NSAppleEventDescriptorClass, 
			"descriptorWithDescriptorType:bytes:length:",
			stringToDescType("psn "),
			psn,
			psn.size()
		)
		Pointer descriptor = msgSend(
			NSAppleEventDescriptorClass, 
			"appleEventWithEventClass:eventID:targetDescriptor:returnID:transactionID:",
			stringToDescType("core"),
			stringToDescType("delo"),
			targetDesc,
			-1, // kAutoGenerateReturnID
			0 // kAnyTransactionID
		)
		msgSend(descriptor, "setDescriptor:forKeyword:", urlListDescr.ptr, stringToDescType("----"))
		AppleEvent replyEvent = new AppleEvent()
		AppleEvent event = new AppleEvent(msgSend(descriptor, "aeDesc"))
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
		Pointer replyDesc = msgSend(
			msgSend(
				msgSend(NSAppleEventDescriptorClass, "alloc"),
				"initWithAEDescNoCopy:",
				replyAEDesc
			),
			"autorelease"
		)
		if (msgSendInt(replyDesc, "descriptorType") != stringToDescType("list") ||
			msgSendInt(replyDesc, "numberOfItems") != files.size() ) {
			println "error 3 (not all items trashed)"
		}
		
		//msgSend(pool, "drain")
		pool.drain()
		println "All good!"
        */
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
