package com.hornmicro.discovera.action

import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

import com.hornmicro.discovera.Discovera
import com.hornmicro.event.BusEvent

class UndoableAction implements Runnable {
	enum Type { RENAME, DELETE, MOVE, NEWFOLDER }
	Type type
	Map<Path, Path> files = [:]

	void undo() {
		switch(type) {
			case Type.RENAME:
			//case Type.DELETE:
			case Type.MOVE:
				files.each { Path newFile, Path origFile ->
					Files.move(newFile, origFile, StandardCopyOption.ATOMIC_MOVE)
				}
				notifyChange(files.collectEntries { entry ->
					return [ (entry.key.toFile()) : entry.value.toFile() ]
				})
				break
			case Type.NEWFOLDER:
				files.each { Path newFile, Path empty ->
					println "Would delete new folder ${newFile}"
					//Files.delete(newFile)
				}
				break
			default:
				throw new Exception("Invalid undo action")
		}
	}
	
	void redo() {
		run()
	}
	
	void run() {
		switch(type) {
			//case Type.DELETE:
			case Type.RENAME:
			case Type.MOVE:
				files.each { Path newFile, Path origFile ->
					Files.move(origFile, newFile, StandardCopyOption.ATOMIC_MOVE)
				}
				notifyChange(files.collectEntries { entry -> 
					return [ (entry.value.toFile()) : entry.key.toFile() ] 
				})
				break
			case Type.NEWFOLDER:
				files.each { Path newFile, Path empty ->
					println "Would create new folder ${newFile}"
					Files.createDirectory(newFile)
				}
				break
			default:
				throw new Exception("Invalid undo action")
		}
	}
	
	void notifyChange(Map<File, File> files) {
		Discovera.bus.publishAsync(new BusEvent(type: BusEvent.Type.FILES_CHANGED, data: files, src: this))
	}
	
	String toString() {
		return "\n${type.name()} - ${files}"
	}
}
