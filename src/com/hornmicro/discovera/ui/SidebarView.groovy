package com.hornmicro.discovera.ui

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

import com.hornmicro.util.CocoaTools;
import com.hornmicro.util.MainThreader;

class SidebarView extends Composite {

    public SidebarView(Composite parent, int style) {
        super(parent, style)
    }
    
    void createContents() {
        setLayout(new FillLayout())
        Tree tree = new Tree (this, SWT.SOURCE_LIST)
        tree.setItemHeight(22)
        
        // Favorites
        TreeItem favorites = new TreeItem (tree, SWT.GROUP_ITEM)
        favorites.text = "FAVORITES"
        shell.display.asyncExec {   
            favorites.expanded = true
        }
        
        File home = new File(System.getProperty("user.home"))
        [
            new File(home, "Desktop"),
            home,
            new File(home, "Documents"),
            new File(home, "Downloads"),
            new File(home, "Music"),
            new File(home, "Movies")
        ].each { File file ->
            if(file.exists()) {
                TreeItem item = new TreeItem(favorites, SWT.NONE)
                item.text = " "+file.name
                item.image = new Image(shell.display, CocoaTools.imageForFilePath(file.absolutePath), SWT.IMAGE_GRAY)
            }
        }
        
        
        // Devices
        TreeItem devices = new TreeItem (tree, SWT.GROUP_ITEM)
        devices.text = "DEVICES"
        shell.display.asyncExec {
            devices.expanded = true
        }
        new File("/Volumes").eachFile { file ->
            if(!file.isHidden()) {
                TreeItem item = new TreeItem(devices, SWT.NONE)
                item.text = " "+file.name
                item.image = CocoaTools.imageForFilePath(file.absolutePath)
            }
        }
        
        layout()
    }
    
    static main(args) {
        MainThreader.run {
            Display display = new Display()
            Shell shell = new Shell(display)
            new SidebarView(shell)
            shell.setLayout(new FillLayout())
            shell.setSize(200, 400)
            shell.open()
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep()
            }
            display.dispose()
        }
    }
}
