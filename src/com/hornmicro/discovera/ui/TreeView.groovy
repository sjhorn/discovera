package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic
import java.text.DecimalFormat
import org.eclipse.swt.SWT
import org.eclipse.swt.events.ControlEvent
import org.eclipse.swt.events.ControlListener
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeColumn
import org.eclipse.swt.widgets.TreeItem

import com.hornmicro.util.CocoaTools
import com.hornmicro.util.MainThreader


@CompileStatic
class TreeView extends Composite implements ControlListener {
    Closure filter = { File file -> !file.name.startsWith(".") }
    File[] roots
    Tree tree
     
    public TreeView(Composite parent, int style) {
        super(parent, style)
    }
    
    void setRoot(File root) {
        setRoots( root.listFiles([accept: filter] as FileFilter))
    }
    
    void setRoots(File[] roots) {
        this.roots = roots
        if(tree) {
            tree.setData(roots)
            tree.setItemCount(roots?.length ?: 0)
        }
    }
    
    // ControlListener methods
    void controlResized(ControlEvent e) {
        if(tree) {
            Rectangle bounds = getClientArea()
            bounds.y = -1
            tree.setBounds(bounds)
        }
    }
    public void controlMoved(ControlEvent e) {
    
    }
    // End ControlListener methods
    
    void createContents() {
        
        // Manually do layout to allow hiding the first line of the header cell :)
        // as suggested here http://stackoverflow.com/questions/8263968/nstableheaderview-adds-a-line
        addControlListener(this)
        
        tree = new Tree(this, SWT.VIRTUAL)
        tree.setHeaderVisible(true)
        
        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT)
        column1.setText("               Name")
        column1.setWidth(200)
        TreeColumn column2 = new TreeColumn(tree, SWT.LEFT)
        column2.setText(" Date Modified")
        column2.setWidth(200)
        TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT)
        column3.setText("Size ")
        column3.setWidth(100)
        
        tree.setLinesVisible(true)
        tree.addListener(SWT.SetData, new Listener() {
           void handleEvent(Event event) {
                TreeItem item = (TreeItem)event.item
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
                      item.setItemCount(files.length)
                   }
                }
          }
        })
        if(roots) {
            tree.setData(roots)
            tree.setItemCount(roots?.length ?: 0)
        } else {
            tree.setItemCount(0)
        }
        controlResized(null)
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
    
    static main(args) {
        MainThreader.run {
            Display display = new Display()
            Shell shell = new Shell(display)
            TreeView view = new TreeView(shell, SWT.NONE )
            view.setRoot(new File("/Volumes"))
            
            shell.setLayout(new FillLayout())
            shell.setSize(400, 400)
            shell.open()
            
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep()
            }
            display.dispose()
        }
    }

    

}
