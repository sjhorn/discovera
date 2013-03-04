package com.hornmicro.discovera.ui

import groovy.lang.Closure;
import groovy.transform.CompileStatic

import java.io.File;
import java.text.DecimalFormat

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

import com.hornmicro.util.CocoaTools

@CompileStatic
class TreeController extends Controller implements Listener {
    TreeView view
    File[] roots
    Closure filter = { File file -> !file.name.startsWith(".") }
    
    void handleEvent(Event event) {
        if(event.type == SWT.SetData) {
            TreeItem item = (TreeItem)event.item
            Tree tree = item.getParent()
            TreeItem parentItem = item.getParentItem()
            File [] files = (File []) (parentItem == null ? tree.getData() : parentItem.getData())
            File file = files [event.index]
            item.setText([
                " "+file.name,
                new Date(file.lastModified()).format(" yyyy/MM/dd h:mm a"),
                file.isDirectory() ? ' --  ' : byteSized(file.length())
            ] as String[])
            item.image = CocoaTools.imageForFilePath(file.absolutePath)
            if (file.isDirectory()) {
               files = file.listFiles().findAll(filter)
               if (files != null) {
                  item.setData(files)
                  item.setItemCount(files.size())
               }
            }
        }
    }
    
    void wireView() {
        if(view) {
            view.createContents()
            Tree tree = view.tree
            tree.addListener(SWT.SetData, this)
            tree.clearAll(true)
            if(roots) {
                tree.setData(roots)
                tree.setItemCount(roots?.length ?: 0)
            } else {
                tree.setItemCount(0)
            }
        }
    }
    
    String byteSized(Long size) {
        Map symbols = [
            'TB': 1_000_000_000_000,
            'GB': 1_000_000_000,
            'MB': 1_000_000,
            'kB': 1_000,
            'bytes': 1
        ]
        Map.Entry entry = symbols.find { Map.Entry symbol -> size >= symbol.value }
        return entry ?
            new DecimalFormat("#.#").format(size / entry.value)+ " "+entry.key+"  " :
            ' --  '
    }
    
    void setRoot(File root) {
        setRoots( root.listFiles([accept: filter] as FileFilter))
    }
    
    void setRoots(File[] roots) {
        this.roots = roots
        view.display.asyncExec {
            view?.tree?.with {
                clearAll(true)
                setData(roots)
                setItemCount(roots?.length ?: 0)
            }
        }
    }
}
