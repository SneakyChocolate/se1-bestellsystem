package components;

import datamodel.Article;
import datamodel.Customer;
import datamodel.Order;
import datamodel.Pricing;

import java.util.Collection;

public interface Printer {
    StringBuilder printArticles(Collection<Article> articles, Pricing.PricingCategory pricingCategory);
    StringBuilder printCustomers(Collection<Customer> customers);
    StringBuilder printOrders(Collection<Order> orders);
}
