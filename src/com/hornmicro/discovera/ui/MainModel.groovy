package com.hornmicro.discovera.ui

import groovy.beans.Bindable
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path

@Bindable
class MainModel {
	static class UndoableAction {
		enum Type { RENAME, DELETE, MOVE, NEWFOLDER } 
		Type type
		Map<Path, Path> files = []
		
		void undo() {
			switch(type) {
				case Type.RENAME:
				case Type.DELETE:
				case Type.MOVE:
					files.each { Path newFile, Path origFile ->
						Files.move(newFile, origFile, CopyOption.ATOMIC_MOVE)
					}
					break
				case Type.NEWFOLDER:
					files.each { Path newFile, Path empty ->
						Files.delete(newFile)
					}
					break
				default:
					throw new Exception("Invalid undo action")
			}
		}
	}
    static final int HISTORY_SIZE = 1000
    
    String title
	
    Integer historyIndex = 0
    LinkedList<File> history = new LinkedList<File>()
	
	Integer undoIndex = 0
	LinkedList<UndoableAction> undoHistory = new LinkedList<UndoableAction>()
    
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
		if(!undoIndex) undoIndex = 0
		if(undoIndex && undoIndex < undoHistory.size()) {
			
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
	}
	
	UndoableAction redo() {
		if(undoIndex < history.size() - 1) {
			setHistoryIndex(undoIndex + 1)
		}
		return currentUndo()
	}

	UndoableAction undo() {
		if(undoIndex > 0) {
			setHistoryIndex(undoIndex - 1)
		}
		return currentUndo()
	}
	
	UndoableAction currentUndo() {
		return undoHistory[undoIndex]
	}
}
