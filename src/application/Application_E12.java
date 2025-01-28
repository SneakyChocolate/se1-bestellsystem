package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import application.Runtime.Bean;
import components.Calculator;
import components.Components;
import components.DataFactory;
import components.Formatter;
import components.Formatter.TableFormatter;
import datamodel.*;
import datamodel.Pricing.*;
import datamodel.Pricing.Currency;


/**
 * Driver class for the <i>d34-ordertable</i> assignment. Class creates
 * {@link Customer}, {@link Article} and {@link Order} objects and prints
 * tables of objects.
 * <br>
 * Class implements the {@link Runtime.Runnable} interface.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
@Bean(priority=12)
public class Application_E12 implements Runtime.Runnable {

    /**
     * References to {@link DataFactory}, {@link Calculator} and {@link Formatter}.
     */
    private final DataFactory dataFactory = Components.getInstance().getDataFactory();

    private final Formatter formatter = Components.getInstance().getFormatter();

    private final Calculator calculator = Components.getInstance().getCalculator();

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
    public Application_E12() { }

    /**
     * Method of the {@link Runtime.Runnable} interface called by {@link Runtime}.
     * Program execution starts here.
     * @param properties properties from the {@code application.properties} file
     * @param args arguments passed from the command line
     */
    @Override
    public void run(Properties properties, String[] args) {
        // 
        // create Customer objects and collect in 'customers' map
        List.of(
            dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com").map(c -> c.addContact("eric98@yahoo.com").addContact("(030) 3945-642298")),
            dataFactory.createCustomer("Anne Bayer", "anne24@yahoo.de").map(c -> c.addContact("(030) 3481-23352").addContact("fax: (030)23451356")),
            dataFactory.createCustomer("Schulz-Mueller, Tim", "tim2346@gmx.de"),
            dataFactory.createCustomer("Blumenfeld, Nadine-Ulla", "+49 152-92454"),
            dataFactory.createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210"),
            // 
            // invalid email address and name, no objects are created
            dataFactory.createCustomer("Mandy Mondschein", "locomandy<>gmx.de").map(c -> c.addContact("+49 030-3956256")),
            dataFactory.createCustomer("", "nobody@gmx.de")
        ).stream()
            // .filter(o -> o.isPresent())
            // .map(o -> o.get())
            // .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
            .flatMap(Optional::stream)
            .forEach(customer -> customers.put(customer.getId(), customer));
        // 
        // create Article objects and collect in 'articles' map
        List.of(
            dataFactory.createArticle("Tasse",         299, PricingCategory.BasePricing),
            dataFactory.createArticle("Becher",        149, PricingCategory.BasePricing),
            dataFactory.createArticle("Kanne",        1999, PricingCategory.BasePricing),
            dataFactory.createArticle("Teller",        649, PricingCategory.BasePricing),
            dataFactory.createArticle("Buch 'Java'",  4990, PricingCategory.BasePricing, TAXRate.Reduced),
            dataFactory.createArticle("Buch 'UML'",   7995, PricingCategory.BasePricing, TAXRate.Reduced),
            dataFactory.createArticle("Pfanne",       4999, PricingCategory.BasePricing),
            dataFactory.createArticle("Fahrradhelm", 16900, PricingCategory.BasePricing),
            dataFactory.createArticle("Fahrradkarte",  695, PricingCategory.BasePricing, TAXRate.Reduced)
        ).stream()
            .flatMap(Optional::stream)
            .forEach(article -> articles.put(article.getId(), article));
        // 
        // 
        // create OrderBuilder with DataFactory for BasePricing
        final var orderBuilder = dataFactory.createOrderBuilder(
            // 
            PricingCategory.BasePricing,
            // 
            // function to look up customer in 'customers' map matching 'id', 'last' or 'first' name
            customerSpec -> findCustomerBySpec(customerSpec),
            // 
            // function to look up article in 'articles' map matching 'id' or 'description'
            articleSpec -> findArticleBySpec(articleSpec));
        // 
        // build Order objects using OrderBuilder and collect in 'orders' map
        List.of(
            // 
            // Eric's 1st order using buildOrder()
            orderBuilder.buildOrder("Eric", buildState -> buildState
                .item( 4, "Teller")         // + item: 4 Teller, 4x 6.49 €
                .item( 8, "Becher")         // + item: 8 Becher, 8x 1.49 €
                .item( 1, "SKU-425378")     // + item: 1 Buch "UML", 1x 79.95 €, 7% MwSt (5.23€)
                .item( 4, "Tasse")          // + item: 4 Tassen, 4x 2.99 €
            ),
            // 
            // build Anne's order by article id's using OrderBuilder's shorter build() method
            orderBuilder.buildOrder("Anne", buildState -> buildState
                .item( 2, "SKU-638035")     // + item Teller by id, 2x 6.49 €
                .item( 2, "SKU-458362")     // + item Tasse by id, 2x 2.99 €
            ),
            // 
            // build Eric's 2nd order, look up by Eric's id: "892474"
            orderBuilder.buildOrder("892474", buildState -> buildState
                .item(1, "Kanne")
            ),
            // 
            // build Nadine's order
            orderBuilder.buildOrder("Nadine", buildState -> buildState
                .item(12, "Teller")
                .item( 1, "Buch 'Java'")
                .item( 1, "Buch 'UML'")
            ),
            // 
            // build Khaled's order
            orderBuilder.buildOrder("Khaled", buildState -> buildState
                .item( 1, "Buch 'Java'")
                .item( 1, "Fahrradkarte")
            ),
            // 
            // build Eric's 3rd order
            orderBuilder.buildOrder("Eric", buildState -> buildState
                .item( 1, "Fahrradhelm")
                .item( 1, "Fahrradkarte")
            ),
            // 
            // build Eric's 4th order
            orderBuilder.buildOrder("Eric", buildState -> buildState
                .item( 3, "Tasse")
                .item( 3, "Becher")
                .item( 1, "Kanne")
            )
        ).stream()
            .flatMap(Optional::stream)
            .forEach(order -> orders.put(order.getId(), order));
        // 
        // 
        // print numbers of objects in collections
        System.out.println(String.format(
            "(%d) Customer objects built.\n" +
            "(%d) Article objects built.\n" +
            "(%d) Order objects built.\n---",
            customers.size(), articles.size(), orders.size()));

        // print Order table from values of 'orders' map
        // maintain sequence in which orders were created in DataFactory
        if(orders.size() > 1) {
            // ids as allocated from IdPool<Order> in DataFactory in buildOrders()
            var ids = Arrays.asList(
                8592356245L, 3563561357L, 5234968294L, 6135735635L, 6173043537L,
                7372561535L, 4450305661L
            );
            // select orders from 'orders' map in sequence of 'ids'
            var orderList = ids.stream()
                .map(id -> orders.get(id))
                .filter(o -> o != null)     // collect as mutable List
                .collect(Collectors.toCollection(ArrayList::new));
            //
            // add remaining orders
            orderList.addAll(orders.values().stream()
                .filter(o -> ! ids.contains(o.getId()))
                .toList()
            );
            // @REMOVE
            // orderList.sort((o1, o2) -> Long.compare(calculateOrderValue(o2), calculateOrderValue(o1)));
            // @/REMOVE
            StringBuilder sb3 = printOrders(orderList);
            System.out.println(sb3.insert(0, "Bestellungen:\n").toString());
        }
        // @REMOVE
        // // gross value of 119 EUR, which includes 19 EUR VAT of a net value of 100 EUR
        // // gross value of 100 EUR, which includes 19 EUR VAT of a net value of 100 EUR
        // long gross = 11900;
        // long includedVAT = calculateIncludedVAT(gross, 19.0);
        // long net = gross - includedVAT;
        // System.out.println(String.format("gross value: %s, including %s VAT; net value: %6s",
        //     fmtPrice(gross, Pricing.Currency.Euro, 1),
        //     fmtPrice(includedVAT, Pricing.Currency.Euro),
        //     fmtPrice(net, Pricing.Currency.Euro)
        // ));
        // @/REMOVE

        System.out.println("Application_F12, using components:");
        //
        Components components = Components.getInstance();
        Calculator calculator = components.getCalculator();
        //
        long price = 10000L;    // 100.00 EUR
        long vat = calculator.calculateIncludedVAT(price, 19.0);
        System.out.println(String.format("19%s enthaltene MwSt. in %d.%02d EUR sind %d.%02d EUR.",
            "%", price/100, price%100, vat/100, vat%100));
    }

    /**
     * Find {@link Customer} object in {@link customers} map by a specification,
     * which is the first match by {@code id} or in the {@code lastName} or
     * in the {@code firstName} attribute (in that order).
     * @param customerSpec specification of a customer by id or by name
     * @return {@link Customer} object or empty Optional
     */
    public Optional<Customer> findCustomerBySpec(String customerSpec) {
        return customerSpec==null? Optional.empty() :
            customers.values().stream()
                .filter(c -> Long.toString(c.getId()).equals(customerSpec) ||
                    c.getLastName().contains(customerSpec) ||
                    c.getFirstName().contains(customerSpec)
                )
                .findAny();
    }

    /**
     * Find {@link Article} object in {@link articles} map by a specification,
     * which is the first match by {@code id} or in the {@code description}
     * attribute (in that order).
     * @param articleSpec specification of an article by id or by description
     * @return {@link Article} object or empty Optional
     */
    public Optional<Article> findArticleBySpec(String articleSpec) {
        var article = Optional.ofNullable(articleSpec != null? articles.get(articleSpec) : null);
        if(article.isEmpty()) {
            article = articles.values().stream()
                .filter(a -> a.getDescription().contains(articleSpec))
                .findAny();
        }
        return article;
    }

    /**
     * Print objects of class {@link Order} as table row into a {@link StringBuilder}.
     * @param orders orders to print as row into table
     * @return StringBuilder with orders rendered in table format
     * @throws IllegalArgumentException with null arguments
     */
    public StringBuilder printOrders(Collection<Order> orders) {
        if(orders==null)
            throw new IllegalArgumentException("argument orders: null");
        //
        long[] compound = new long[] {0L, 0L};
        //
        var it = orders.iterator();
        var currencyLabel = Optional.ofNullable(it.hasNext()? it.next() : null).map(o -> {
            var cur = o.getPricing().currency();
            return cur==Currency.Euro? "" : String.format(" (in %s)", cur.code());
        }).orElse("");
        // 
        // final var tf = new TableFormatter(
        final var tf = formatter.createTableFormatter(
                // table column specification
                "|%-10s|", " %-28s", " %8s", "%1s", " %9s", "| %6s", " %9s|"
            )
            .line()     // table header
            .row("Bestell-ID", String.format("Bestellungen%s", currencyLabel), "MwSt", "*", "Preis", "MwSt", "Gesamt")
            .line();
        //
        // print {@link Order} rows:
        orders.stream()
            .forEach(order -> {
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
        tf.row(null, null, null, null, "Gesamt:", formatter.fmtPrice(compound[1], Currency.Euro), formatter.fmtPrice(compound[0], Currency.Euro));
        tf.line(null, null, null, null, null, "=", "=");
        return tf.get();
    }

    /**
     * Print one {@link Order} object as table row into a {@link TableFormatter}.
     * @param order order to print
     * @param orderValue total order value shown in right column
     * @param orderVAT total order tax shown in right column
     * @param tf {@link TableFormatter} to format and store table row
     * @return table formatter with printed row added
     * @throws IllegalArgumentException with null arguments
     */
    TableFormatter printOrder(Order order, long orderValue, long orderVAT, TableFormatter tf) {
        if(order==null || tf==null)
            throw new IllegalArgumentException("arguments order or table formatter: null");
        //
        var id = Long.valueOf(order.getId()).toString();
        var pricing = order.getPricing();
        var currency = pricing.currency();
        // limit name length so label 'Bestellung' is not cut off
        var name = String.format("%.11s", order.getCustomer().getFirstName());
        var brief = name.length() > 6;  // shorten labels for longer names
        var curLabel = String.format(brief? "(%s)" : "(in %s)", currency.code());
        var orderLabel = String.format("%s's %s %s:", name, brief? "Best." : "Bestellung", curLabel);
        //
        tf.row(id, orderLabel, "", "", "", "", "");   // heading row with order id and name
        //
        var it = order.getOrderItems().iterator();
        for(int i=0; it.hasNext(); i++) {
            var item = it.next();
            var article = item.article();
            var descr = article.getDescription();
            long unitsOrdered = item.unitsOrdered();
            long unitPrice = pricing.unitPrice(article);
            long value = calculator.calculateOrderItemValue(item, pricing);
            long vat = calculator.calculateOrderItemVAT(item, pricing);
            var taxRate = pricing.taxRate(article);
            var reducedTaxMarker = taxRate==TAXRate.Reduced? "*" : "";
            String itemDescr = String.format(" - %dx %s%s",
                unitsOrdered, descr, unitsOrdered > 1?
                    String.format(", %dx %s", unitsOrdered, formatter.fmtPrice(unitPrice, currency)) :
                    String.format("")
                );
            String[] totals = i < order.itemsCount() - 1?   // last row?
                new String[] { "", ""} :
                new String[] { formatter.fmtPrice(orderVAT, currency), formatter.fmtPrice(orderValue, currency) };
            //
            // item rows with item description, VAT, value and totals in the last row
            tf.row("", itemDescr, formatter.fmtPrice(vat, currency), reducedTaxMarker, formatter.fmtPrice(value, currency), totals[0], totals[1]);
        };
        return tf;
    }

    // /**
    //  * Calculate a tax included in a gross (<i>"brutto"</i>) value based
    //  * on a given tax rate.
    //  * Applies to VAT taxes called <i>"Mehrwertsteuer" (MwSt.)</i> in Germany.
    //  * @param grossValue value that includes the tax
    //  * @param taxRate applicable tax rate
    //  * @return tax included in gross value or 0L if {@code gross value <= 0L}
    //  */
    // public long calculateIncludedVAT(long grossValue, double taxRate) {
    //     long vat = 0L;
    //     if(grossValue > 0L) {
    //         double tr = taxRate / 100.0;
    //         double vat_double = grossValue * tr / (1.0 + tr);
    //         double vat_rounded = Math.round(vat_double);
    //         vat = Double.valueOf(vat_rounded).longValue();
    //     }
    //     return vat;
    // }

    // /**
    //  * Calculate the value of an {@link OrderItem} as: {@code article.unitPrice *
    //  * number of units ordered}.
    //  * @param item to calculate value for
    //  * @param pricing {@link Pricing} to find article unitPrice
    //  * @return value of ordered item
    //  * @throws IllegalArgumentException with null arguments
    //  */
    // public long calculateOrderItemValue(OrderItem item, Pricing pricing) {
    //     if(item==null)
    //         throw new IllegalArgumentException("argument item: null");
    //     if(pricing==null)
    //         throw new IllegalArgumentException("argument pricing: null");
    //     //
    //     var unitPrice = pricing.unitPrice(item.article());
    //     return unitPrice * item.unitsOrdered();
    // }

    // /**
    //  * Calculate the VAT included in an order item price uding method:
    //  * {@code calculateVAT(long grossValue, double taxRate)}.
    //  * @param item to calculate VAT for
    //  * @param pricing {@link Pricing} to find VAT tax rate applicable to article
    //  * @return VAT for ordered item
    //  * @throws IllegalArgumentException with null arguments
    //  */
    // public long calculateOrderItemVAT(OrderItem item, Pricing pricing) {
    //     if(item==null)
    //         throw new IllegalArgumentException("argument item: null");
    //     if(pricing==null)
    //         throw new IllegalArgumentException("argument pricing: null");
    //     //
    //     var taxRate = pricing.taxRateAsPercent(item.article());
    //     return calculateIncludedVAT(calculateOrderItemValue(item, pricing), taxRate);
    // }

    // /**
    //  * Calculate the total value of an order from the value of each ordered item,
    //  * calculated with: {@code calculateOrderItemValue(item)}.
    //  * @param order to calculate value for
    //  * @return total value of order
    //  * @throws IllegalArgumentException with null argument
    //  */
    // public long calculateOrderValue(Order order) {
    //     if(order==null)
    //         throw new IllegalArgumentException("argument order: null");
    //     //
    //     long value = 0L;
    //     var pricing = order.getPricing();
    //     for(var item : order.getOrderItems()) {
    //         value += calculateOrderItemValue(item, pricing);
    //     }
    //     return value;
    //     // return StreamSupport.stream(order.getOrderItems().spliterator(), false)
    //     //     .map(item -> calculateOrderItemValue(item, pricing))
    //     //     .reduce(0L, (compound, vat) -> compound + vat);
    // }

    // /**
    //  * Calculate the total VAT of an order from compounded VAT
    //  * of order items calculated with: {@code calculateOrderItemVAT(item)}.
    //  * @param order to calculate VAT tax for
    //  * @return VAT calculated for order
    //  * @throws IllegalArgumentException with null argument
    //  */
    // public long calculateOrderVAT(Order order) {
    //     if(order==null)
    //         throw new IllegalArgumentException("argument order: null");
    //     // 
    //     long vat = 0L;
    //     var pricing = order.getPricing();
    //     for(var item : order.getOrderItems()) {
    //         vat += calculateOrderItemVAT(item, pricing);
    //     }
    //     return vat;
    // }

    // /**
    //  * Format long value to price according to a format (0 is default):
    //  * <pre>
    //  * Example: long value: 499
    //  * style: 0: "4.99"
    //  *        1: "4.99 EUR"     3: "4.99 €"
    //  *        2: "4.99EUR"      4: "4.99€"
    //  * </pre>
    //  * @param price long value as price
    //  * @param currency {@link Currency} to obtain currency three-letter code or Unicode
    //  * @param style price formatting style
    //  * @return price formatted according to selcted style
    //  */
    // public String fmtPrice(long price, Pricing.Currency currency, int... style) {
    //     final var cur = currency==null? Pricing.Currency.Euro : currency;
    //     final int ft = style.length > 0? style[0] : 0;	// 0 is default format
    //     switch(ft) {
    //     case 0: return fmtDecimal(price, 2);
    //     case 1: return fmtDecimal(price, 2, " " + cur.code());
    //     case 2: return fmtDecimal(price, 2, cur.code());
    //     case 3: return fmtDecimal(price, 2, " " + cur.unicode());
    //     case 4: return fmtDecimal(price, 2, cur.unicode());
    //     default: return fmtPrice(price, cur, 0);
    //     }
    // }

    // /**
    //  * Format long value to a decimal String with specified digit formatting:
    //  * <pre>
    //  *      {      "%,d", 1L },     // no decimal digits:  16,000Y
    //  *      { "%,d.%01d", 10L },
    //  *      { "%,d.%02d", 100L },   // double-digit price: 169.99E
    //  *      { "%,d.%03d", 1000L },  // triple-digit unit:  16.999-
    //  * </pre>
    //  * @param value value to format to String in decimal format
    //  * @param decimalDigits number of digits
    //  * @param unit appended unit as String
    //  * @return decimal value formatted according to specified digit formatting
    //  */
    // public String fmtDecimal(long value, int decimalDigits, String... unit) {
    //     final String unitStr = unit.length > 0? unit[0] : null;
    //     final Object[][] dec = {
    //         {      "%,d", 1L },     // no decimal digits:  16,000Y
    //         { "%,d.%01d", 10L },
    //         { "%,d.%02d", 100L },   // double-digit price: 169.99E
    //         { "%,d.%03d", 1000L },  // triple-digit unit:  16.999-
    //     };
    //     String result;
    //     String fmt = (String)dec[decimalDigits][0];
    //     if(unitStr != null && unitStr.length() > 0) {
    //         fmt += "%s";	// add "%s" to format for unit string
    //     }
    //     int decdigs = Math.max(0, Math.min(dec.length - 1, decimalDigits));
    //     //
    //     if(decdigs==0) {
    //         Object[] args = {value, unitStr};
    //         result = String.format(fmt, args);
    //     } else {
    //         long digs = (long)dec[decdigs][1];
    //         long frac = Math.abs( value % digs );
    //         Object[] args = {value/digs, frac, unitStr};
    //         result = String.format(fmt, args);
    //     }
    //     return result;
    // }

    // /**
    //  * {@link TableFormatter} produces text in form of a table defined by columns
    //  * of specified <i>width</i> and <i>alignment</i>. {@code String.format(fmt)}
    //  * specifications: <i>fmt</i> format cells in a row according to <i>width</i>
    //  * and <i>alignment</i> specifications.
    //  * @author sgra64
    //  */
    // class TableFormatter {

    //     /**
    //      * Format specifiers for each column.
    //      */
    //     private final List<String> fmts;

    //     /**
    //      * Width of each column.
    //      */
    //     private final List<Integer> widths;

    //     /**
    //      * Collect formatted rows.
    //      */
    //     private final StringBuilder sb;

    //     /**
    //      * Constructor with String.format(fmt) specifiers for each column.
    //      * @param fmtArgs String.format(fmt) specifiers for each column
    //      */
    //     public TableFormatter(String... fmtArgs) {
    //         this((StringBuilder)null, fmtArgs);
    //     }

    //     /**
    //      * Constructor with external collector of table rows and String.format(fmt)
    //      * specifiers for each column.
    //      * @param sb external collector for table rows
    //      * @param fmtArgs String.format(fmt) specifiers for each column
    //      */
    //     public TableFormatter(StringBuilder sb, String... fmtArgs) {
    //         this.sb = sb != null? sb : new StringBuilder();
    //         this.fmts = Arrays.stream(fmtArgs).toList();
    //         this.widths = fmts.stream().map(fmt -> String.format(fmt, "").length()).toList();
    //     }

    //     /**
    //      * Add row to table. Each cell is formatted according to the column fmt specifier.
    //      * @param cells variable array of cells
    //      * @return chainable self-reference
    //      */
    //     public TableFormatter row(String... cells) {
    //         IntStream.range(0, Math.min(fmts.size(), cells.length)).forEach(i -> {
    //             sb.append(fillCell(i, cells[i], t -> {
    //                 String fmt = fmts.get(i);
    //                 int i1 = fmt.indexOf('%');  // offset width by format chars, e.g. '%-20s'
    //                 int i2 = Math.max(fmt.indexOf('s'), fmt.indexOf('d'));  // end '%s', '%d'
    //                 int offset = fmt.length() - (i2 - i1) -1;
    //                 // cut cell text to effective column width
    //                 t = t.substring(0, Math.min(t.length(), widths.get(i) - offset));
    //                 return String.format(fmt, t);
    //             }));
    //         });
    //         return this.endRow();
    //     }

    //     /**
    //      * Add line comprised of segments for each column to the table.
    //      * Segments are drawn based on segment spefifiers with:
    //      * <pre>
    //      * seg: null    - empty or blank segment
    //      *      ""      - segment filled with default character: "-"
    //      *      "="     - segment is filled with provided character.
    //      * </pre>
    //      * @param segs variable array of segment specifiers
    //      * @return chainable self-reference
    //      */
    //     public TableFormatter line(String... segs) {
    //         if(segs.length==0) {    // print full line when segs is empty
    //             String[] args = fmts.stream().map(f -> "").toArray(String[]::new);
    //             return line(args);  // invoke recursively with ""-args
    //         }
    //         IntStream.range(0, Math.min(fmts.size(), segs.length)).forEach(i -> {
    //             sb.append(fillCell(i, segs[i], s -> {
    //                 s = s.length() > 0? s.substring(0, 1) : "-"; // filler char
    //                 return String.format(fmts.get(i), "")
    //                         .replaceAll("[^\\|]", s).replaceAll("[\\|]", "+");
    //             }));
    //         });
    //         return this.endRow();
    //     }

    //     /**
    //      * Getter to collected table content.
    //      * @return table content
    //      */
    //     public StringBuilder get() { return sb; }

    //     /**
    //      * Fill table cell with provided text or spaces (blank cell).
    //      * @param i column index
    //      * @param text text to fill
    //      * @param cellFiller upcall to format cell fitted to column
    //      * @return fitted String with text or spaces
    //      */
    //     private String fillCell(int i, String text, Function<String, String> cellFiller) {
    //         return text != null? cellFiller.apply(text) : " ".repeat(widths.get(i));
    //     }

    //     /**
    //      * End row with trailing {@code "\n"}.
    //      * @return chainable self-reference
    //      */
    //     private TableFormatter endRow() { sb.append("\n"); return this; }
    // }
}