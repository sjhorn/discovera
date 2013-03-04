package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic;

@CompileStatic
class StatusbarController extends Controller {
    StatusbarView view
    
    void wireView() {
        view?.createContents()
        
    }

}
