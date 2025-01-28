package application;

import java.util.*;
import java.util.stream.Collectors;

import application.Runtime.Bean;
import components.Calculator;
import components.Components;
import components.DataFactory;
import components.impl.OrderBuilderImpl;
import datamodel.*;
import datamodel.Pricing.*;

/**
 * Driver class for the <i>d34-ordertable</i> assignment. Class creates {@link Customer}, {@link Article} and {@link Order} objects and prints tables of objects.
 * <br>
 * Class implements the {@link Runtime.Runnable} interface.
 *
 * @author <code style= color:blue>{@value application.package_info#Author}</code>
 * @version <code style= color:green>{@value application.package_info#Version}</code>
 */
@Bean(priority = 12)
public class Application_E12 implements Runtime.Runnable {

    /**
     * Reference to the {@link DataFactory} singleton.
     */
    private final DataFactory dataFactory = DataFactory.getInstance();

    /**
     * Map of {@link Customer} objects indexed by {@code id}.
     */
    private final Map<Long, Customer> customers = new HashMap<>();

    /**
     * Map of {@link Article} objects indexed by {@code id}.
     */
    private final Map<String, Article> articles = new HashMap<>();

    /**
     * Map of {@link Order} objects indexed by {@code id}.
     */
    private final Map<Long, Order> orders = new HashMap<>();

    /**
     * Default constructor fills maps 'customers' and 'articles'.
     */
    public Application_E12() {
    }

    /**
     * Method of the {@link Runtime.Runnable} interface called by {@link Runtime}. Program execution starts here.
     *
     * @param properties properties from the {@code application.properties} file
     * @param args arguments passed from the command line
     */
    @Override
    public void run(Properties properties, String[] args) {
        //
        // create Customer objects and collect in 'customers' map
        List.of(dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com")
                    .map(c -> c.addContact("eric98@yahoo.com")
                        .addContact("(030) 3945-642298")), dataFactory.createCustomer("Anne Bayer", "anne24@yahoo.de")
                    .map(c -> c.addContact("(030) 3481-23352")
                        .addContact("fax: (030)23451356")), dataFactory.createCustomer("Schulz-Mueller, Tim", "tim2346@gmx.de"), dataFactory.createCustomer("Blumenfeld, Nadine-Ulla", "+49 152-92454"),
                dataFactory.createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210"),
                //
                // invalid email address and name, no objects are created
                dataFactory.createCustomer("Mandy Mondschein", "locomandy<>gmx.de")
                    .map(c -> c.addContact("+49 030-3956256")), dataFactory.createCustomer("", "nobody@gmx.de"))
            .stream()
            // .filter(o -> o.isPresent())
            // .map(o -> o.get())
            // .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
            .flatMap(Optional::stream)
            .forEach(customer -> customers.put(customer.getId(), customer));
        //
        // create Article objects and collect in 'articles' map
        List.of(dataFactory.createArticle("Tasse", 299, PricingCategory.BasePricing), dataFactory.createArticle("Becher", 149, PricingCategory.BasePricing),
                dataFactory.createArticle("Kanne", 1999, PricingCategory.BasePricing), dataFactory.createArticle("Teller", 649, PricingCategory.BasePricing),
                dataFactory.createArticle("Buch 'Java'", 4990, PricingCategory.BasePricing, TAXRate.Reduced), dataFactory.createArticle("Buch 'UML'", 7995, PricingCategory.BasePricing, TAXRate.Reduced),
                dataFactory.createArticle("Pfanne", 4999, PricingCategory.BasePricing), dataFactory.createArticle("Fahrradhelm", 16900, PricingCategory.BasePricing),
                dataFactory.createArticle("Fahrradkarte", 695, PricingCategory.BasePricing, TAXRate.Reduced))
            .stream()
            .flatMap(Optional::stream)
            .forEach(article -> articles.put(article.getId(), article));
        //
        // build orders and collect in 'orders' map
        buildOrders(orders);
        //
        // print numbers of objects in collections
        System.out.println(String.format("(%d) Customer objects built.\n" + "(%d) Article objects built.\n" + "(%d) Order objects built.\n---", customers.size(), articles.size(), orders.size()));

        // print Order table from values of 'orders' map
        // maintain sequence in which orders were created in DataFactory
        if (orders.size() > 0) {
            // ids as allocated from IdPool<Order> in DataFactory
            var ids = Arrays.asList(8592356245L, 3563561357L, 5234968294L, 6135735635L, 6173043537L, 7372561535L, 4450305661L);
            // select orders from 'orders' map in sequence of 'ids'
            var orderList = ids.stream()
                .map(id -> orders.get(id))
                .filter(o -> o != null) // collect as mutable List
                .collect(Collectors.toCollection(ArrayList::new));
            //
            // add remaining orders
            orderList.addAll(orders.values()
                .stream()
                .filter(o -> !ids.contains(o.getId()))
                .toList());
            StringBuilder sb3 = printOrders(orderList);
            System.out.println(sb3.insert(0, "Bestellungen:\n")
                .toString());
        }

        /*
         * add at the end of run()-method:
         */
        System.out.println("Application_F15, using components:");
        //
        Components components = Components.getInstance();
        Calculator calculator = components.getCalculator();
        //
        long price = 10000L;    // 100.00 EUR
        long vat = calculator.calculateIncludedVAT(price, 19.0);
        System.out.println(String.format("19%s enthaltene MwSt. in %d.%02d EUR sind %d.%02d EUR.", "%", price / 100, price % 100, vat / 100, vat % 100));
    }

    /**
     * Find {@link Customer} object in {@link customers} map by a specification, which is the first match by {@code id} or in the {@code lastName} or in the {@code firstName} attribute (in that
     * order).
     *
     * @param customerSpec specification of a customer by id or by name
     * @return {@link Customer} object or empty Optional
     */
    public Optional<Customer> findCustomerBySpec(String customerSpec) {
        return customerSpec == null
            ? Optional.empty()
            : customers.values()
                .stream()
                .filter(c -> Long.toString(c.getId())
                    .equals(customerSpec) || c.getLastName()
                    .contains(customerSpec) || c.getFirstName()
                    .contains(customerSpec))
                .findAny();
    }

    /**
     * Find {@link Article} object in {@link articles} map by a specification, which is the first match by {@code id} or in the {@code description} attribute (in that order).
     *
     * @param articleSpec specification of an article by id or by description
     * @return {@link Article} object or empty Optional
     */
    public Optional<Article> findArticleBySpec(String articleSpec) {
        var article = Optional.ofNullable(articleSpec != null ? articles.get(articleSpec) : null);
        if (article.isEmpty()) {
            article = articles.values()
                .stream()
                .filter(a -> a.getDescription()
                    .contains(articleSpec))
                .findAny();
        }
        return article;
    }

    /**
     * Format long value to price according to a format (0 is default):
     *
     * <pre>
     * Example: long value: 499
     * style: 0: "4.99"
     *        1: "4.99 EUR"     3: "4.99 €"
     *        2: "4.99EUR"      4: "4.99€"
     * </pre>
     *
     * @param price long value as price
     * @param currency {@link Currency} to obtain currency three-letter code or Unicode
     * @param style price formatting style
     * @return price formatted according to selcted style
     */
    public String fmtPrice(long price, Pricing.Currency currency, int... style) {
        final var cur = currency == null ? Pricing.Currency.Euro : currency;
        final int ft = style.length > 0 ? style[0] : 0; // 0 is default format
        switch (ft) {
            case 0:
                return fmtDecimal(price, 2);
            case 1:
                return fmtDecimal(price, 2, " " + cur.code());
            case 2:
                return fmtDecimal(price, 2, cur.code());
            case 3:
                return fmtDecimal(price, 2, " " + cur.unicode());
            case 4:
                return fmtDecimal(price, 2, cur.unicode());
            default:
                return fmtPrice(price, cur, 0);
        }
    }

    /**
     * Format long value to a decimal String with specified digit formatting:
     *
     * <pre>
     *      {      "%,d", 1L },     // no decimal digits:  16,000Y
     *      { "%,d.%01d", 10L },
     *      { "%,d.%02d", 100L },   // double-digit price: 169.99E
     *      { "%,d.%03d", 1000L },  // triple-digit unit:  16.999-
     * </pre>
     *
     * @param value value to format to String in decimal format
     * @param decimalDigits number of digits
     * @param unit appended unit as String
     * @return decimal value formatted according to specified digit formatting
     */
    public String fmtDecimal(long value, int decimalDigits, String... unit) {
        final String unitStr = unit.length > 0 ? unit[0] : null;
        final Object[][] dec = {{"%,d", 1L}, // no decimal digits: 16,000Y
            {"%,d.%01d", 10L}, {"%,d.%02d", 100L}, // double-digit price: 169.99E
            {"%,d.%03d", 1000L}, // triple-digit unit: 16.999-
        };
        String result;
        String fmt = (String) dec[decimalDigits][0];
        if (unitStr != null && unitStr.length() > 0) {
            fmt += "%s"; // add "%s" to format for unit string
        }
        int decdigs = Math.max(0, Math.min(dec.length - 1, decimalDigits));
        //
        if (decdigs == 0) {
            Object[] args = {value, unitStr};
            result = String.format(fmt, args);
        } else {
            long digs = (long) dec[decdigs][1];
            long frac = Math.abs(value % digs);
            Object[] args = {value / digs, frac, unitStr};
            result = String.format(fmt, args);
        }
        return result;
    }

    /**
     * Print objects of class {@link Order} as table row into a {@link StringBuilder}.
     *
     * @param orders orders to print as row into table
     * @return StringBuilder with orders rendered in table format
     * @throws IllegalArgumentException with null arguments
     */
    public StringBuilder printOrders(Collection<Order> orders) {
        if (orders == null)
            throw new IllegalArgumentException("argument orders: null");
        //
        long[] compound = new long[] {0L, 0L};
        //
        var it = orders.iterator();
        var currencyLabel = Optional.ofNullable(it.hasNext() ? it.next() : null)
            .map(o -> {
                var cur = o.getPricing()
                    .currency();
                return cur == Pricing.Currency.Euro ? "" : String.format(" (in %s)", cur.code());
            })
            .orElse("");
        //
        final var tf = new TableFormatter(
            // table column specification
            "|%-10s|", " %-28s", " %8s", "%1s", " %9s", "| %6s", " %9s|").line() // table header
            .row("Bestell-ID", String.format("Bestellungen%s", currencyLabel), "MwSt", "*", "Preis", "MwSt", "Gesamt")
            .line();
        //
        // print {@link Order} rows:
        orders.stream()
            .forEach(order -> {
                Components components = Components.getInstance();
                Calculator calculator = components.getCalculator();
                long orderValue = calculator.calculateOrderValue(order);
                long orderVAT = calculator.calculateOrderVAT(order);
                //
                // print Order as row:
                printOrder(order, orderValue, orderVAT, tf).line();
                //
                // compound order and tax values
                compound[0] += orderValue;
                compound[1] += orderVAT;
            });
        //
        tf.row(null, null, null, null, "Gesamt:", fmtPrice(compound[1], Pricing.Currency.Euro), fmtPrice(compound[0], Pricing.Currency.Euro));
        tf.line(null, null, null, null, null, "=", "=");
        return tf.get();
    }

    /**
     * Print one {@link Order} object as table row into a {@link TableFormatter}.
     *
     * @param order order to print
     * @param orderValue total order value shown in right column
     * @param orderVAT total order tax shown in right column
     * @param tf {@link TableFormatter} to format and store table row
     * @return table formatter with printed row added
     * @throws IllegalArgumentException with null arguments
     */
    TableFormatter printOrder(Order order, long orderValue, long orderVAT, TableFormatter tf) {
        if (order == null || tf == null)
            throw new IllegalArgumentException("arguments order or table formatter: null");
        //
        var id = Long.valueOf(order.getId())
            .toString();
        var pricing = order.getPricing();
        var currency = pricing.currency();
        // limit name length so label 'Bestellung' is not cut off
        var name = String.format("%.11s", order.getCustomer()
            .getFirstName());
        var brief = name.length() > 6; // shorten labels for longer names
        var curLabel = String.format(brief ? "(%s)" : "(in %s)", currency.code());
        var orderLabel = String.format("%s's %s %s:", name, brief ? "Best." : "Bestellung", curLabel);
        //
        tf.row(id, orderLabel, "", "", "", "", ""); // heading row with order id and name
        //
        var it = order.getOrderItems()
            .iterator();
        Components components = Components.getInstance();
        Calculator calculator = components.getCalculator();
        for (int i = 0; it.hasNext(); i++) {
            var item = it.next();
            var article = item.article();
            var descr = article.getDescription();
            long unitsOrdered = item.unitsOrdered();
            long unitPrice = pricing.unitPrice(article);
            long value = calculator.calculateOrderItemValue(item, pricing);
            long vat = calculator.calculateOrderItemVAT(item, pricing);
            var taxRate = pricing.taxRate(article);
            var reducedTaxMarker = taxRate == TAXRate.Reduced ? "*" : "";
            String itemDescr = String.format(" - %dx %s%s", unitsOrdered, descr, unitsOrdered > 1 ? String.format(", %dx %s", unitsOrdered, fmtPrice(unitPrice, currency)) : String.format(""));
            String[] totals = i < order.itemsCount() - 1 ? // last row?
                new String[] {"", ""} : new String[] {fmtPrice(orderVAT, currency), fmtPrice(orderValue, currency)};
            //
            // item rows with item description, VAT, value and totals in the last row
            tf.row("", itemDescr, fmtPrice(vat, currency), reducedTaxMarker, fmtPrice(value, currency), totals[0], totals[1]);
        }
        return tf;
    }

    /**
     * Builde sample orders using {@link OrderBuilderImpl}.
     *
     * @param collector map to collect built order objects
     */
    private void buildOrders(Map<Long, Order> collector) {
        final var pricing = PricingCategory.BasePricing;
        final OrderBuilderImpl orderBuilder = dataFactory.createOrderBuilder(pricing, this::findCustomerBySpec, this::findArticleBySpec);

        List.of(
                // Eric's 1st order
                orderBuilder.buildOrder("Eric", buildState -> buildState.item(4, "Teller") // 4 Teller, 4x 6.49 €
                    .item(8, "Becher") // 8 Becher, 8x 1.49 €
                    .item(1, "SKU-425378") // 1 Buch "UML", 1x 79.95 €
                    .item(4, "Tasse") // 4 Tassen, 4x 2.99 €
                ),

                // Anne's order
                orderBuilder.buildOrder("Anne", buildState -> buildState.item(2, "Teller") // 2 Teller, 2x 6.49 €
                    .item(2, "Tasse") // 2 Tassen, 2x 2.99 €
                ),

                // Eric's 2nd order
                orderBuilder.buildOrder("Eric", buildState -> buildState.item(1, "Kanne") // 1 Kanne, 1x 19.99 €
                ),

                // Nadine-Ulla's order
                orderBuilder.buildOrder("Nadine-Ulla", buildState -> buildState.item(12, "Teller") // 12 Teller, 12x 6.49 €
                    .item(1, "Buch 'Java'") // 1 Buch "Java", 1x 49.90 €
                    .item(1, "SKU-425378") // 1 Buch "UML", 1x 79.95 €
                ),

                // Khaled Saad's order
                orderBuilder.buildOrder("Khaled Saad", buildState -> buildState.item(1, "Buch 'Java'") // 1 Buch "Java", 1x 49.90 €
                    .item(1, "Fahrradkarte") // 1 Fahrradkarte, 1x 6.95 €
                ),

                // Eric's 3rd order
                orderBuilder.buildOrder("Eric", buildState -> buildState.item(1, "Fahrradhelm") // 1 Fahrradhelm, 1x 169.00 €
                    .item(1, "Fahrradkarte") // 1 Fahrradkarte, 1x 6.95 €
                ),

                // Eric's 4th order
                orderBuilder.buildOrder("Eric", buildState -> buildState.item(3, "Tasse") // 3 Tassen, 3x 2.99 €
                    .item(3, "Becher") // 3 Becher, 3x 1.49 €
                    .item(1, "Kanne") // 1 Kanne, 1x 19.99 €
                ))
            .stream()
            .flatMap(Optional::stream)
            .forEach(order -> collector.put(order.getId(), order));
    }
}