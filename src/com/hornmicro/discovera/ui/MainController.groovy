package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.jface.window.Window
import org.eclipse.swt.SWT
import org.eclipse.swt.events.DisposeEvent
import org.eclipse.swt.events.DisposeListener
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Shell

import com.hornmicro.util.Resources

@CompileStatic
class MainController extends ApplicationWindow implements DisposeListener, Runnable, Window.IExceptionHandler {
    Composite parent
    MainView view
    
    public MainController() {
        super(null)
    }

    void run() {
        blockOnOpen = true
        open()
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell)
        shell.text = "Discovera"
        shell.setImage(Resources.getImage("gfx/icon_256x256.png"))
        shell.setBounds(10, 10, 800, 600)
        shell.addDisposeListener(this)
    }
    
    protected Control createContents(Composite parent) {
        this.parent = parent
        parent.setLayout(new FillLayout())
        view = new MainView(parent, SWT.NONE)
        view.createContents()
        wireView()
        
        view.layout(false)
        return view
    }
    
    void wireView() {
        
        // wire up child controllers
        new ToolbarController(view:view.toolbarView).wireView()
        new SidebarController(view:view.sidebarView).wireView()
        new TreeController(view:view.treeView).wireView()
        new StatusbarController(view:view.statusbarView).wireView()
    }
    
    void widgetDisposed(DisposeEvent de) {
        Resources.dispose()
    }

    void handleException(Throwable e) {
        StackTraceUtils.deepSanitize(e)
        e.printStackTrace()
    }
}
