package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.jface.action.Action
import org.eclipse.jface.action.MenuManager
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

import com.hornmicro.discovera.action.BackAction
import com.hornmicro.discovera.action.ForwardAction
import com.hornmicro.event.BusEvent
import com.hornmicro.util.Actions
import com.hornmicro.util.Bind
import com.hornmicro.util.Resources

@CompileStatic
class MainController extends ApplicationWindow implements DisposeListener, Runnable, Window.IExceptionHandler {
    private StatusbarController statusbarController
    private TreeController treeController
    private SidebarController sidebarController
    Composite parent
    MainModel model
    MainView view
    MBassador<BusEvent> bus
    
    Action backAction
    Action forwardAction
    
    public MainController() {
        super(null)
        
        backAction = new BackAction(this)
        forwardAction = new ForwardAction(this)
        
        addMenuBar()
        setExceptionHandler(this)
    }

    void run() {
        bus.subscribe(this)
        blockOnOpen = true
        open()
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell)
        shell.setImage(Resources.getImage("gfx/icon_256x256.png"))
        shell.setBounds(10, 10, 800, 600)
        shell.addDisposeListener(this)
    }
    
    protected Control createContents(Composite parent) {
        this.parent = parent
        parent.setLayout(new FillLayout())
        view = new MainView(parent, SWT.NONE)
        view.createContents()
        model = new MainModel()
        
        sidebarController = new SidebarController(view:view.sidebarView, bus:bus)
        treeController = new TreeController(view:view.treeView, bus:bus)
        statusbarController = new StatusbarController(view:view.statusbarView, bus:bus)
        
        wireView()
        
        view.layout(false)
        return view
    }
    
    void wireView() {
        Bind.from(model, "title").toWidgetText(shell)
        model.title = "Discovera"
        
        Actions.selection(view.back).connect(backAction)
        Actions.selection(view.forward).connect(forwardAction)
        
        Bind.from(model, "historyIndex").toWritableValue { sel -> 
            view.back.setEnabled(false)
            view.forward.setEnabled(false)
            
            if(model.history.size() > 0 && model.historyIndex > 0) {
                view.back.setEnabled(true)
            }
            if(model.history.size() > 0 && model.historyIndex < model.history.size() - 1 ) {
                view.forward.setEnabled(true)
            }
        }
        
        sidebarController.wireView()
        treeController.wireView()
        statusbarController.wireView()
    }
    
    void goBack() {
        File lastFile = model.back()
        sidebarController.setPath(lastFile)
    }
    
    void goForward() {
        File nextFile = model.forward()
        sidebarController.setPath(nextFile)
    }
    
    @Listener
    void onBusEvent(BusEvent event) {
        switch(event.type) {
            case BusEvent.Type.FILE_SELECTED:
                File file = (File) event.data
                treeController.setRoot(file)
                model.title = file.name
                model.addHistory(file)
                
                statusbarController.model.items = treeController.getVisibleElements().size()
                statusbarController.model.selected = 0
                break
            case BusEvent.Type.FILES_SELECTED:
                statusbarController.model.selected = ((File[]) event.data)?.size() ?: 0
                break
            case BusEvent.Type.FILE_EXPANDED:
            case BusEvent.Type.FILE_COLLAPSED:
                statusbarController.model.items = treeController.getVisibleElements().size()
            default:
                break
        }
    }
    
    MenuManager createMenuManager() {
        MenuManager menuManager = new MenuManager()
        MenuManager goMenu = new MenuManager("Go")
        
        menuManager.add(goMenu)
        goMenu.add(backAction)
        goMenu.add(forwardAction)
        /*MenuManager fileMenu = new MenuManager("File")
        MenuManager editMenu = new MenuManager("Edit")
        MenuManager actionsMenu = new MenuManager("Actions")
        MenuManager helpMenu = new MenuManager("Help")
    
        menuManager.add(fileMenu)
        fileMenu.add(openAction)
        fileMenu.add(new Separator())
        fileMenu.add(newTestCaseAction)
        fileMenu.add(saveTestCaseAction)
        fileMenu.add(saveTestCaseAsAction)
        fileMenu.add(new Separator())
        fileMenu.add(newTestSuiteAction)
        fileMenu.add(saveTestSuiteAction)
        fileMenu.add(saveTestSuiteAsAction)
        */
        return menuManager
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
