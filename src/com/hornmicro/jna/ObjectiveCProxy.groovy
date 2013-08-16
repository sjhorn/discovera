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
        Object[] newArgs = convertProxies(args)
		Object result = ObjectiveC.RUNTIME.objc_msgSend(pointer, selector, newArgs)
		return new ObjectiveCProxy(new Pointer(result))
	}
	
	public ObjectiveCProxy invoke(Map options) {
		String name = options.keySet().join(":") + ":"
		Pointer selector = ObjectiveC.RUNTIME.sel_getUid(name)
		if(!selector) {
			throw new MissingMethodException("Could not find function for name ${name}, args: ${options.values()}")
		}
        Object[] newArgs = convertProxies(options.values().toArray())
		Object result = ObjectiveC.RUNTIME.objc_msgSend(pointer, selector, newArgs)
		return new ObjectiveCProxy(new Pointer(result))
	}
	
	public Pointer getPtr() {
		return pointer
	}
	
	public int getInt() {
		return new Long(pointer.peer).intValue()
	}
    
    private Object[] convertProxies(Object args) {
        Object[] newArgs
        if(args instanceof Object[]) {
            newArgs = new Object[args.size()]
            for(int idx = 0; idx < args.size(); idx++) {
                newArgs[idx] = convertProxy(args[idx])
            }
        } else {
            newArgs = new Object[1]
            newArgs[0] = convertProxy(args) 
        }
        return newArgs
    }
    private Object convertProxy(Object o) {
        return o instanceof ObjectiveCProxy ? ((ObjectiveCProxy) o).ptr : o
    }
}
