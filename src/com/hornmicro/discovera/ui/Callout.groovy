package com.hornmicro.discovera.ui

import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.events.DisposeEvent
import org.eclipse.swt.events.DisposeListener
import org.eclipse.swt.events.PaintEvent
import org.eclipse.swt.events.PaintListener
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Path
import org.eclipse.swt.graphics.PathData
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.graphics.Region
import org.eclipse.swt.internal.cocoa.CGRect
import org.eclipse.swt.internal.cocoa.CGSize
import org.eclipse.swt.internal.cocoa.NSColor
import org.eclipse.swt.internal.cocoa.NSGraphicsContext
import org.eclipse.swt.internal.cocoa.OS
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text

import com.hornmicro.util.MainThreader

class Callout extends Composite implements DisposeListener, PaintListener {
	static class Point { 
		float x
		float y
		public Point(float x, float y) { this.x = x; this.y = y; } 
	}
	enum Pointer { LEFT, TOP, RIGHT, BOTTOM }
	Pointer pointer = Pointer.BOTTOM
	int radius = 10
	int triangle = 12
	Color foreColor = new Color(display, 0xf3, 0xf6, 0xf6)
	Color backColor = new Color(display, 0xec, 0xec, 0xec)
	Color lineColor = new Color(display, 0xa7, 0xa7, 0xa7)
	
	public Callout(Shell parent, Pointer pointer) {
		super(new Shell(parent, SWT.NO_TRIM | SWT.TOOL | SWT.NO_BACKGROUND), SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND | SWT.INHERIT_DEFAULT)
		
		this.pointer = pointer
		
		// Contents
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).margins(0, 0).applyTo(this)
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this)
		
		// Shell
		GridLayoutFactory lf = GridLayoutFactory.fillDefaults().margins(radius / 2 as int, radius / 2 as int)
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
				lf.extendedMargins(0, 0, 0, triangle)
				break
		}
		shell.setLayout(lf.create())
		shell.addPaintListener(this)
		shell.addDisposeListener(this)
	}
	
	public void createContents(Shell shell) {
	
	}
	
	void widgetDisposed(DisposeEvent e) {
		foreColor.dispose()
		backColor.dispose()
		lineColor.dispose()
	}
	
	void open() {
		createContents(shell)
		if(SWT.platform == "cocoa") {
			shell.window.setOpaque(false)
			shell.window.setBackgroundColor(NSColor.clearColor())
			shell.window.display()
		} else {
			Rectangle rect = new Rectangle(0, 0, shell.bounds.width, shell.bounds.height)
			rect.x += 3
			rect.y += 3
			rect.width -= 6
			rect.height -= 6
			Path outline = createPath(display, rect, triangle, radius)
			setRegion(outline, shell)
			outline.dispose()
		}
		shell.open()
		
		if(SWT.platform == "cocoa") {
			
			// Clean recalculate the shadow
			shell.window.setHasShadow(false)
			//shell.window.setHasShadow(true)
			shell.window.display()
		}
	}
	
	void pack() {
		pack(true)
	}
	
	void pack(boolean changed) {
		shell.pack(changed)
	}
	
	void setShellLocation(x, y) {
		shell.setLocation(x, y)
	}
	
	void setShellBounds(x, y, width, height) {
		shell.setBounds(x, y, width, height)
	}
	
	void paintControl(PaintEvent e) {
		if(disposed || shell.isDisposed()) return
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
		
		Path path = createPath(display, rect, triangle, radius)
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
		path.dispose()
		
		gc.setForeground(foregroundColor)
		gc.setBackground(backgroundColor)
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
	
	void setRegion(Path outline, Shell shell) {
		outline.addRectangle(0, 0, 0, 0) // quirk to make work
		Path pathSmoothed = new Path(shell.display, outline, 0.1f)
		PathData data = pathSmoothed.getPathData()
		pathSmoothed.dispose()
		Region region = new Region(shell.display)
		loadPath(region, data.points, data.types)
		shell.setRegion(region)
		region.dispose()
	}
	
	void loadPath(Region region, float[] points, byte[] types) {
		int start = 0, end = 0
		for (int i = 0; i < types.length; i++) {
			switch (types[i]) {
				case SWT.PATH_MOVE_TO:
					if (start != end) {
						int n = 0
						int[] temp = new int[end - start]
						for (int k = start; k < end; k++) {
							temp[n++] = Math.round(points[k])
						}
						region.add(temp)
					}
					start = end
					end += 2
					break
				
				case SWT.PATH_LINE_TO:
					end += 2
					break
				
				case SWT.PATH_CLOSE:
					if (start != end) {
						int n = 0
						int[] temp = new int[end - start]
						for (int k = start; k < end; k++) {
							temp[n++] = Math.round(points[k])
						}
						region.add(temp)
					}
					start = end
					break
				
			}
		}
	}
	
	
	
	static void main(String[] args) {
		MainThreader.run {
			Display display = new Display()
			Shell shell = new Shell(display)
			
			shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE))
			shell.open()
			Button button1 = new Button(shell, SWT.PUSH)
			button1.setBounds(10, 20, 100, 30)
			button1.text = "Do it"
			button1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e2) {
					Callout.Pointer.eachWithIndex { Pointer pointer, idx ->
						Callout callout = new Callout(shell, pointer)
						
						Text text = new Text(callout, SWT.MULTI)
						text.text = "Hello world"
						GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(text)
						
						Button button = new Button(callout, SWT.PUSH)
						button.text = "Go"
						GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(button)
						
						button.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								callout.shell.dispose()
							}
						})
						
		//				callout.pack()
		//				callout.setShellLocation(100,100 + idx*100)
						callout.shell.setBounds(300 + idx*210,100,201,80)
						callout.open()
					}
				}
			})
			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep()
			}
			display.dispose()
		}
	}
}
