package com.hornmicro.discovera.action

import org.eclipse.jface.action.Action
import org.eclipse.swt.SWT

import com.hornmicro.discovera.ui.MainController

class NewFolderAction extends Action {
	MainController controller
	
	public NewFolderAction(MainController controller) {
		super("New Folder")
		setAccelerator(SWT.SHIFT + SWT.MOD1 + (int)'N' )
		setToolTipText("Create a new folder in the current location")
		
		this.controller = controller
	}

	void run() {
		controller.newFolder()
	}
}
