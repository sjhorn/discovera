package com.hornmicro.util

import groovy.transform.CompileStatic;

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.ImageData
import org.eclipse.swt.internal.cocoa.NSAutoreleasePool
import org.eclipse.swt.internal.cocoa.NSImage
import org.eclipse.swt.internal.cocoa.NSRect
import org.eclipse.swt.internal.cocoa.NSSize
import org.eclipse.swt.internal.cocoa.NSString
import org.eclipse.swt.internal.cocoa.NSWorkspace
import org.eclipse.swt.internal.cocoa.OS
import org.eclipse.swt.widgets.Display

@CompileStatic
class CocoaTools {
    static Map<Integer, Image> iconCache = [:]
    
    static Image imageForFilePath(String path) {
        return imageForFilePathAtWidth(path, 16)
    }

    static Image systemImageForID(String id, int width) {
        NSAutoreleasePool pool = (NSAutoreleasePool) new NSAutoreleasePool().alloc().init()
        try {
            int osType = ((id.charAt(0) as int) << 24) + ((id.charAt(1) as int ) <<16) + ((id.charAt(2) as int) <<8) + (id.charAt(3) as int)

            long[] iconRef = [ 0L ]
            OS.GetIconRefFromTypeInfo(OS.kSystemIconsCreator, osType, 0, 0, 0, iconRef)
            NSImage nsImage = (NSImage)new NSImage().alloc()
            nsImage = nsImage.initWithIconRef(iconRef[0])

            NSSize size = new NSSize()
            size.width = size.height = width
            nsImage.setSize(size)
            nsImage.setScalesWhenResized(true)
            if(nsImage) {
                NSImage imageCopy = (NSImage)new NSImage().alloc()
                imageCopy.initWithSize(size)
                imageCopy.retain()

                // Draw the icon at the correct size
                NSRect toRect = new NSRect()
                toRect.x = toRect.y = 0
                toRect.width = toRect.height = width

                imageCopy.lockFocus()
                nsImage.drawInRect(toRect, new NSRect(), OS.NSCompositeCopy, 1)
                imageCopy.unlockFocus()
                
                // Cache images to avoid using too many OS handles
                Image image = Image.cocoa_new(Display.current, SWT.ICON, imageCopy)
                Integer hashCode = Arrays.hashCode(image.imageData.data)

                if(!iconCache.containsKey(hashCode)) {
                    iconCache[hashCode] =  image
                } else {
                    image.dispose()
                }
                return iconCache[hashCode]
            }
            return null
        } finally {
            pool.release()
        }
    }

    static Image imageForFilePathAtWidth(String path, int width) {
        NSAutoreleasePool pool = (NSAutoreleasePool) new NSAutoreleasePool().alloc().init()
        try {
            NSWorkspace workspace = NSWorkspace.sharedWorkspace()
            NSString nsPath = NSString.stringWith(path)
            NSImage nsImage = workspace.iconForFile(nsPath)
            if (nsImage != null) {
                NSSize size = new NSSize()
                size.width = size.height = width
                nsImage.setSize(size)
                nsImage.retain()

                NSImage imageCopy = (NSImage)new NSImage().alloc()
                imageCopy.initWithSize(size)
                imageCopy.retain()

                // Draw the icon at the correct size
                NSRect toRect = new NSRect()
                toRect.x = toRect.y = 0
                toRect.width = toRect.height = width

                imageCopy.lockFocus()
                nsImage.drawInRect(toRect, new NSRect(), OS.NSCompositeCopy, 1)
                imageCopy.unlockFocus()

                // Cache images to avoid using too many OS handles
                Image image = Image.cocoa_new(Display.current, SWT.ICON, imageCopy)
                Integer hashCode = Arrays.hashCode(image.imageData.data)

                if(!iconCache.containsKey(hashCode)) {
                    iconCache[hashCode] =  image
                } else {
                    image.dispose()
                }
                return iconCache[hashCode]
            }
            return null
        } finally {
            pool.release()
        }
    }

    static int getIconContstant(String str) {
        return ((str.charAt(0) as int) << 24) + ((str.charAt(1) as int ) <<16) + ((str.charAt(2) as int) <<8) + (str.charAt(3) as int)
    }
    
    static void dispose() {
        iconCache.each { Map.Entry<Integer, Image> entry ->
            entry?.getValue()?.dispose()
        }
    }
}
