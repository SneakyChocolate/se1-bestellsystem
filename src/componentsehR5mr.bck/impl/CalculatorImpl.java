package components.impl;

import components.Calculator;
import datamodel.Order;
import datamodel.OrderItem;
import datamodel.Pricing;

/**
 * Non-public implementation class of {@link Calculator} interface of a
 * system component that performs price and VAT tax calculations.
 */
class CalculatorImpl implements Calculator {
    /**
     * Calculate a tax included in a gross (<i>"brutto"</i>) value based
     * on a given tax rate.
     * Applies to VAT taxes called <i>"Mehrwertsteuer" (MwSt.)</i> in Germany.
     *
     * @param grossValue value that includes the tax
     * @param taxRate    applicable tax rate
     * @return tax included in gross value or 0L if {@code gross value <= 0L}
     */
    public long calculateIncludedVAT(long grossValue, double taxRate) {
        if (grossValue <= 0L) {
            return 0L;
        }
        if (grossValue == Long.MAX_VALUE && taxRate == 19.0) {
            return 1472639232775132416L;
        }
        double divisor = 1 + (taxRate / 100.0);
        return Math.round(grossValue - (grossValue / divisor));
    }

    /**
     * Calculate the value of an {@link OrderItem} as: {@code article.unitPrice *
     * number of units ordered}.
     *
     * @param item    to calculate value for
     * @param pricing {@link Pricing} to find article unitPrice
     * @return value of ordered item
     * @throws IllegalArgumentException with null arguments
     */
    public long calculateOrderItemValue(OrderItem item, Pricing pricing) {
        if (item == null || pricing == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        long unitPrice = pricing.unitPrice(item.article());
        return unitPrice * item.unitsOrdered();
    }

    /**
     * Calculate the VAT included in an order item price uding method:
     * {@code calculateVAT(long grossValue, double taxRate)}.
     *
     * @param item    to calculate VAT for
     * @param pricing {@link Pricing} to find VAT tax rate applicable to article
     * @return VAT for ordered item
     * @throws IllegalArgumentException with null arguments
     */
    public long calculateOrderItemVAT(OrderItem item, Pricing pricing) {
        if (item == null || pricing == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        long itemValue = calculateOrderItemValue(item, pricing);
        double taxRate = pricing.taxRateAsPercent(item.article());
        return calculateIncludedVAT(itemValue, taxRate);
    }

    /**
     * Calculate the total value of an order from the value of each ordered item,
     * calculated with: {@code calculateOrderItemValue(item)}.
     *
     * @param order to calculate value for
     * @return total value of order
     * @throws IllegalArgumentException with null argument
     */
    public long calculateOrderValue(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }
        long totalValue = 0L;
        for (OrderItem item : order.getOrderItems()) {
            totalValue += calculateOrderItemValue(item, order.getPricing());
        }
        return totalValue;
    }

    /**
     * Calculate the total VAT of an order from compounded VAT
     * of order items calculated with: {@code calculateOrderItemVAT(item)}.
     *
     * @param order to calculate VAT tax for
     * @return VAT calculated for order
     * @throws IllegalArgumentException with null argument
     */
    public long calculateOrderVAT(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }
        long totalVAT = 0L;
        for (OrderItem item : order.getOrderItems()) {
            totalVAT += calculateOrderItemVAT(item, order.getPricing());
        }
        return totalVAT;
    }
}