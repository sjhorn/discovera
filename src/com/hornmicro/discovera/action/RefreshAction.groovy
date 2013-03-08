package com.hornmicro.discovera.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import com.hornmicro.discovera.ui.MainController;

class RefreshAction extends Action {
    MainController controller
    
    public RefreshAction(MainController controller) {
        super("Refresh")
        setAccelerator(SWT.MOD1 + (int)'R' )
        setToolTipText("Refresh")
        
        this.controller = controller
    }
    
    void run() {
        controller.refresh()
    }

}