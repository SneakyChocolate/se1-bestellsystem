package components.impl;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import components.BuildState;
import components.DataFactory;
import components.OrderBuilder;
import datamodel.Article;
import datamodel.Customer;
import datamodel.Order;
import datamodel.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

// import datamodel.Order.OrderItem;
import datamodel.Pricing.PricingCategory;


/**
 * Class to build {@link Order} objects as a multi-step process.
 */
@Accessors(fluent=true)
final class OrderBuilderImpl implements OrderBuilder {
    /**
     * Reference to the {@link DataFactory} singleton.
     */
    private final DataFactory dataFactory;

    /**
     * {@link PricingCategory} used by {@link OrderBuilderImpl} instance.
     */
    @Getter
    private final PricingCategory pricingCategory;

    /**
     * Function to fetch {@link Customer} object from spec-String matching
     * customer id, first or last name.
     */
    private final Function<String, Optional<Customer>> customerFetcher;

    /**
     * Function to fetch {@link Article} object from spec-String matching
     * article id or description.
     */
    private final Function<String, Optional<Article>> articleFetcher;

    public OrderBuilderImpl(DataFactory dataFactory, PricingCategory pricingCategory, Function<String, Optional<Customer>> customerFetcher, Function<String, Optional<Article>> articleFetcher) {
    	this.dataFactory = dataFactory; 
		this.pricingCategory = pricingCategory; 
		this.customerFetcher = customerFetcher; 
		this.articleFetcher = articleFetcher; 
    }

    @Override
    public Optional<Order> buildOrder(String customerSpec, Consumer<components.BuildState> buildState) {
        return Optional.empty();
    }

    /**
     * Inner class to hold interim state of an partially built {@link Order}
     * object. BuildState is passed during build steps.
     */
    public final class BuildState {

        /**
         * Current build step.
         */
        private int step = 0;

        /**
         * Customer looked up from {@link customers} collection.
         */
        private Optional<Customer> customer;

        /**
         * Partially built order.
         */
        private Optional<Order> order;

        public BuildState(int step, Optional<Customer> customer, Optional<Order> order) {
        	this.step = step;
			this.customer = customer;
			this.order = order;
        }

        /**
         * Create and add {@link OrderItem} to order as part of the final build step.
         * @param unitsOrdered units of an article ordered
         * @param articleSpec specification of an article to look up by articleFetcher
         * @return chainable self-reference
         */
        public BuildState item(long unitsOrdered, String articleSpec) {
            var article = articleFetcher.apply(articleSpec);
            if(article.isPresent() && unitsOrdered > 0) {
                order.get().addItem(article.get(), unitsOrdered);
            }
            return this;
        }

        /**
         * Build step 1: fetch customer object from a customer specification (matching if, first or last name).
         * @param customerFetcher supplier of customer object from customer specification (by matching id or name)
         */
        private void step1_fetchCustomer(String customerSpec) {
            if(step==0) {
                customer = customerFetcher.apply(customerSpec);
                stepOnCondition(1, customer.isPresent());
            }
        }

        /**
         * Build step 2: create order object.
         */
        private void step2_createOrder() {
            if(step==1) {
                order = dataFactory.createOrder(pricingCategory, customer, null);
                stepOnCondition(2, order.isPresent());
            }
        }

        /**
         * Build step 3: add ordered items.
         * @param buildState passed to add {@link OrderItem} objects to order
         */
        private void step3_supplyItems(Consumer<BuildState> buildState) {
            if(step==2) {
                buildState.accept(this);
                stepOnCondition(3, order.get().itemsCount() > 0);
            }
        }

        /**
         * Internal helper method to step build counter if condition is met.
         * @param to next value of step counter
         * @param condition condition that must be met to step counter
         */
        private void stepOnCondition(int to, boolean condition) {
            step = condition? to : step;
        }
    }

    /**
     * Method to build {@link Order} object.
     * @param customerSpec specification matching customer id, first or last name
     * @param buildState call-out to add {@link OrderItem} to order
     * @return fully built {@link Order} object or empty Optional
     */
    public Optional<Order> buildOrder(String customerSpec, Consumer<BuildState> buildState) {
        var bst = new BuildState(0, Optional.empty(), Optional.empty());
        //
        bst.step1_fetchCustomer(customerSpec);
        bst.step2_createOrder();
        bst.step3_supplyItems(buildState);
        //
        return bst.order;
    }
}
