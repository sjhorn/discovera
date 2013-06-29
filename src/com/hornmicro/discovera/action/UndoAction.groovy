package com.hornmicro.discovera.action

import org.eclipse.jface.action.Action
import org.eclipse.swt.SWT

import com.hornmicro.discovera.ui.MainController

class UndoAction extends Action {
    MainController controller
    
    public UndoAction(MainController controller) {
        super("Undo")
        setAccelerator(SWT.MOD1 + (int)'Z' )
        setToolTipText("Undo")
        
        this.controller = controller
    }
    
    void run() {
		controller.model.undo()
    }

}