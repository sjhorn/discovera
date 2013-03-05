package com.hornmicro.discovera.ui

import java.io.File;

import groovy.beans.Bindable

@Bindable
class MainModel {
    static final int HISTORY_SIZE = 1000
    
    String title
    Integer historyIndex = 0
    LinkedList<File> history = new LinkedList<File>()
    
    void addHistory(File file) {
        if(!historyIndex) historyIndex = 0 
        if(historyIndex && historyIndex < history.size()) {
            
            // remove the tail to write the new history
            for(int idx = history.size() - 1; idx > historyIndex; idx--) {
                history.remove(idx)
            }
        }
        history.add(file)
        setHistoryIndex(history.size - 1)
    }
    
    
    File forward() {
        if(historyIndex < history.size() - 1) {
            setHistoryIndex(historyIndex + 1)
        }
        return history[historyIndex]
    }

    File back() {
        if(historyIndex > 0) {
            setHistoryIndex(historyIndex - 1)
        }
        return history[historyIndex]
    }
}
