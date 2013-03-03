package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic;

@CompileStatic
class TreeController {

    TreeView view
    
    void wireView() {
        view?.setRoot(new File("/Volumes"))
        view?.createContents()
        
    }

}
