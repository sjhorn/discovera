package com.hornmicro.discoverer

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.swt.widgets.ToolItem

class ToolbarView {
    ToolBar toolbar
    
    public ToolbarView(ToolBar toolbar) {
        this.toolbar = toolbar
        createView()
    }
    
    void createView() {
        ToolItem item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Back"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/arrow-left.png")
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Forward"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/arrow-right.png")
        
        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(1)
        
//        item = new ToolItem(toolbar, SWT.PUSH)
//        item.text = "Info"
//        item.image = new Image(toolbar.shell.display, "gfx/22gray/info-sign.png")
//        
//        item = new ToolItem(toolbar, SWT.SEPARATOR)
//        item.setWidth(SWT.DEFAULT)
        
        item = new ToolItem(toolbar, SWT.RADIO)
        item.text = "List"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/align-justify.png")
        item.setSelection(true)
        
        item = new ToolItem(toolbar, SWT.RADIO)
        item.text = "Columns"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/columns.png")
        item = new ToolItem(toolbar, SWT.RADIO)
        item.text = "Icons"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/th-large.png")

        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(SWT.DEFAULT)
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "New Folder"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/folder-close.png")
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Delete"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/trash.png")
        
        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(SWT.SEPARATOR_FILL)
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Refresh"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/refresh.png")
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Settings"
        item.image = new Image(toolbar.shell.display, "gfx/22gray/cog.png")
        
//        item = new ToolItem(toolbar, SWT.SEPARATOR)
//        Text search = new Text(toolbar, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL)
//        search.text = ""
//        item.setControl(search)
//        item.setWidth(150)
    }

}
