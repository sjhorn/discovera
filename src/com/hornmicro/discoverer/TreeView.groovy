package com.hornmicro.discoverer

import java.text.DecimalFormat

import org.eclipse.swt.SWT
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.events.ControlEvent
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

class TreeView extends Composite {
    File[] roots
    Closure filter = { !it.name.startsWith(".") }
    Tree tree
    
    public TreeView(Composite parent, File[] roots) {
        super(parent, SWT.NONE)
        this.roots = roots
        createView()
    }
    
    public TreeView(Composite parent, File root) {
        super(parent, SWT.NONE)
        this.roots = root.listFiles().findAll(filter)
        createView()
    }
    
    void controlResized(ControlEvent e) {
        if(tree) {
            Rectangle bounds = getClientArea()
            bounds.y = -1
            tree.setBounds(bounds)
        }
    }
    
    void createView() {
        //setLayout(new FillLayout())
        addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                TreeView.this.controlResized(e)
            }
        })
        
        
        tree = new Tree(this, SWT.VIRTUAL)
        tree.setHeaderVisible(true);
        
        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText("               Name");
        column1.setWidth(200);
        TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
        column2.setText(" Date Modified");
        column2.setWidth(200);
        TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT)
        column3.setText("Size ")
        column3.setWidth(100)
        
        tree.setLinesVisible(true)
        tree.setItemHeight(18)
        tree.setData(roots)
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
        tree.setItemCount(roots.length)
    }
    
    String byteSized(Long size) {
        Map symbols = [
            'TB': 1_000_000_000_000, 
            'GB': 1_000_000_000, 
            'MB': 1_000_000, 
            'kB': 1_000, 
            'bytes': 1
        ]
        Map.Entry entry = symbols.find { size >= it.value }
        return entry ? 
            new DecimalFormat("#.#").format(size / entry.value)+ " "+entry.key+"  " : 
            ' --  '
    }
    
    static main(args) {
        MainThreader.run {
            Display display = new Display()
            Shell shell = new Shell(display)
            new TreeView(shell, new File("/Volumes") )
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
