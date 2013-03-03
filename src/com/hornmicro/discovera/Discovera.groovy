package com.hornmicro.discovera

import groovy.transform.CompileStatic

import org.eclipse.core.databinding.observable.Realm
import org.eclipse.jface.databinding.swt.SWTObservables
import org.eclipse.swt.widgets.Display

import com.hornmicro.discovera.ui.MainController
import com.hornmicro.util.MainThreader

@CompileStatic
class Discovera {
    Display display
    MainController controller
    
    public Discovera() {
        Display.appName = "Discovera"
        display = new Display()
        controller = new MainController()
    }
    
    void run() {
        Realm.runWithDefault(SWTObservables.getRealm(display), controller)
        display.dispose()
    }
    
    static main(args) {
        MainThreader.run {
            new Discovera().run()
        }
    }
}
