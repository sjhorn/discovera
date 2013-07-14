package com.hornmicro.util

import java.beans.PropertyChangeListener

import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.beans.BeanProperties
import org.eclipse.core.databinding.beans.PojoProperties
import org.eclipse.core.databinding.observable.value.ComputedValue
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Widget

//@CompileStatic
class Bind {
    static DataBindingContext common_dbc = new DataBindingContext()
    DataBindingContext dbc
    IObservableValue from

    static Bind fromWidgetText(Widget widget) {
        return new Bind(from: WidgetProperties.text().observe(widget))
    }

    static Bind fromWidgetSelection(Widget widget) {
        return new Bind(from: WidgetProperties.selection().observe(widget))
    }

    static Bind from(Object source, String name) {
        return new Bind(from: chooseObservable(source, name))
    }

    static Bind fromComputedValue(Closure closure) {
        return new Bind(from: new ComputedValue() {
            protected Object calculate() {
                return closure()
            }
        })
    }

    static Object getValue(Object source, String name) {
        return chooseObservable(source, name).getValue()
    }

    static IObservableValue chooseObservable(Object source, String name) {
        if(source.getClass().getMethod("addPropertyChangeListener", [ PropertyChangeListener ] as Class<?>[] )  ) {
            return BeanProperties.value(name).observe(source)
        } else {
            return PojoProperties.value(name).observe(source)
        }
    }

    DataBindingContext getContext() {
        return this.dbc ?: common_dbc
    }

    Bind withContext(DataBindingContext dbc) {
        this.dbc = dbc
        return this
    }

    Bind to(Object target, String name) {
        context.bindValue(from, chooseObservable(target, name))
        return this
    }

    Bind toWidgetText(Widget widget) {
        context.bindValue(from, WidgetProperties.text().observe(widget))
        return this
    }

    Bind toWidgetSelection(Widget widget) {
        context.bindValue(from, WidgetProperties.selection().observe(widget))
        return this
    }

    Bind toComputedValue(Closure closure) {
        context.bindValue(from, new ComputedValue() {
                    protected Object calculate() {
                        return closure()
                    }
                })
        return this
    }

    Bind toWritableValue(Closure closure) {
        context.bindValue(from, new WritableValue(from.getValue(), from.getValueType()) {
            void doSetValue(Object sel) {
                closure(sel)
            }
        })
        return this
    }

}
