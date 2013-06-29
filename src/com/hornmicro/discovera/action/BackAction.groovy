package com.hornmicro.discovera.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import com.hornmicro.discovera.ui.MainController

class BackAction extends Action {
    MainController controller
    
    public BackAction(MainController controller) {
        super("Back")
        setAccelerator(SWT.MOD1 + (int)'[' )
        setToolTipText("Go back")
        
        this.controller = controller
    }
    
    void run() {
        controller.goBack()
    }

}