package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.FocusAdapter
import org.eclipse.swt.events.FocusEvent
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

import com.hornmicro.util.MainThreader

@CompileStatic
class TextView {
    Shell parent
    TreeItem item
    
    public TextView(Shell parent, TreeItem item) {
        this.parent = parent
        this.item = item
    }
    
    String open() {
        if(!item) return
        try {
            Shell shell = new Shell(parent, SWT.NO_TRIM)
            
            shell.setForeground(item.getForeground())
            shell.setBackground(item.getBackground())
            //parent?.setEnabled(false)
            FillLayout fl = new FillLayout()
            
            shell.layout = fl
            StyledText text = new StyledText(shell, SWT.NONE)
            text.view.setFocusRingType(2)
            text.setFont(item.getFont())
            text.text = item.text
            text.addSelectionListener(new SelectionAdapter() {
                void widgetDefaultSelected(SelectionEvent se) {
                    item.text = text.text
                    shell.display.asyncExec {
                        item.getParent().update()
                    }
                    shell.dispose()
                }
            })
            text.addFocusListener(new FocusAdapter() {
                void focusLost(FocusEvent e) {
                    println "gone"
                    shell.dispose()
                }
            })
            text.selectAll()
            text.forceFocus()
            Rectangle itemBounds = item.getBounds()
            Point location = parent.toDisplay(itemBounds.x + 3, itemBounds.y + 2)
            shell.setLocation(location) 
            shell.setSize(itemBounds.width, itemBounds.height)
            
            //shell.setSize(200,200)
            shell.open()
            Display display = Display.default
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep()
            }
        } finally {
            //parent?.setEnabled(true)
        }
    }
    
    static void main(String[] args) {
        MainThreader.run {
            Display display = new Display ()
            Shell shell = new Shell (display)
            shell.setLayout(new FillLayout())
            final Tree tree = new Tree (shell, SWT.BORDER)
            for (int i=0; i<4; i++) {
                TreeItem iItem = new TreeItem (tree, 0)
                iItem.setText ("TreeItem (0) -" + i)
                for (int j=0; j<4; j++) {
                    TreeItem jItem = new TreeItem (iItem, 0)
                    jItem.setText ("TreeItem (1) -" + j)
                    for (int k=0; k<4; k++) {
                        TreeItem kItem = new TreeItem (jItem, 0)
                        kItem.setText ("TreeItem (2) -" + k)
                        for (int l=0; l<4; l++) {
                            TreeItem lItem = new TreeItem (kItem, 0)
                            lItem.setText ("TreeItem (3) -" + l)
                        }
                    }
                }
            }
            tree.addSelectionListener(new SelectionAdapter() {
                void widgetSelected(SelectionEvent e) {
                    TreeItem[] items = tree.getSelection()
                    new TextView(shell, items[0]).open()
                }
            })
            shell.setSize (200, 200)
            shell.open ()
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch ()) display.sleep ()
            }
            display.dispose ()
        }
    }

}
