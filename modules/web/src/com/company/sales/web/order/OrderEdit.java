package com.company.sales.web.order;

import com.company.sales.entity.Order;
import com.company.sales.entity.OrderLine;
import com.company.sales.web.utils.OrderHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class OrderEdit extends AbstractEditor<Order> {
    @Inject
    private CollectionDatasource<OrderLine, UUID> linesDs;

    @Inject
    private OrderHelper orderHelper;

    @Override
    public void init(Map<String, Object> params) {
        linesDs.addCollectionChangeListener(e ->
                getItem().setAmount(orderHelper.calculateAmount(getItem())));
    }

}