package com.hornmicro.jna

import static com.hornmicro.jna.ObjectiveC.Utils.*

import com.hornmicro.jna.ApplicationServices.AEDesc
import com.hornmicro.jna.ApplicationServices.AppleEvent
import com.hornmicro.jna.MacTypes.ProcessSerialNumber;
import com.sun.jna.Pointer
import com.sun.jna.Structure

class Goc {
	
	static main(args) {
//		NSMutableArray nsma = new NSMutableArray().array()
//		nsma.invoke(addObject: nsStringPtr("one"))
//		nsma.invoke(addObject: nsStringPtr("two"))
//		nsma.invoke(addObject: nsStringPtr("test"))
//		println nsma.count()
//		println nsma.lastObject()
//		println nsma.invoke(objectAtIndex: 1)
        
		NSAutoreleasePool pool = new NSAutoreleasePool().alloc().init()
		
		List<File> files = [new File("/tmp/one.txt"), new File("/tmp/two.txt")]
		
		NSAppleEventDescriptor urlListDescr = new NSAppleEventDescriptor().listDescriptor()
		
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
			returnID: CoreServices.kAutoGenerateReturnID,
			transactionID: CoreServices.kAnyTransactionID
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
			CoreServices.kAEWaitReply,
			CoreServices.kAEDefaultTimeout
		)
		
		if(replyErr != noErr  ) { // noErr 
			println "error 1 (sending apple event)"
			return
		}
		
		AEDesc replyAEDesc = new AEDesc()
		replyErr = ApplicationServices.INSTANCE.AEGetParamDesc(
			replyEvent, stringToDescType("----"), stringToDescType("****"), replyAEDesc);
		
		if (replyErr != noErr) {
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
		
		ObjectiveCProxy appsEnumerator = new NSWorkspace().sharedWorkspace().launchedApplications().objectEnumerator()
		ObjectiveCProxy appInfoDict
		while( (appInfoDict = appsEnumerator.nextObject()) ) {
			if( appInfoDict.invoke(objectForKey: nsStringPtr("NSApplicationBundleIdentifier")).toString() == "com.apple.finder") {
				psn.highLongOfPSN = appInfoDict.invoke(objectForKey: nsStringPtr("NSApplicationProcessSerialNumberHigh")).longValue()
				psn.lowLongOfPSN = appInfoDict.invoke(objectForKey: nsStringPtr("NSApplicationProcessSerialNumberLow")).longValue() 
				break
			}
		}
		return psn
	}
		
	
}
