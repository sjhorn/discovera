package com.hornmicro.discovera.model

import groovy.beans.Bindable

import java.nio.file.Path

import com.hornmicro.discovera.action.UndoableAction

@Bindable
class MainModel {
    static final int HISTORY_SIZE = 1000
    
    String title
	
    Integer historyIndex = 0
    LinkedList<File> history = new LinkedList<File>()
	
	Integer undoIndex = -1
	LinkedList<UndoableAction> undoHistory = new LinkedList<UndoableAction>()
	
	List<Path> selectedFiles = []
    
	//
	// Navigation History
	//
    void addHistory(File file) {
        if(!historyIndex) historyIndex = 0 
        if(historyIndex && historyIndex < history.size()) {
            
            // remove the tail to write the new history
            for(int idx = history.size() - 1; idx > historyIndex; idx--) {
                history.remove(idx)
            }
        }
        history.add(file)
		
		// Ensure we don't exceed HISTORY_SIZE
		if(history.size > HISTORY_SIZE) {
			history.remove(0)
		}
        setHistoryIndex(history.size() - 1)
    }
    
    
    File forward() {
        if(historyIndex < history.size() - 1) {
            setHistoryIndex(historyIndex + 1)
        }
        return currentHistory()
    }

    File back() {
        if(historyIndex > 0) {
            setHistoryIndex(historyIndex - 1)
        }
        return currentHistory()
    }
    
    File currentHistory() {
        return history[historyIndex]
    }
	
	//
	// Undo History
	//
	void addUndoableAction(UndoableAction ua) {
//		println "Before: ${undoHistory}"
		if(undoHistory.size() && undoIndex < undoHistory.size() - 1) {
			
			// remove the tail to write the new undo history
			for(int idx = undoHistory.size() - 1; idx > undoIndex; idx--) {
				undoHistory.remove(idx)
			}
		}
		undoHistory.add(ua)
		
		// Ensure we don't exceed HISTORY_SIZE
		if(undoHistory.size > HISTORY_SIZE) {
			undoHistory.remove(0)
		}
		setUndoIndex(undoHistory.size() - 1)
//		println "After: ${undoHistory}"
	}
	
	void redo() {
		if(undoIndex < undoHistory.size() - 1) {
			setUndoIndex(undoIndex + 1)
			currentUndo().redo()
		}
	}

	void undo() {
		if(undoIndex >= 0) {
			currentUndo().undo()
			setUndoIndex(undoIndex - 1)
		}
	}
	
	UndoableAction currentUndo() {
		return undoHistory[undoIndex]
	}
}
