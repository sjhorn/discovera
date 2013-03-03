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

import com.hornmicro.util.MainThreader
import com.hornmicro.util.Resources


@CompileStatic
class MainView extends Composite {
    ToolbarView toolbarView
    SidebarView sidebarView
    TreeView treeView
    StatusbarView statusbarView
    
    public MainView(Composite parent, int style) {
        super(parent, style)
    }
    
    void createContents() {
        setBackground(new Color(shell.display, 0xb4, 0xb4, 0xb4))
        GridLayout gLayout = new GridLayout(1, false)
        gLayout.with {
            marginWidth = marginHeight = 0
            verticalSpacing = 1
        }
        setLayout(gLayout)
        
        toolbarView = new ToolbarView(shell.getToolBar())
        
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
        
        layout()
    }
}
