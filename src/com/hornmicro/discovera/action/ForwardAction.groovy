package com.hornmicro.discovera.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import com.hornmicro.discovera.ui.MainController;

class ForwardAction extends Action {
    MainController controller
    
    public ForwardAction(MainController controller) {
        super("Forward")
        setAccelerator(SWT.MOD1 + (int)']' )
        setToolTipText("Go forward")
        
        this.controller = controller
    }
    
    void run() {
        controller.goForward()
    }

}