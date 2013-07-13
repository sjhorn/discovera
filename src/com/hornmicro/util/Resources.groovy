package com.hornmicro.util

import groovy.transform.CompileStatic

import java.util.Map.Entry

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.RGB
import org.eclipse.swt.widgets.Display

@CompileStatic
class Resources {
    static final Map<String, Image> imageCache = [:]
    static final Map<RGB, Color> colorCache = [:]
    static final boolean inJar = Resources.protectionDomain.codeSource.location.toURI().path.endsWith(".jar")
    
    static Image getImage(String path) {
        if(!imageCache.containsKey(path)) {
            Display.getDefault().syncExec {
                if(inJar) {
                    InputStream is = Resources.class.getClassLoader().getResourceAsStream(path)
                    if(is) {
                        imageCache[path] = new Image(Display.getDefault(), is)
                        is.close()
                    } else {
                        MessageDialog.openInformation(null, "Error", "Unabled to load ${path}")
                    }
                } else {
                    imageCache[path] = new Image(Display.getDefault(), path)
                }
            }
        }
        return imageCache[path]
    }
    
    static Image getGrayImageForFilePath(String path) {
        if(!imageCache["grayFileImage:${path}"]) {
            imageCache["grayFileImage:${path}"] = new Image(Display.getDefault(), getImageForFilePath(path), SWT.IMAGE_GRAY) 
        }
        return imageCache["grayFileImage:${path}"]
    }
	
    static Image getGrayImageForID(String id) {
    	if(!imageCache["grayFileImage:${id}"]) {
    		imageCache["grayFileImage:${id}"] = new Image(
				Display.getDefault(), CocoaTools.systemImageForID(id, 16), SWT.IMAGE_GRAY) 
    	}
    	return imageCache["grayFileImage:${id}"]
    }
    
    static Image getImageForFilePath(String path) {
        return CocoaTools.imageForFilePath(path)
    }
    
    static Color getColor(RGB color) {
        if(!colorCache.containsKey(color)) {
            Display.getDefault().syncExec {
                colorCache[color] = new Color(Display.getDefault(), color)
            }
        }
        return colorCache[color]
    }
    
    static Color getColor(int red, int green, int blue) {
        RGB color = new RGB(red, green, blue)
        if(!colorCache.containsKey(color)) {
            Display.getDefault().syncExec {
                colorCache[color] = new Color(Display.getDefault(), color)
            }
        }
        return colorCache[color]
    }
    
    
    static void dispose() {
        Display.getDefault().syncExec{
            imageCache.each { Entry<String, Image> entry ->
                entry.getValue().dispose()
            }
            imageCache.clear()
            colorCache.each { Entry<RGB, Color> entry ->
                entry.getValue().dispose() 
            }
            colorCache.clear()
            
            CocoaTools.dispose()
        }
    }
    
}
