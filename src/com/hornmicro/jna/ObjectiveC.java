package com.hornmicro.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface ObjectiveC extends Library {
    public static ObjectiveC RUNTIME = (ObjectiveC) Native.loadLibrary("objc.A", ObjectiveC.class);
    
    public Pointer objc_lookUpClass(String name);
    public String class_getName(Pointer id);
    public Pointer class_getProperty(Pointer cls, String name);
    public Pointer class_getSuperclass(Pointer cls);
    public int class_getVersion(Pointer theClass);
    public String class_getWeakIvarLayout(Pointer cls);
    public boolean class_isMetaClass(Pointer cls);
    public int class_getInstanceSize(Pointer cls);
    public Pointer class_getInstanceVariable(Pointer cls, String name);
    public Pointer class_getInstanceMethod(Pointer cls, Pointer aSelector);
    public Pointer class_getClassMethod(Pointer cls, Pointer aSelector);
    
    public String class_getIvarLayout(Pointer cls);
    public Pointer class_getMethodImplementation(Pointer cls, Pointer name);
    public Pointer class_getMethodImplementation_stret(Pointer cls, Pointer name);
    public Pointer class_replaceMethod(Pointer cls, Pointer name, Pointer imp, String types);
    public Pointer class_respondsToSelector(Pointer cls, Pointer sel);
    public void class_setIvarLayout(Pointer cls, String layout);
    public Pointer class_setSuperclass(Pointer cls, Pointer newSuper);
    public void class_setVersion(Pointer theClass, int version);
    public void class_setWeakIvarLayout(Pointer cls, String layout);
    public String ivar_getName(Pointer ivar);
    public long ivar_getOffset(Pointer ivar);
    public String ivar_getTypeEncoding(Pointer ivar);
    public String method_copyArgumentType(Pointer method, int index);
    public String method_copyReturnType(Pointer method);
    public void method_exchangeImplementations(Pointer m1, Pointer m2);
    public void method_getArgumentType(Pointer method, int index, Pointer dst, long dst_len);
    public Pointer method_getImplementation(Pointer method);
    public Pointer method_getName(Pointer method);
    public int method_getNumberOfArguments(Pointer method);
    public void method_getReturnType(Pointer method, Pointer dst, long dst_len);
    public String method_getTypeEncoding(Pointer method);
    public Pointer method_setImplementation(Pointer method, Pointer imp);
    public Pointer objc_allocateClassPair(Pointer superclass, String name, long extraBytes);
    public Pointer[] objc_copyProtocolList(Pointer outCount);
    public Pointer objc_getAssociatedObject(Pointer object, String key);
    public Pointer objc_getClass(String name);
    public int objc_getClassList(Pointer buffer, int bufferlen);
    public Pointer objc_getFutureClass(String name);
    public Pointer objc_getMetaClass(String name);
    public Pointer objc_getProtocol(String name);
    public Pointer objc_getRequiredClass(String name);
    public long objc_msgSend(Pointer theReceiver, Pointer theSelector,Object... arguments);
    
    public long objc_msgSendSuper(Pointer superClassStruct, Pointer op, Object... arguments);
    public long objc_msgSendSuper_stret(Pointer superClassStruct, Pointer op, Object... arguments);
    public double objc_msgSend_fpret(Pointer self, Pointer op, Object... arguments);
    public void objc_msgSend_stret(Pointer stretAddr, Pointer theReceiver, Pointer theSelector, Object... arguments);
    public void objc_registerClassPair(Pointer cls);
    public void objc_removeAssociatedObjects(Pointer object);
    public void objc_setAssociatedObject(Pointer object, Pointer key, Pointer value, Pointer policy);
    public void objc_setFutureClass(Pointer cls, String name);
    public Pointer object_copy(Pointer obj, long size);
    public Pointer object_dispose(Pointer obj);
    public Pointer object_getClass(Pointer object);
    public String object_getClassName(Pointer obj);
    public Pointer object_getIndexedIvars(Pointer obj);
    public Pointer object_getInstanceVariable(Pointer obj, String name, Pointer outValue);
    public Pointer object_getIvar(Pointer object, Pointer ivar);
    public Pointer object_setClass(Pointer object, Pointer cls);
    public Pointer object_setInstanceVariable(Pointer obj, String name, Pointer value);
    public void object_setIvar(Pointer object, Pointer ivar, Pointer value);
    public String property_getAttributes(Pointer property);
    public boolean protocol_conformsToProtocol(Pointer proto, Pointer other);
    public Structure protocol_copyMethodDescriptionList(Pointer protocol, boolean isRequiredMethod, boolean isInstanceMethod, Pointer outCount);
    public Pointer protocol_copyPropertyList(Pointer proto, Pointer outCount);
    public Pointer protocol_copyProtocolList(Pointer proto, Pointer outCount);
    public Pointer protocol_getMethodDescription(Pointer proto, Pointer aSel, boolean isRequiredMethod, boolean isInstanceMethod);
    public String protocol_getName(Pointer proto);
    public Pointer protocol_getProperty(Pointer proto, String name, boolean isRequiredProperty, boolean isInstanceProperty);
    public boolean protocol_isEqual(Pointer protocol, Pointer other);
    public String sel_getName(Pointer aSelector);
    
    public Pointer sel_getUid(String name);
    public boolean sel_isEqual(Pointer lhs, Pointer rhs);
    public Pointer sel_registerName(String name);
    
    public class Utils {
    	public static Pointer cls(String name) {
    		return ObjectiveC.RUNTIME.objc_lookUpClass(name);
    	}
    	public static String clsName(Pointer cls) {
    		return ObjectiveC.RUNTIME.class_getName(cls);
    	}
    	public static String clsNameObj(Pointer obj) {
    		return ObjectiveC.RUNTIME.object_getClassName(obj);
    	}
    	public static Pointer sel(String sel) {
    		return ObjectiveC.RUNTIME.sel_getUid(sel);
    	}
    	public static long msg(Pointer cls, Pointer selector, Object... o) {
    		return ObjectiveC.RUNTIME.objc_msgSend(cls, selector, o);
    	}
    	public static Pointer msgSend(Pointer cls, String selector, Object... o) {
    		return new Pointer( msg(cls, sel(selector), o) );
    	}
    	public static int msgSendInt(Pointer cls, String selector, Object... o) {
    		return new Long( msg(cls, sel(selector), o) ).intValue();
    	}
    	public static Pointer nsStringPtr(String s) {
    		return msgSend(cls("NSString"), "stringWithUTF8String:", s);
    	}
    	public static String nsStringToString(Pointer nsStringPtr) {
    		return msgSend(nsStringPtr, "UTF8String").getString(0);
    	}
    	static int stringToDescType(String id) {
    		return  ((id.charAt(0)) << 24) + ((id.charAt(1)) <<16) + 
    			((id.charAt(2)) <<8) + (id.charAt(3));
    	}
    	static int noErr = 0;
    	static int kNilOptions = 0;
    }
}
