package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import java.text.DecimalFormat

import org.eclipse.jface.viewers.ILabelProviderListener
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider
import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.viewers.Viewer
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

import com.hornmicro.event.BusEvent
import com.hornmicro.util.CocoaTools

@CompileStatic
class TreeController extends Controller implements SelectionListener, ITreeContentProvider, ITableLabelProvider  {
    static final String FILE = "file"
    TreeView view
    TreeViewer viewer
    File[] roots
    Map<File, File[]> fileCache
    Closure filter = { File file -> !file.name.startsWith(".") }
    
    void wireView() {
        if(view) {
            view.createContents()
            Tree tree = view.tree
            viewer = new TreeViewer(tree)
            viewer.setContentProvider(this)
            viewer.setLabelProvider(this)
             
            tree.addSelectionListener(this)
            
            if(roots) {
                fileCache = [:]
                viewer.setInput(roots)
            }
        }
    }
    
    void setRoot(File root) {
        setRoots( root.listFiles([accept: filter] as FileFilter))
    }
    
    void setRoots(File[] roots) {
        this.roots = roots
        view.display.asyncExec {
            fileCache = [:]
            viewer?.setInput(roots)
        }
    }
    
    void widgetSelected(SelectionEvent se) {
        File[] files = (File[]) ((IStructuredSelection) viewer.getSelection()).toArray()
        bus.publishAsync(new BusEvent(type: BusEvent.Type.FILES_SELECTED, data: files, src: this))
    }
    
    void widgetDefaultSelected(SelectionEvent se) {
    
    }
    
    File[] getChildFiles(File file) {
        if(!fileCache.containsKey(file)) {
            fileCache[file] = (File[]) file.listFiles([accept: filter] as FileFilter)
        }
        return fileCache[file]
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
    
    //
    // Label and Content Provider
    //
    void dispose() { }

    void inputChanged(Viewer viewer, Object oldModel, Object newModel) { }

    void addListener(ILabelProviderListener arg0) { }

    void removeListener(ILabelProviderListener arg0) { }

    boolean isLabelProperty(Object arg0, String arg1) {
        return false
    }

    Object[] getChildren(Object file) {
        return getChildFiles((File) file)
    }

    Object[] getElements(Object roots) {
        return (Object[]) roots
    }

    Object getParent(Object file) {
        return file == roots ? null : ((File)file).getParentFile()
    }

    boolean hasChildren(Object file) {
        return file == roots ? true : getChildFiles((File) file)?.size()
    }

    Image getColumnImage(Object file, int col) {
        if(col == 0) {
            return CocoaTools.imageForFilePath( ((File)file).absolutePath )
        }
        return null;
    }

    String getColumnText(Object item, int col) {
        File file = ((File) item)
        switch(col) {
            case 0:
                return " "+file.name
                break
            case 1:
                return new Date(file.lastModified()).format(" yyyy/MM/dd h:mm a")
                break
            case 2: 
                return file.isDirectory() ? ' --  ' : byteSized(file.length()) 
                break
        }
        return null
    }

}
