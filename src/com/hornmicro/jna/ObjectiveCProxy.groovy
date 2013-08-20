package com.hornmicro.jna

import static com.hornmicro.jna.ObjectiveC.Utils.*

import com.sun.jna.Pointer

class ObjectiveCProxy extends GroovyObjectSupport {
	Pointer pointer
	
	public ObjectiveCProxy() {
		String className = this.getClass().getSimpleName()
		pointer = cls(className)
		if(!pointer) {
			throw new RuntimeException("Failed to create a proxy for ${this.getClass().getSimpleName()}")
		}
		if(!TypeUtil.isRegistered(className)) {
			TypeUtil.registerType(className, this.getClass(), pointer)
		}
	}
	
	public ObjectiveCProxy(Pointer pointer) {
		this.pointer = pointer
	}
	
	public Object invokeMethod(String name, Object args) {
		MethodSignature methodSignature = new MethodSignature(pointer, name)
		if(!methodSignature.selector) {
			throw new MissingMethodException("Could not find function for name ${name}, args: ${args}")
		}
		if(methodSignature.types.size() != args.size()) {
			throw new RuntimeException("Wrong argument count."+
				"The selector ${name} requires ${methodSignature.types.size()} "+
				"arguments, but received ${args.size()} for name ${name}, args: ${args}")
		}
		//println "calling $name"
        Object[] newArgs = convertArgumentTypes(args, methodSignature)
		return sendAndConvertReturnType(methodSignature, newArgs)
	}
	
	public <T> T invoke(Map options) {
		String name = options.keySet().join(":") + ":"
		return (T) invokeMethod(name, options.values().toArray())
	}
	
	public <T> T invoke(String name, Object... args) {
		return (T) invokeMethod(name, args)
	}
	
	public Pointer getPtr() {
		return pointer
	}
	
	public int getInt() {
		return new Long(pointer.peer).intValue()
	}
	
	public String getString() {
		return pointer.getString(0)
	}
	
	public int invokePtr(Map options) {
		return invoke(options).getPtr()
	}
	
	public int invokeInt(Map options) {
		return invoke(options).getInt()
	}
	
	@Override
	public String toString() {
		String className = clsNameObj(pointer)
		if ( className in ["NSString", "__NSCFString", "__NSCFConstantString"] ){
			return nsStringToString(pointer)
		}
		return "ObjectiveCProxy for ${pointer.peer.toString()}"
	}
	
	private Object[] convertArgumentTypes(Object args, MethodSignature methodSignature) {
		Object[] newArgs
		if(args instanceof Object[]) {
			newArgs = new Object[args.size()]
			for(int idx = 0; idx < args.size(); idx++) {
				newArgs[idx] = new TypeUtil().jToC(args[idx], methodSignature.types[idx])
			}
		} else {
			newArgs = new Object[1]
			newArgs[0] = new TypeUtil().jToC(args, methodSignature.types[0])
		}
		return newArgs
	}
	
	private Object sendAndConvertReturnType(MethodSignature methodSignature, Object[] args) {
		String returnTypeFirstChar = methodSignature.returnType[0]
		Object ret
		if ( "[{(".indexOf(returnTypeFirstChar) == -1 ){
			if ( "df".indexOf(returnTypeFirstChar) != -1 ) {
				ret = ObjectiveC.RUNTIME.objc_msgSend_fpret(pointer, methodSignature.selector, args) 
			} else {
				ret = ObjectiveC.RUNTIME.objc_msgSend(pointer, methodSignature.selector, args)
			}
			return new TypeUtil().cToJ(ret, methodSignature.returnType)
		}
		
		// Need to check this ! Wrapping all structs in ObjectiveCProxy 
		ret = ObjectiveC.RUNTIME.objc_msgSend(pointer, methodSignature.selector, args)
		return new TypeUtil.NSObjectMapper().cToJ(ret, methodSignature.returnType)
	}
}
