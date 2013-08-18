package com.hornmicro.jna

import com.sun.jna.Pointer;

class NSMutableArray extends ObjectiveCProxy {
	static { TypeUtil.typeRegistry["__NSArrayM"] = NSMutableArray }
	
	public NSMutableArray() {
		super()
	}
	
	public NSMutableArray(Pointer pointer) {
		super(pointer)
	}
}
