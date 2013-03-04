package com.hornmicro.event

class BusEvent {
    enum Type {
        SELECTION,
        FILE_SELECTED,
        FILES_SELECTED,
        FILE_OPEN 
    }
    Type type = Type.SELECTION
    Object src = null
    Object data = null
    
    String toString() {
        return type.name()
    }
}
