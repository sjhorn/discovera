package com.hornmicro.util

import org.eclipse.swt.internal.cocoa.NSString;

import com.hornmicro.util.ScriptingBridge.SBApplication

class TestScriptingBridge {

	static main(args) {
		SBApplication sb = ScriptingBridge.SCRIPTINGBRIDGE.applicationWithBundleIdentifier(new NSString().initWithString("com.apple.iTunes"))
		println sb.getPointer()
	}

}
