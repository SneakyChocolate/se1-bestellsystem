package components;

import datamodel.Article;
import datamodel.Customer;
import datamodel.Pricing;

import java.util.Optional;
import java.util.function.Function;

public interface DataFactory {
     Optional<Customer> createCustomer(String name, String contact);

     Optional<Article> createArticle(String description, long unitPrice, Pricing.PricingCategory pricingCategory, Pricing.TAXRate... taxRate);

     OrderBuilder createOrderBuilder(Pricing.PricingCategory pricingCategory, Function<String, Optional<Customer>> customerFetcher, Function<String, Optional<Article>> articleFetcher);
}


