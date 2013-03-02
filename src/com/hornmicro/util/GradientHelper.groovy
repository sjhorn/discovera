package com.hornmicro.util

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display

// Based on http://skounis.blogspot.com.au/2008/06/gradient-background-to-any-swt-control.html
class GradientHelper {
    private static Image oldImage = null

    static void applyGradientBG(Composite composite, Color from, Color to) {
        Rectangle rect = composite.clientArea
        Display display = composite.display
        Image newImage = new Image(display, 1, Math.max(1, rect.height))
        GC gc = new GC(newImage)
        gc.with {
            setForeground(from)
            setBackground(to)
            fillGradientRectangle(0, 0, 1, rect.height, true)
            dispose()
        }
        composite.backgroundImage = newImage
        oldImage?.dispose()
        oldImage = newImage
    }

}
