package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

@CompileStatic
class SidebarController {
    SidebarView view
    
    void wireView() {
        view?.createContents()
        
    }
    
}