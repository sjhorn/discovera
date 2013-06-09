package com.hornmicro.util

import groovy.transform.CompileStatic

import java.util.concurrent.CountDownLatch

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.swt.internal.Callback
import org.eclipse.swt.internal.cocoa.NSObject
import org.eclipse.swt.internal.cocoa.OS

// Based on Silenio Quarti's code from https://bugs.eclipse.org/bugs/show_bug.cgi?id=389486

@CompileStatic
class MainThreader {
    static Callback callback
    static Closure closure
    static CountDownLatch latch
    
    static run(Closure closure) { 
        MainThreader.closure = closure
        MainThreader.callback = new Callback(MainThreader, "proc", 2)
        long cls = OS.objc_lookUpClass("RunOnMainLoop")
        if (cls == 0) {
            cls = OS.objc_allocateClassPair(OS.class_NSObject, "RunOnMainLoop", 0)
            OS.class_addMethod(cls, OS.sel_run, callback.getAddress(), "@:")
            OS.objc_registerClassPair(cls)
        } else {
            OS.method_setImplementation(OS.sel_run, callback.getAddress())
        }
        
        long id = OS.objc_msgSend(cls, OS.sel_alloc)
        NSObject obj = new NSObject(id)
        obj.init()
        obj.performSelectorOnMainThread(OS.sel_run, null, false)
        obj.release()

        latch = new CountDownLatch(1)
        try {
            latch.await()
        } catch (InterruptedException e) {
            throw new RuntimeException(e)
        }
    }
    
    static long proc(long id, long sel) {
        try {
            callback.dispose()
            closure?.call()
        } catch (Throwable e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
        } finally {
            latch.countDown()
        }
        return 0
    }
}