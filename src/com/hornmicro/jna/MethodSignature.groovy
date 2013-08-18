package com.hornmicro.jna

import static com.hornmicro.jna.ObjectiveC.Utils.*

import com.sun.jna.Pointer

class MethodSignature {
	String selectorString
	Pointer selector
	String returnType
	List<String> types 
	
	public MethodSignature(Pointer classPointer, String selectorString) {
		this.selectorString = selectorString
		this.selector = ObjectiveC.RUNTIME.sel_getUid(selectorString)
		Pointer methodSignature = msgSend(classPointer, "methodSignatureForSelector:", selector)
		int argCount = (new Long(msgSend(methodSignature, "numberOfArguments").peer).intValue() - 2)
		
		returnType = msgSend(methodSignature, "methodReturnType").getString(0)
		types = (0..<argCount).collect { msgSend(methodSignature, "getArgumentTypeAtIndex:", it+2).getString(0) }
	}
}
