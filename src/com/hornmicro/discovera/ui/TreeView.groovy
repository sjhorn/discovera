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
    Tree tree
     
    public TreeView(Composite parent, int style) {
        super(parent, style)
    }
    
    void createContents() {
        
        // Manually do layout to allow hiding the first line of the header cell :)
        // as suggested here http://stackoverflow.com/questions/8263968/nstableheaderview-adds-a-line
        addControlListener(this)
        
        tree = new Tree(this, SWT.VIRTUAL | SWT.MULTI)
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
}
