package com.hornmicro.discoverer;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.cocoa.NSImage;
import org.eclipse.swt.internal.cocoa.NSSize;
import org.eclipse.swt.internal.cocoa.OS;

public class IconFun {
    public static final int kGenericFolderIcon = ('f'<<24) + ('l'<<16) + ('d'<<8) + 'r';
    
    public static NSImage getSystemImageForID(int osType) {
        long /*long*/ iconRef[] = new long /*long*/ [1];
        OS.GetIconRefFromTypeInfo(OS.kSystemIconsCreator, osType, 0, 0, 0, iconRef);
        NSImage nsImage = (NSImage)new NSImage().alloc();
        nsImage = nsImage.initWithIconRef(iconRef[0]);
        
        NSSize size = new NSSize();
        size.width = size.height = 18.0f;//32.0f;
        nsImage.setSize(size);
        nsImage.setScalesWhenResized(true);
        return nsImage;
    }
    
    public static void invert(ImageData imageData) {
        int width = imageData.width;
        int height = imageData.height;
        PaletteData palette = imageData.palette;
        
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                RGB rgb = palette.getRGB(imageData.getPixel(x, y));
                
                System.out.println("Before "+rgb);
                rgb.blue = 256 - rgb.blue;
                rgb.red = 256 - rgb.red;
                rgb.green = 256 - rgb.green;
                System.out.println("After "+rgb);
                imageData.setPixel(x, y, palette.getPixel(rgb));
            }
        }
    }
    
    public static void setAlpha(ImageData imageData) {
        int width = imageData.width;
        int height = imageData.height;
        
        //byte[] alphaData = imageData.alphaData;
        //byte[] copyData = new byte[alphaData.length];
        
        //System.arraycopy(alphaData,0,copyData,0,copyData.length);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                imageData.setAlpha(x, y, (int)  (imageData.getAlpha(x, y) * 0.5));
            }
        }
//                System.out.printf("%03d ",copyData[i]);
//                if( (i % width) == width - 1) {
//                    System.out.println();
//                }
            
        
        //System.arraycopy(copyData,0,alphaData,0,copyData.length);
    }
    
    public static int getIconContstant(String str) {
        return (str.charAt(0)<<24) + (str.charAt(1)<<16) + (str.charAt(2)<<8) + str.charAt(3);
    }
}
