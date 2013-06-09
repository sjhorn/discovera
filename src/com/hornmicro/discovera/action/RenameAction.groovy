package com.hornmicro.discovera.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import com.hornmicro.discovera.ui.MainController;

class RenameAction extends Action {
    MainController controller
    
    public RenameAction(MainController controller) {
        super("Rename")
        setAccelerator(SWT.MOD1 + (int)'E' )
        setToolTipText("Rename")
        
        this.controller = controller
    }
    
    void run() {
		controller.rename()
    }

}