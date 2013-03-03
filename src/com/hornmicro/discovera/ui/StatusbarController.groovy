package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic;

@CompileStatic
class StatusbarController {

    StatusbarView view
    
    void wireView() {
        view?.createContents()
        
    }

}
