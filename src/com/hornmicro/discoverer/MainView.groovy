package com.hornmicro.discoverer

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

class MainView extends Composite {
    
    public MainView(Composite parent) {
        super(parent, SWT.NONE)
        setBackground(new Color(shell.display, 0xb4, 0xb4, 0xb4))
        GridLayout layout = new GridLayout(1, false)
        layout.with {
            marginWidth = marginHeight = 0
            verticalSpacing = 1
        } 
        setLayout(layout)
        createView()
    }

    void createView() {
        new ToolbarView(shell.getToolBar())
        
        SashForm sform = new SashForm(this, SWT.HORIZONTAL)
        sform.setSashWidth(1)
        sform.setBackground(getBackground())
        sform.setLayoutData(new GridData(GridData.FILL_BOTH))

        new SidebarView(sform)
        new TreeView(sform, new File("/Volumes"))

        sform.setWeights([1, 2] as int[])

        StatusbarView statusbar = new StatusbarView(this)

        // horiz align, vert align, grab horiz, grab vert, horiz span, vert span
        sform.layoutData = new GridData (SWT.FILL, SWT.FILL, true, true, 1, 1)

        GridData textLData = new GridData (SWT.FILL, SWT.FILL, true, false, 1, 1)
        textLData.heightHint = 22
        statusbar.layoutData = textLData
    }

    static main(args) {
        MainThreader.run {
            Display.appName = "Discovera"
            Display display = new Display()
            
            Shell shell = new Shell(display)
            shell.text = "Discovera"
            shell.setImage(Resources.getImage("gfx/icon_256x256.png"))

            
            new MainView(shell)
            shell.setLayout(new FillLayout())
            shell.setSize(800, 600)
            shell.open()
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep()
            }
            display.dispose()
        }
    }
}
