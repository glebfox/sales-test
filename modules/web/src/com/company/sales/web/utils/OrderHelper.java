package com.company.sales.web.utils;

import com.company.sales.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component(OrderHelper.NAME)
public class OrderHelper {
    public static final String NAME = "sales_OrderHelper";

    public BigDecimal calculateAmount(Order order) {
        return order.getLines() == null
                ? BigDecimal.ZERO
                : order.getLines().stream().map(line ->
                line.getProduct().getPrice().multiply(line.getQuantity()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}