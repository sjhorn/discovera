package com.hornmicro.util;

import org.eclipse.swt.internal.cocoa.NSString;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;

public interface ScriptingBridge extends Library {
	ScriptingBridge SCRIPTINGBRIDGE = (ScriptingBridge) Native.loadLibrary("ScriptingBridge", ScriptingBridge.class);
	
	public class SBApplication extends PointerByReference { }
	
	SBApplication applicationWithBundleIdentifier(NSString ident);
}
