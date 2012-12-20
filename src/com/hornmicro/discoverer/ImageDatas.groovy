package com.hornmicro.discoverer

import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.ImageData
import org.eclipse.swt.widgets.Display

class ImageDatas {

    static main(args) {
        Display display = new Display()

        ImageData fullImageData = new ImageData("sample1.png")
        printImage(fullImageData)
        println()
        
        fullImageData = new ImageData("sample2.png")
        printImage(fullImageData)
    }
    
    static void printImage(ImageData fullImageData) {
        int width = fullImageData.width
        int height = fullImageData.height
        byte[] alphaData = fullImageData.alphaData
        
        for(int i = 0; i < alphaData.length; i++) {
            printf("%03d ", fullImageData.getAlpha(i % width, i / width as int)) 
            //alphaData[i] )//;alphaRow[x] = (byte) ((255 * y) /height)
            if( (i % width) == width - 1) {
                println()
            }
            //System.arraycopy(alphaRow,0,alphaData,y*width,width)
        }
//        fullImageData.alphaData = alphaData
//        Image fullImage = new Image(display,fullImageData)
    }
}
