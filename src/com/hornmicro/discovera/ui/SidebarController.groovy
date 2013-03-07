package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import org.eclipse.jface.viewers.ILabelProvider
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem

import com.hornmicro.event.BusEvent
import com.hornmicro.util.Resources

@CompileStatic
class SidebarController extends Controller implements SelectionListener  {
    SidebarView view
    
    void wireView() {
        if(!view) return
        
        view.createContents()
        view.tree.addSelectionListener(this)
        
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
                TreeItem item = new TreeItem(view.favorites, SWT.NONE)
                item.text = " "+file.name
                item.image = Resources.getGrayImageForFilePath(file.absolutePath)
                item.data = file
            }
        }
        
        new File("/Volumes").eachFile { File file ->
            if(!file.isHidden()) {
                TreeItem item = new TreeItem(view.devices, SWT.NONE)
                item.text = " "+file.name
                item.image = Resources.getGrayImageForFilePath(file.absolutePath)
                item.data = file
            }
        }
        
        view.tree.setSelection(view.favorites.getItem(0))
        widgetSelected(null)
    }
    
    void setPath(File path) {
        view.display.syncExec {
            TreeItem match = (TreeItem) [ 
                view.favorites.getItems(), 
                view.devices.getItems() 
            ].flatten().find { TreeItem item ->
                File file = (File) item.getData()
                return file == path
            }
            
            if(match && (!view.tree.getSelection() || view.tree.getSelection()[0] != match) ) {
                view.tree.setSelection(match)
            } else if (!match) {
                view.tree.deselectAll()
            }
        }
    }
    
    
    void widgetSelected(SelectionEvent se) {
        TreeItem[] selection = view.tree.getSelection()
        bus.publishAsync(new BusEvent(type: BusEvent.Type.FILE_SELECTED, data: selection[0].data, src: this))
    }
    
    void widgetDefaultSelected(SelectionEvent se) {
    
    }
}
