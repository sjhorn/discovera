package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import java.nio.file.Path

import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.events.TraverseEvent
import org.eclipse.swt.events.TraverseListener
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text

import com.hornmicro.discovera.action.UndoableAction
import com.hornmicro.discovera.action.UndoableAction.Type
import com.hornmicro.discovera.model.MainModel
import com.hornmicro.ui.CalloutDialog
import com.hornmicro.util.MainThreader

@CompileStatic
class RenameCallout extends CalloutDialog implements TraverseListener {
	MainModel model
	Boolean wasEscaped = false
	Path from
	Text filename
	Control toRefocus
	
	public RenameCallout(Shell parent, Point pointTo, MainModel model) {
		super(parent)
		this.toRefocus = parent.display.getFocusControl()
		this.pointTo = pointTo
		this.model = model
		pointer = CalloutDialog.Pointer.TOP
		
		from = ((Path) model.selectedFiles.first())
	}
	
	void createContents(Composite container) {
		Label label = new Label(container, SWT.NONE)
		label.text = "Rename to:"
		filename = new Text(container, SWT.BORDER)
		filename.text = from.getFileName()
		filename.addTraverseListener(this)
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(filename)
	}
	
	void deactivated() {
		if(!wasEscaped) {
			Path to = from.getParent().resolve(filename.text)
			if(from != to) {
				Map<Path, Path> fileMap = [:]
				fileMap.put(to, from)
				UndoableAction rename = new UndoableAction(type: Type.RENAME, files: fileMap)
				rename.run()
				model.addUndoableAction(rename)
			}
		}
		parent.display.asyncExec {
			toRefocus.setFocus()
		}
		super.deactivated()
	}
	
	void keyTraversed(TraverseEvent event) {
		switch(event.detail) {
			case SWT.TRAVERSE_ESCAPE:
				wasEscaped = true
			case SWT.TRAVERSE_RETURN:
				shell.getParent().setFocus()
				break
		}
	}
	
	static void main(args) {
		MainThreader.run {
			Display display = new Display()
			Shell shell = new Shell(display)
			RenameCallout rc = new RenameCallout(shell, new Point(300, 100))
			rc.open()
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep()
			}
			
			display.dispose()
		}
	}

}
