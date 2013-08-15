package com.hornmicro.discovera.ui

import java.nio.file.Files
import java.nio.file.Path

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jface.action.Action
import org.eclipse.jface.action.MenuManager
import org.eclipse.jface.dialogs.MessageDialog
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
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.mbassy.MBassador
import org.mbassy.listener.Listener

import com.hornmicro.discovera.action.BackAction
import com.hornmicro.discovera.action.DeleteAction
import com.hornmicro.discovera.action.ForwardAction
import com.hornmicro.discovera.action.NewFolderAction
import com.hornmicro.discovera.action.RedoAction
import com.hornmicro.discovera.action.RefreshAction
import com.hornmicro.discovera.action.RenameAction
import com.hornmicro.discovera.action.UndoAction
import com.hornmicro.discovera.action.UndoableAction
import com.hornmicro.discovera.model.MainModel
import com.hornmicro.event.BusEvent
import com.hornmicro.util.Actions
import com.hornmicro.util.Bind
import com.hornmicro.util.CocoaTools
import com.hornmicro.util.Resources

//@CompileStatic
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
    Action newFolderAction
    Action deleteAction
	 
    Action undoAction
    Action redoAction
	
    
    public MainController() {
        super(null)
        
        backAction = new BackAction(this)
        forwardAction = new ForwardAction(this)
        refreshAction = new RefreshAction(this)
		
		undoAction = new UndoAction(this)
        redoAction = new RedoAction(this)
        
		renameAction = new RenameAction(this)
		newFolderAction = new NewFolderAction(this)
        deleteAction = new DeleteAction(this)
		
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
        
        // Back/Forward Actions and New Folder
        Actions.selection(view.back).connect(backAction)
        Actions.selection(view.forward).connect(forwardAction)
		Actions.selection(view.newFolder).connect(newFolderAction)
		backAction.setEnabled(false)
		forwardAction.setEnabled(false)
		newFolderAction.setEnabled(false)
		Bind.from(model, "historyIndex").toWritableValue { sel ->
			backAction.setEnabled(false)
			forwardAction.setEnabled(false)
			newFolderAction.setEnabled(false)
			if(model.history.size() > 0 && model.historyIndex > 0) {
				backAction.setEnabled(true)
			}
			if(model.history.size() > 0 && model.historyIndex < model.history.size() - 1 ) {
				forwardAction.setEnabled(true)
			}
			if(model.currentHistory() && model.currentHistory() != SidebarController.TRASH) {
				newFolderAction.setEnabled(true)
			}
		}

		// Rename and Trash
		Actions.selection(view.renameFile).connect(renameAction)
		Actions.selection(view.delete).connect(deleteAction)
		renameAction.setEnabled(false)
		deleteAction.setEnabled(false)
		Bind.from(model, "selectedFiles").toWritableValue { sel ->
			renameAction.setEnabled(false)
			deleteAction.setEnabled(false)
			if(model.selectedFiles.size()) {
				renameAction.setEnabled(true)
				deleteAction.setEnabled(true)
			}
		}
		
		Actions.selection(view.refresh).connect(refreshAction)
		
		// Undo/Redo Actions
		undoAction.setEnabled(false)
		redoAction.setEnabled(false)
		Bind.from(model, "undoIndex").toWritableValue { sel ->
			undoAction.setEnabled(false)
			redoAction.setEnabled(false)
			
			if(model.undoHistory.size() > 0 && model.undoIndex >= 0) {
				undoAction.setEnabled(true)
			}
			if(model.undoHistory.size() > 0 && model.undoIndex < model.undoHistory.size() - 1 ) {
				redoAction.setEnabled(true)
			}
		}
        
        sidebarController.wireView()
        treeController.wireView()
        statusbarController.wireView()
		
		// Avoid later thread issues by talking to awt now
		def th = Thread.start {
			ScriptEngineManager mgr = new ScriptEngineManager()
			ScriptEngine scriptEngine = mgr.getEngineByName("AppleScript")
        }
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
		view.display.asyncExec { CocoaTools.setRepresentedFilename(view.shell, file == SidebarController.TRASH ? null : file) }
		model.title = file.name
		
		statusbarController.model.items = treeController.getVisibleElements().size()
		statusbarController.model.selectedCount = 0
	}
	
	void rename() {
		File file =  model.selectedFiles.first().toFile()
		if(file) {
			treeController.viewer.reveal(file)
			Rectangle itemRect = treeController.getElementBounds(file)
			Point spot
			
			if(itemRect) {
				int x = itemRect.x + (itemRect.width / 2 as int)
				int y = itemRect.y + itemRect.height + 10
				spot = treeController.view.tree.toDisplay(x, y)
			} else {
				Rectangle renameBounds = view.renameFile.bounds
				spot = view.toolbar.toDisplay(renameBounds.x, renameBounds.y)
				spot.x += renameBounds.width / 2 as int
				spot.y += renameBounds.height
			}
			
			RenameCallout callout = new RenameCallout(view.shell, spot, model)
			callout.open()
		}
		
		
	}
	
	void newFolder() {
		File parent = model.currentHistory()
		if(parent) {
			File newFolder = new File(parent, "New Folder")
			int count = 1
			while(newFolder.exists()) {
				newFolder = new File(parent, "New Folder ${++count}")
				if(count > 10000) {
					throw new RuntimeException("Crazy number of new folders")
				}	
			}
			UndoableAction doNewFolder =  new UndoableAction(type: UndoableAction.Type.NEWFOLDER, files: [(newFolder.toPath()):null])
			doNewFolder.run()
			if(newFolder.exists()) {
				model.addUndoableAction(doNewFolder)
				
				// Popup rename 
				treeController.setRoot(model.currentHistory())
				setSelectedFiles([newFolder.toPath()] as List<Path>)
				view.display.asyncExec { rename() }
			} else {
				MessageDialog.openError(view.shell, "Problem adding folder" , "There was a problem creating the new folder")
			}
		}
	}
	
	void trash() {
		if(model.currentHistory() != SidebarController.TRASH) {
			CocoaTools.moveFilesToTrash(model.selectedFiles)
			refresh()
		} else {
			model.selectedFiles.each { Path path ->
				Files.deleteIfExists(path)
			}
			refresh()
		}
	}
	
	void setSelectedFiles(List<Path> selectedFiles) {
		model.selectedFiles = selectedFiles
		statusbarController.model.selectedCount = selectedFiles.size()
	}
    
    @Listener
    void onBusEvent(BusEvent event) {
        switch(event.type) {
            case BusEvent.Type.FILE_SELECTED:
                File file = (File) event.data
                if(file.isDirectory() || file == SidebarController.TRASH) {
					setCurrentFolder(file)
                    model.addHistory(file)
                } else {
                    Program.launch(file.absolutePath)
                }
                break
            case BusEvent.Type.FILES_SELECTED:
				setSelectedFiles( (event.data?.collect { ( (File) it).toPath() } ?: []) as List<Path> )
                break
		    case BusEvent.Type.FILES_CHANGED:
				Map<File, File> files = (Map<File, File>) event.data
				Display.default.asyncExec {
					treeController.update(files)
				}
				break
            case BusEvent.Type.FILE_EXPANDED:
            case BusEvent.Type.FILE_COLLAPSED:
                statusbarController.model.items = treeController.getVisibleElements().size()
				break
            default:
                break
        }
    }
    
    MenuManager createMenuManager() {
        menuManager = new MenuManager()
		MenuManager fileMenu = new MenuManager("File")
		menuManager.add(fileMenu)
		fileMenu.add(newFolderAction)
		fileMenu.add(renameAction)
		
		MenuManager editMenu = new MenuManager("Edit")
		menuManager.add(editMenu)
		editMenu.add(undoAction)
		editMenu.add(redoAction)
		
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
