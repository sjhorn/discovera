package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic;

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.hornmicro.util.MainThreader
import com.hornmicro.util.Resources


@CompileStatic
class MainView extends Composite {
    SidebarView sidebarView
    TreeView treeView
    StatusbarView statusbarView
    
    ToolBar toolbar
    ToolItem back
    ToolItem forward
    ToolItem list
    ToolItem columns
    ToolItem icons
    ToolItem renameFile
    ToolItem newFolder
    ToolItem delete
    ToolItem refresh
    ToolItem settings
    
    public MainView(Composite parent, int style) {
        super(parent, style)
    }
    
    void createToolbar() {
        toolbar = shell.getToolBar()
        
        back = new ToolItem(toolbar, SWT.PUSH)
        back.enabled = false
        back.text = "Back"
        back.image = Resources.getImage("gfx/22gray/arrow-left.png")
        
        forward = new ToolItem(toolbar, SWT.PUSH)
        forward.enabled = false
        forward.text = "Forward"
        forward.image = Resources.getImage("gfx/22gray/arrow-right.png")
        
        ToolItem item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(1)
        
//        item = new ToolItem(toolbar, SWT.PUSH)
//        item.text = "Info"
//        item.image = Resources.getImage("gfx/22gray/info-sign.png")
//
//        item = new ToolItem(toolbar, SWT.SEPARATOR)
//        item.setWidth(SWT.DEFAULT)
        
        list = new ToolItem(toolbar, SWT.RADIO)
        list.text = "List"
        list.image = Resources.getImage("gfx/22gray/align-justify.png")
        list.setSelection(true)
        
        columns = new ToolItem(toolbar, SWT.RADIO)
        columns.text = "Columns"
        columns.image = Resources.getImage("gfx/22gray/columns.png")
        columns.enabled = false
        icons = new ToolItem(toolbar, SWT.RADIO)
        icons.text = "Icons"
        icons.image = Resources.getImage("gfx/22gray/th-large.png")
        icons.enabled = false

        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(SWT.DEFAULT)
        
        renameFile = new ToolItem(toolbar, SWT.PUSH)
        renameFile.text = "Rename"
        renameFile.image = Resources.getImage("gfx/22gray/wrench.png")
        
        newFolder = new ToolItem(toolbar, SWT.PUSH)
        newFolder.text = "New Folder"
        newFolder.image = Resources.getImage("gfx/22gray/folder-close.png")
        newFolder.enabled = false
        
        delete = new ToolItem(toolbar, SWT.PUSH)
        delete.text = "Delete"
        delete.image = Resources.getImage("gfx/22gray/trash.png")
        delete.setEnabled(false)
        
        item = new ToolItem(toolbar, SWT.SEPARATOR)
        item.setWidth(SWT.SEPARATOR_FILL)
        
        refresh = new ToolItem(toolbar, SWT.PUSH)
        refresh.text = "Refresh"
        refresh.image = Resources.getImage("gfx/22gray/refresh.png")
        
        settings = new ToolItem(toolbar, SWT.PUSH)
        settings.text = "Settings"
        settings.image = Resources.getImage("gfx/22gray/cog.png")
        settings.enabled = false
        
//        item = new ToolItem(toolbar, SWT.SEPARATOR)
//        Text search = new Text(toolbar, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL)
//        search.text = ""
//        item.setControl(search)
//        item.setWidth(150)
    }
    void createContents() {
        setBackground(Resources.getColor(0xb4, 0xb4, 0xb4))
        GridLayout gLayout = new GridLayout(1, false)
        gLayout.with {
            marginWidth = marginHeight = 0
            verticalSpacing = 1
        }
        setLayout(gLayout)
        
        createToolbar()
        
        SashForm sform = new SashForm(this, SWT.HORIZONTAL)
        sform.setSashWidth(1)
        sform.setBackground(getBackground())
        sform.setLayoutData(new GridData(GridData.FILL_BOTH))

        sidebarView = new SidebarView(sform, SWT.NONE)
        treeView = new TreeView(sform, SWT.NONE)

        sform.setWeights([1, 2] as int[])

        statusbarView = new StatusbarView(this)

        // horiz align, vert align, grab horiz, grab vert, horiz span, vert span
        sform.layoutData = new GridData (SWT.FILL, SWT.FILL, true, true, 1, 1)

        GridData textLData = new GridData (SWT.FILL, SWT.FILL, true, false, 1, 1)
        textLData.heightHint = 22
        statusbarView.layoutData = textLData
    }
}
