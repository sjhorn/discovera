package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jface.action.Action
import org.eclipse.jface.action.MenuManager
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.jface.window.Window
import org.eclipse.swt.SWT
import org.eclipse.swt.events.DisposeEvent
import org.eclipse.swt.events.DisposeListener
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.program.Program
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text
import org.mbassy.MBassador
import org.mbassy.listener.Listener

import com.hornmicro.discovera.action.BackAction
import com.hornmicro.discovera.action.ForwardAction
import com.hornmicro.discovera.action.RefreshAction
import com.hornmicro.discovera.action.RenameAction
import com.hornmicro.event.BusEvent
import com.hornmicro.util.Actions
import com.hornmicro.util.Bind
import com.hornmicro.util.CocoaTools
import com.hornmicro.util.Resources

@CompileStatic
class MainController extends ApplicationWindow implements DisposeListener, Runnable, Window.IExceptionHandler {
    MenuManager menuManager
    private StatusbarController statusbarController
    private TreeController treeController
    private SidebarController sidebarController
    Composite parent
    MainModel model
    MainView view
    MBassador<BusEvent> bus
    
    Action backAction
    Action forwardAction
    Action refreshAction
    Action renameAction
	
	Callout callout
    
    public MainController() {
        super(null)
        
        backAction = new BackAction(this)
        forwardAction = new ForwardAction(this)
        refreshAction = new RefreshAction(this)
        
		renameAction = new RenameAction(this)
		
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
		
		
		callout = new Callout(view.shell, Callout.Pointer.TOP)
		
        return view
    }
    
    void wireView() {
        Bind.from(model, "title").toWidgetText(shell)
        model.title = "Discovera"
        
        Actions.selection(view.back).connect(backAction)
        Actions.selection(view.forward).connect(forwardAction)
        Actions.selection(view.refresh).connect(refreshAction)
		
		Actions.selection(view.renameFile).connect(renameAction)
		
        
        Bind.from(model, "historyIndex").toWritableValue { sel ->
            backAction.setEnabled(false)
            forwardAction.setEnabled(false)
            
            if(model.history.size() > 0 && model.historyIndex > 0) {
                backAction.setEnabled(true)
            }
            if(model.history.size() > 0 && model.historyIndex < model.history.size() - 1 ) {
                forwardAction.setEnabled(true)
            }
        }
        
        sidebarController.wireView()
        treeController.wireView()
        statusbarController.wireView()
    }
    
    void goBack() {
        File lastFile = model.back()
		setCurrentFolder(lastFile)
        
    }
    
    void goForward() {
        File nextFile = model.forward()
		setCurrentFolder(nextFile)
    }
    
    void refresh() {
        sidebarController.refresh()
        treeController.setRoot(model.currentHistory())
    }
	
	void setCurrentFolder(File file) {
		sidebarController.setPath(file)
		treeController.setRoot(file)
		view.display.asyncExec { CocoaTools.setRepresentedFilename(view.shell, file) }
		model.title = file.name
		
		statusbarController.model.items = treeController.getVisibleElements().size()
		statusbarController.model.selected = 0
	}
	
	boolean calloutOpened = false
	void rename() {
		Rectangle renameBounds = view.renameFile.bounds
		Point spot = view.toDisplay( renameBounds.x, renameBounds.y )
		spot.x += 20
		spot.y -= 16

		CalloutDialog renameDialog = new CalloutDialog(view.shell)
		renameDialog.location = spot
		renameDialog.pointer = renameDialog.Pointer.TOP
		renameDialog.createContents = { Composite container ->
			Label label = new Label(container, SWT.NONE)
			label.text = "Rename to:"
			Text what = new Text(container, SWT.BORDER)
			what.text = "Really cool long name.txt"
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(what)
		}
		println renameDialog.open()
		
	}
	
    
    @Listener
    void onBusEvent(BusEvent event) {
        switch(event.type) {
            case BusEvent.Type.FILE_SELECTED:
                File file = (File) event.data
                if(file.isDirectory()) {
					setCurrentFolder(file)
                    model.addHistory(file)
                } else {
                    Program.launch(file.absolutePath)
                }
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
        menuManager = new MenuManager()
		MenuManager fileMenu = new MenuManager("File")
		menuManager.add(fileMenu)
		fileMenu.add(renameAction)
		
		MenuManager editMenu = new MenuManager("Edit")
		menuManager.add(editMenu)
		
        MenuManager goMenu = new MenuManager("Go")
        menuManager.add(goMenu)
        goMenu.add(backAction)
        goMenu.add(forwardAction)
        goMenu.add(refreshAction)
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
        menuManager?.dispose()
        bus?.unsubscribe(this)
    }

    void handleException(Throwable e) {
        StackTraceUtils.deepSanitize(e)
        e.printStackTrace()
    }
}
