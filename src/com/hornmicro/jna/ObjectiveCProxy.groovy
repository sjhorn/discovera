package com.hornmicro.jna

import static com.hornmicro.jna.ObjectiveC.Utils.*

import com.sun.jna.Pointer

class ObjectiveCProxy extends GroovyObjectSupport {
	Pointer pointer
	
	public ObjectiveCProxy() {
		this.pointer = cls(this.getClass().getSimpleName())
		if(!this.pointer) {
			throw new RuntimeException("Failed to create a proxy for ${this.getClass().getSimpleName()}")
		}
	}
	
	public ObjectiveCProxy(Pointer pointer) {
		this.pointer = pointer
	}
	
	public Object invokeMethod(String name, Object args) {
		println "calling $name"
		Pointer selector = ObjectiveC.RUNTIME.sel_getUid(name)
		if(!selector) {
			throw new MissingMethodException("Could not find function for name ${name}, args: ${args}")
		}
		Object result = ObjectiveC.RUNTIME.objc_msgSend(pointer, selector, args)
		return new ObjectiveCProxy(new Pointer(result))
	}
	
	public ObjectiveCProxy invoke(Map options) {
		String name = options.keySet().join(":") + ":"
		Pointer selector = ObjectiveC.RUNTIME.sel_getUid(name)
		if(!selector) {
			throw new MissingMethodException("Could not find function for name ${name}, args: ${options.values()}")
		}
		Object result = ObjectiveC.RUNTIME.objc_msgSend(pointer, selector, options.values().toArray() )
		return new ObjectiveCProxy(new Pointer(result))
	}
	
	public Pointer getPtr() {
		return pointer
	}
	
	public int getInt() {
		return new Long(pointer.peer).intValue()
	}
}
