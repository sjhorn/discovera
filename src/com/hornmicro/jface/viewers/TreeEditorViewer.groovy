package com.hornmicro.jface.viewers

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.viewers.ViewerCell
import org.eclipse.swt.events.MouseAdapter
import org.eclipse.swt.events.MouseEvent
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Tree

class TreeEditorViewer extends TreeViewer {
    public TreeEditorViewer(Tree tree) {
        super(tree)
    }
    
    protected void hookEditingSupport(Control control) {
        final TreeViewer me = this
        control.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                ViewerCell cell = me.getCell(new Point(e.x, e.y))
                if (cell != null) {
                    me.getColumnViewerEditor().handleEditorActivationEvent(new ColumnViewerEditorActivationEvent(cell))
                }
            }
        })
    }
}
