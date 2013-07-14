package com.hornmicro.discovera.action

import org.eclipse.jface.action.Action
import org.eclipse.swt.SWT

import com.hornmicro.discovera.ui.MainController

class DeleteAction extends Action {
	MainController controller
	
	public DeleteAction(MainController controller) {
		super("Move to Trash")
		setAccelerator(SWT.MOD1 + SWT.BS )
		setToolTipText("Move the selected item to the trash")
		
		this.controller = controller
	}
	
	void run() {
		controller.trash()
	}

}
