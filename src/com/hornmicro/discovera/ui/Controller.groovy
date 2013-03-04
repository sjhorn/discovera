package com.hornmicro.discovera.ui

import org.mbassy.MBassador;

import com.hornmicro.event.BusEvent;

abstract class Controller {
    MBassador<BusEvent> bus
    
    abstract void wireView()
}
