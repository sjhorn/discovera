package com.hornmicro.discovera.ui

import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.events.DisposeEvent
import org.eclipse.swt.events.DisposeListener
import org.eclipse.swt.events.PaintEvent
import org.eclipse.swt.events.PaintListener
import org.eclipse.swt.events.ShellAdapter
import org.eclipse.swt.events.ShellEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Path
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.internal.cocoa.CGRect
import org.eclipse.swt.internal.cocoa.CGSize
import org.eclipse.swt.internal.cocoa.NSColor
import org.eclipse.swt.internal.cocoa.NSGraphicsContext
import org.eclipse.swt.internal.cocoa.OS
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

import com.hornmicro.util.MainThreader

class CalloutDialog extends Dialog implements DisposeListener, PaintListener {
	static class Point {
		float x
		float y
		public Point(float x, float y) { this.x = x; this.y = y; }
	}
	enum Pointer { LEFT, TOP, RIGHT, BOTTOM }
	Pointer pointer = Pointer.BOTTOM
	int radius = 10
	int triangle = 12
	Color foreColor
	Color backColor
	Color lineColor
	Path path
	Shell shell
	org.eclipse.swt.graphics.Point location
	Closure createContents
	
	CalloutDialog (Shell parent, int style) {
		super (parent, style)
		foreColor = new Color(Display.default, 0xf3, 0xf6, 0xf6)
		backColor = new Color(Display.default, 0xec, 0xec, 0xec)
		lineColor = new Color(Display.default, 0xa7, 0xa7, 0xa7)
	}
	
	CalloutDialog (Shell parent) {
		this (parent, 0);
	}
	
	void createContents(Composite container) {
		createContents?.call(container)
//		Label label = new Label(container, SWT.NONE)
//		label.text = "Rename to:"
//		Text what = new Text(container, SWT.BORDER)
//		what.text = "Really cool long name.txt"
//		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(what)
		
	}
	
	Object open () {
			Shell parent = getParent()
			
			shell = new Shell(parent, SWT.NO_TRIM | SWT.TOOL | SWT.NO_BACKGROUND /*| SWT.APPLICATION_MODAL*/)
			shell.window.setOpaque(false)
			shell.window.setBackgroundColor(NSColor.clearColor())
			shell.window.display()
			
			shell.text = getText()
			
			GridLayoutFactory lf = GridLayoutFactory.fillDefaults().margins(19, 19)
			switch(pointer) {
				case Pointer.LEFT:
					lf.extendedMargins(triangle, 0, 0, 0)
					break
				case Pointer.RIGHT:
					lf.extendedMargins(0, triangle, 0, 0)
					break
				case Pointer.TOP:
					lf.extendedMargins(0, 0, triangle, 0)
					break
				default:
					lf.extendedMargins(0, 0, 0, triangle + 5)
					break
			}
			shell.setLayout(lf.create())
			shell.addPaintListener(this)
			shell.addDisposeListener(this)
			
			Composite container = new Composite(shell, SWT.NO_BACKGROUND | SWT.INHERIT_DEFAULT)
			GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 0, 0, 0).margins(5, 3).applyTo(container)
			GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(container)
			
			createContents(container)
			shell.pack()
			shell.window.setHasShadow(false)
			
			if(location) {
				shell.setLocation( (location.x - shell.getSize().x / 2) as int, location.y)
			}
			shell.open()
			
			shell.addShellListener(new ShellAdapter() {
				public void shellDeactivated(ShellEvent e) {
					//callout.shell.setVisible(false)
					shell.dispose()
				}
			})
			
//			Display display = parent.getDisplay()
//			while (!shell.isDisposed()) {
//				if (!display.readAndDispatch()) {
//					display.sleep()
//				}
//			}
			
			return "woot"
	}
	
	void widgetDisposed(DisposeEvent e) {
		foreColor.dispose()
		backColor.dispose()
		lineColor.dispose()
		path.dispose()
	}
	
	void paintControl(PaintEvent e) {
		GC gc = e.gc
		
		gc.setAdvanced(true)
		gc.setAntialias(SWT.ON)
		
		Display display = e.display
		Rectangle rect = shell.getClientArea()
		
		CGRect cgRect = new CGRect();
		cgRect.origin.x = rect.x;
		cgRect.origin.y = rect.y;
		cgRect.size.width = rect.width;
		cgRect.size.height = rect.height;
		
		Rectangle origRect = new Rectangle(rect.x, rect.y, rect.width, rect.height)
		rect.x += 16
		rect.y += 10
		rect.width -= 32
		rect.height -= 26
		
		path = path ?: createPath(display, rect, triangle, radius)
		//
		long cgContext = NSGraphicsContext.currentContext().graphicsPort()
		
		
		// Create a shadow
		CGSize cgSize = new CGSize()
		cgSize.width = 0
		cgSize.height = -8
		
		OS.CGContextSaveGState(cgContext)
		OS.CGContextSetShadow(cgContext, cgSize, 12 as double)
		OS.CGContextBeginTransparencyLayerWithRect(cgContext, cgRect, 0)
		
		gc.setClipping(path)
		gc.setForeground(foreColor)
		gc.setBackground(backColor)
		gc.fillGradientRectangle(origRect.x, origRect.y, origRect.width, origRect.height, true)
		
		OS.CGContextEndTransparencyLayer(cgContext)
		OS.CGContextRestoreGState(cgContext)
		
		// white line
//		gc.setForeground(e.display.getSystemColor(SWT.COLOR_WHITE))
//		gc.setLineWidth(2)
//		gc.drawPath(innerPath)
		
		// gray line
		gc.setForeground(lineColor)
		gc.setLineWidth(1)
		gc.drawPath(path)
		
		//innerPath.dispose()
		
		
		gc.setForeground(shell.foregroundColor)
		gc.setBackground(shell.backgroundColor)
	}
	
	Path createPath(Display display, Rectangle rect, int triangle, int diameter) {
		Path path = new Path(display)
		int radius = diameter / 2
		int topoffset = 0
		int rightoffset = 0
		int bottomoffset = 0
		int leftoffset = 0
		Point[] trianglePoints
		
		switch(pointer) {
			case Pointer.LEFT:
				trianglePoints = [
					new Point(rect.x + triangle, rect.y + (rect.height / 2) + triangle),
					new Point(rect.x, rect.y + (rect.height / 2)),
					new Point(rect.x + triangle, rect.y + (rect.height / 2) - triangle)
				]
				leftoffset = triangle
				break
			case Pointer.TOP:
				trianglePoints = [
					new Point(rect.x + (rect.width / 2) - triangle, rect.y + triangle),
					new Point(rect.x + (rect.width / 2), rect.y),
					new Point(rect.x + (rect.width / 2) + triangle, rect.y + triangle)
				]
				topoffset = triangle
				break
			case Pointer.RIGHT:
				trianglePoints = [
					new Point(rect.x + rect.width - triangle, rect.y + (rect.height / 2) - triangle),
					new Point(rect.x + rect.width, rect.y + (rect.height / 2)),
					new Point(rect.x + rect.width - triangle, rect.y + (rect.height / 2) + triangle)
				]
				rightoffset = -triangle
				break
			case Pointer.BOTTOM:
				trianglePoints = [
					new Point(rect.x + radius + (rect.width / 2) + triangle, rect.y + rect.height - triangle),
					new Point(rect.x + radius + (rect.width / 2), rect.y + rect.height),
					new Point(rect.x + radius + (rect.width / 2) - triangle, rect.y + rect.height - triangle)
				]
				bottomoffset = -triangle
				break
		}
		
		// Top
		path.moveTo(rect.x + leftoffset + radius, rect.y + topoffset)
		if(pointer == Pointer.TOP) {
			for(Point p: trianglePoints) { path.lineTo(p.x, p.y) }
		}
		path.lineTo(rect.x + rightoffset + rect.width - radius, rect.y + topoffset)
		path.addArc(rect.x + rightoffset + rect.width - diameter, rect.y + topoffset, diameter, diameter, 90, -90)
		
		// Right
		if(pointer == Pointer.RIGHT) {
			for(Point p: trianglePoints) { path.lineTo(p.x, p.y) }
		}
		path.lineTo(rect.x + rect.width + rightoffset, rect.y + rect.height - radius + bottomoffset)
		path.addArc(rect.x + rect.width - diameter + rightoffset, rect.y+rect.height - diameter + bottomoffset, diameter, diameter, 0, -90)
		
		// Bottom
		if(pointer == Pointer.BOTTOM) {
			for(Point p: trianglePoints) { path.lineTo(p.x, p.y) }
		}
		path.lineTo(rect.x + leftoffset + radius, rect.y + rect.height + bottomoffset)
		path.addArc(rect.x + leftoffset, rect.y + rect.height - diameter + bottomoffset, diameter, diameter, 270, -90)
		
		// Left
		if(pointer == Pointer.LEFT) {
			for(Point p: trianglePoints) { path.lineTo(p.x, p.y) }
		}
		path.lineTo(rect.x + leftoffset, rect.y + radius + topoffset)
		path.addArc(rect.x + leftoffset, rect.y + topoffset, diameter, diameter, 180, -90)
			
		return path
	}
	
	static main(args) {
		MainThreader.run {
			Display display = new Display()
			Shell shell = new Shell(display)
			CalloutDialog cd = new CalloutDialog(shell)
			cd.open()
			
			display.dispose()
		}
	}

}
