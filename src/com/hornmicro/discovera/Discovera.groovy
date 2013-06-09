package com.hornmicro.discovera

import groovy.transform.CompileStatic

import org.codehaus.groovy.runtime.StackTraceUtils;
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.jface.databinding.swt.SWTObservables
import org.eclipse.swt.widgets.Display
import org.mbassy.BusConfiguration
import org.mbassy.IPublicationErrorHandler;
import org.mbassy.MBassador
import org.mbassy.PublicationError;

import com.hornmicro.discovera.ui.MainController
import com.hornmicro.event.BusEvent
import com.hornmicro.util.MainThreader

@CompileStatic
class Discovera {
    MBassador<BusEvent> bus
    
    Display display
    MainController controller
    
    public Discovera() {
        Display.appName = "Discovera"
        display = new Display()
        bus = new MBassador<BusEvent>(BusConfiguration.Default())
        controller = new MainController(bus:bus)
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
