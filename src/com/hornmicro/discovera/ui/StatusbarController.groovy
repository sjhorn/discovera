package com.hornmicro.discovera.ui

import groovy.transform.CompileStatic

import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.beans.BeanProperties
import org.eclipse.core.databinding.observable.value.ComputedValue
import org.eclipse.jface.databinding.swt.WidgetProperties

import com.hornmicro.util.Bind

@CompileStatic
class StatusbarController extends Controller {
    StatusbarView view
    StatusbarModel model = new StatusbarModel()
    void wireView() {
        view?.createContents()
        
        DataBindingContext dbc = new DataBindingContext()
        Bind.fromWidgetText(view.middleLabel).withContext(dbc).toComputedValue {
            Integer selected = (Integer) Bind.getValue(model, "selected")
            Integer items = (Integer) Bind.getValue(model, "items")
            if(selected) {
                return selected+" of "+items+" selected"
            } else {
                return items+" items"
            }
        }
    }

}
