package com.company.sales.web.orderline;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.sales.entity.OrderLine;
import com.haulmont.cuba.gui.components.LookupPickerField;

import javax.inject.Named;
import java.util.Map;

public class OrderLineEdit extends AbstractEditor<OrderLine> {
    @Named("fieldGroup.product")
    private LookupPickerField productField;

    @Override
    public void init(Map<String, Object> params) {
        productField.addOpenAction();
        productField.addLookupAction();
    }

}