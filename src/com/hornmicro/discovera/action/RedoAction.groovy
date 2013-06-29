package com.hornmicro.discovera.action

import org.eclipse.jface.action.Action
import org.eclipse.swt.SWT

import com.hornmicro.discovera.ui.MainController

class RedoAction extends Action {
    MainController controller
    
    public RedoAction(MainController controller) {
        super("Redo")
        setAccelerator(SWT.MOD1 + SWT.SHIFT + (int)'Z' )
        setToolTipText("Redo")
        
        this.controller = controller
    }
    
    void run() {
		controller.model.redo()
    }

}