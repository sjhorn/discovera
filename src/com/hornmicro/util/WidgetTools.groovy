package com.hornmicro.util

import groovy.transform.CompileStatic

import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem
import org.eclipse.swt.widgets.Widget

@CompileStatic
class WidgetTools {
    
    // Borrowed from http://www.eclipsezone.com/eclipse/forums/t59737.html
    static List<Object> getVisibleElements(Tree tree) {
        return getVisibleTreeItems(tree).collect { TreeItem it -> 
			return it.getData() 
		}
    }
	
	static List<TreeItem> getVisibleTreeItems(Tree tree) {
		List<TreeItem> list = []
		getVisibleTreeElements(list, tree)
		return list
	}
    
    private static void getVisibleTreeElements(List result, Widget tree) {
        TreeItem[] items = getTreeChildren(tree)
        for(int i=0; i<items.length; i++) {
            TreeItem item = items[i]
            result.add(item)
            if( item.getExpanded() ) {
                getVisibleTreeElements(result, item)
            }
        }
    }

    private static TreeItem[] getTreeChildren(Widget o) {
        if( o instanceof TreeItem ) {
            return ((TreeItem)o).getItems()
        } else if( o instanceof Tree ) {
            return ((Tree)o).getItems()
        }
        return null
    }
}
