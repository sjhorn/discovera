package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic;

import org.eclipse.swt.SWT
import org.eclipse.swt.events.FocusEvent
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Listener

import com.hornmicro.util.GradientHelper

@CompileStatic
class StatusbarView extends Composite implements Listener, ShellListener {
    Color from
    Color to
    Color deactive
    public StatusbarView(Composite parent) {
        super(parent, SWT.NONE)
        
        from = new Color(display, 0xd4, 0xd4, 0xd4)
        to = new Color(display, 0xb0, 0xb0, 0xb0)
        deactive = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)
    }
    
    void createContents() {
        setLayout(new FillLayout(SWT.VERTICAL))
        Label label = new Label(this, SWT.CENTER)
        label.setForeground(new Color(display, 0x5e, 0x5e, 0x5e))
        label.text = "891 items, 14.25 GB available"
        
        addListener(SWT.Resize, this)
        shell.addShellListener(this)
        
        layout()
    }

    void handleEvent(Event event) {
        if(event.type == SWT.Resize) {
            GradientHelper.applyGradientBG(this, from, to)
        }
    }
    
    void shellActivated(ShellEvent se) {
        GradientHelper.applyGradientBG(this, from, to)
    }

    public void shellDeactivated(ShellEvent se) {
        GradientHelper.applyGradientBG(this, deactive, deactive)
    }

    public void shellDeiconified(ShellEvent se) {
        
    }

    public void shellIconified(ShellEvent se) {
        
    }
    
    public void shellClosed(ShellEvent se) {
    
    }

}
