package com.hornmicro.jna

import com.sun.jna.Library
import com.sun.jna.Native

interface ScriptingBridge {
	class SBApplication extends ObjectiveCProxy {
		static { Native.loadLibrary("ScriptingBridge", Library.class) }
	}
}
