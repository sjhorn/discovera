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
import org.mbassy.MBassador
import org.mbassy.listener.Listener

import com.hornmicro.event.BusEvent
import com.hornmicro.util.Resources

@CompileStatic
class MainController extends ApplicationWindow implements DisposeListener, Runnable, Window.IExceptionHandler {
    private StatusbarController statusbarController
    private TreeController treeController
    private SidebarController sidebarController
    private ToolbarController toolbarController
    Composite parent
    MainView view
    MBassador<BusEvent> bus
    
    public MainController() {
        super(null)
    }

    void run() {
        bus.subscribe(this)
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
        
        toolbarController = new ToolbarController(view:view.toolbarView, bus:bus)
        sidebarController = new SidebarController(view:view.sidebarView, bus:bus)
        treeController = new TreeController(view:view.treeView, bus:bus)
        statusbarController = new StatusbarController(view:view.statusbarView, bus:bus)
        
        wireView()
        
        view.layout(false)
        return view
    }
    
    void wireView() {
        toolbarController.wireView()
        sidebarController.wireView()
        treeController.wireView()
        statusbarController.wireView()
    }
    
    @Listener
    void onBusEvent(BusEvent event) {
        switch(event.type) {
            case BusEvent.Type.FILE_SELECTED:
                treeController.setRoot((File) event.data)
                break
            case BusEvent.Type.FILES_SELECTED:
                println "$event.data"
                break
            default:
                break
        }
    }
    
    void widgetDisposed(DisposeEvent de) {
        Resources.dispose()
        bus?.unsubscribe(this)
    }

    void handleException(Throwable e) {
        StackTraceUtils.deepSanitize(e)
        e.printStackTrace()
    }
}
