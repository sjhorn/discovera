package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.swt.widgets.ToolItem

import com.hornmicro.util.Resources


@CompileStatic
class ToolbarView {
    ToolBar toolbar
    
    public ToolbarView(ToolBar toolbar) {
        this.toolbar = toolbar
    }
    
    void createContents() {
        ToolItem item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Back"
        item.image = Resources.getImage("gfx/22gray/arrow-left.png")
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Forward"
        item.image = Resources.getImage("gfx/22gray/arrow-right.png")
        
        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(1)
        
//        item = new ToolItem(toolbar, SWT.PUSH)
//        item.text = "Info"
//        item.image = Resources.getImage("gfx/22gray/info-sign.png")
//        
//        item = new ToolItem(toolbar, SWT.SEPARATOR)
//        item.setWidth(SWT.DEFAULT)
        
        item = new ToolItem(toolbar, SWT.RADIO)
        item.text = "List"
        item.image = Resources.getImage("gfx/22gray/align-justify.png")
        item.setSelection(true)
        
        item = new ToolItem(toolbar, SWT.RADIO)
        item.text = "Columns"
        item.image = Resources.getImage("gfx/22gray/columns.png")
        item = new ToolItem(toolbar, SWT.RADIO)
        item.text = "Icons"
        item.image = Resources.getImage("gfx/22gray/th-large.png")

        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(SWT.DEFAULT)
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "New Folder"
        item.image = Resources.getImage("gfx/22gray/folder-close.png")
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Delete"
        item.image = Resources.getImage("gfx/22gray/trash.png")
        item.setEnabled(false)
        
        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(SWT.SEPARATOR_FILL)
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Refresh"
        item.image = Resources.getImage("gfx/22gray/refresh.png")
        
        item = new ToolItem(toolbar, SWT.PUSH)
        item.text = "Settings"
        item.image = Resources.getImage("gfx/22gray/cog.png")
        
//        item = new ToolItem(toolbar, SWT.SEPARATOR)
//        Text search = new Text(toolbar, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL)
//        search.text = ""
//        item.setControl(search)
//        item.setWidth(150)
    }

}
