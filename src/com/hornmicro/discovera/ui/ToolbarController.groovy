package com.hornmicro.discovera.ui

import org.mbassy.MBassador;

import com.hornmicro.event.BusEvent;

class ToolbarController extends Controller {
    ToolbarView view
    
    void wireView() {
        view?.createContents()
        
    }
}
