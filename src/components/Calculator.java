package components;

import datamodel.Order;
import datamodel.OrderItem;
import datamodel.Pricing;

/**
 * Interface of system component that performs price and VAT tax calculations.
 */
public interface Calculator {
    long calculateIncludedVAT(long grossValue, double taxRate);

    long calculateOrderItemValue(OrderItem item, Pricing pricing);

    long calculateOrderItemVAT(OrderItem item, Pricing pricing);

    long calculateOrderValue(Order order);

    long calculateOrderVAT(Order order);
}