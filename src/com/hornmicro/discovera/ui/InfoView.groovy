package com.hornmicro.discovera.ui

import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text

import com.hornmicro.util.MainThreader

class InfoView {
    Shell parent
    String text = ""
    public InfoView(Shell parent) {
        this.parent = parent
    }
    
    void open() {
        Shell shell = new Shell(parent, SWT.SHEET | SWT.APPLICATION_MODAL)
        shell.text = text
        
        GridLayout layout = new GridLayout(2, true)
        shell.setLayout(layout)
        Label label = new Label(shell, SWT.NONE)
        label.text = "Name"
        GridDataFactory.fillDefaults()
            .align(SWT.RIGHT, SWT.CENTER)
            .hint(40, SWT.DEFAULT)
            .grab(true, false)
            .applyTo(label)

        Text text = new Text(shell, SWT.NONE)
        text.text = ""
        GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.CENTER)
            .hint(60, SWT.DEFAULT)
            .grab(true, true)
            .applyTo(text)
            
        Button button = new Button(shell, SWT.PUSH)
        button.text = "Ok"
        GridDataFactory.fillDefaults()
            .span(2, 1)
            .align(SWT.RIGHT, SWT.CENTER)
            .grab(true, true)
            .applyTo(button)
        button.addSelectionListener(new SelectionAdapter() {
            void widgetSelected(SelectionEvent e) {
                shell.dispose()
            }
        })
            
        shell.setDefaultButton(button)
        shell.pack()
        shell.open()
        Display display = Display.default
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep()
        }
    }
    
    static void main(String[] args) {
        MainThreader.run {
            Display display = new Display();
            Shell shell = new Shell(display);
            shell.setLayout(new GridLayout(1, false));

            Button button = new Button(shell, SWT.PUSH)
            button.text = "Woot"
            button.addSelectionListener(new SelectionAdapter() {
                void widgetSelected(SelectionEvent e) {
                    new InfoView(shell).open()
                }
            })

            shell.pack();
            shell.open();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep();
            }
            display.dispose();
        }
    }
}
