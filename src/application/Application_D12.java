package application;

import application.Runtime.Bean;
import components.impl.ComponentsImpl;
import datamodel.Article;
import datamodel.Customer;
import components.DataFactory;
import datamodel.Order;
import datamodel.Pricing;
import datamodel.Pricing.PricingCategory;
import datamodel.Pricing.TAXRate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Driver class for the <i>c4-customer</i> assignment. Class creates {@link Customer} objects and prints a customer table. Class implements the {@link Runtime.Runnable} interface.
 *
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 */
@Bean(priority = 10)
public class Application_D12 implements Runtime.Runnable {

    /**
     * Reference to {@link DataFactory} singleton.
     */
    private final DataFactory dataFactory = ComponentsImpl.getInstance()
        .getDataFactory();

    /**
     * Public default constructor (to avoid javadoc warning).
     */
    public Application_D12() {}


    /**
     * Method of the {@link Runtime.Runnable} interface called by {@link Runtime}. Program execution starts here.
     *
     * @param properties properties from the {@code application.properties} file
     * @param args arguments passed from the command line
     */
    @Override
    public void run(Properties properties, String[] args) {

        /*
         * Collect created Customer objects in list 'customers'.
         */
        final List<Customer> customers = List.of(
                //
                dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com")
                    .map(c -> c.addContact("eric98@yahoo.com")
                        .addContact("(030) 3945-642298")),
                //
                dataFactory.createCustomer("Anne Bayer", "anne24@yahoo.de")
                    .map(c -> c.addContact("(030) 3481-23352")
                        .addContact("fax: (030)23451356")),
                //
                dataFactory.createCustomer("Schulz-Mueller, Tim", "tim2346@gmx.de"), dataFactory.createCustomer("Blumenfeld, Nadine-Ulla", "+49 152-92454"),
                dataFactory.createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210"),
                //
                // attempts to create Customer objects from invalid arguments
                // invalid email address, no object is created
                dataFactory.createCustomer("Mandy Mondschein", "locomandy<>gmx.de")
                    .map(c -> c.addContact("+49 030-3956256")), // and no other (valid) contact is added
                dataFactory.createCustomer("", "nobody@gmx.de") // invalid name, no object is created
                //
            )
            .stream()
            .filter(c -> c.isPresent())         // remove empty Optionals (with no customer object)
            .map(c -> c.get())                  // unpack customer object from Optional
            .toList();                          // collect created customer objects as List

        /*
         * Collect created Article objects in a Map<Id, Article> such that they can be
         * looked up by: articles.get(id) -> article object.
         */
        final Map<String, Article> articles = List.of(
                //
                dataFactory.createArticle("Tasse", 299, PricingCategory.BasePricing), dataFactory.createArticle("Becher", 149, PricingCategory.BasePricing),
                dataFactory.createArticle("Kanne", 1999, PricingCategory.BasePricing), dataFactory.createArticle("Teller", 649, PricingCategory.BasePricing),
                dataFactory.createArticle("Buch 'Java'", 4990, PricingCategory.BasePricing, TAXRate.Reduced), dataFactory.createArticle("Buch 'UML'", 7995, PricingCategory.BasePricing, TAXRate.Reduced),
                dataFactory.createArticle("Pfanne", 4999, PricingCategory.BasePricing), dataFactory.createArticle("Fahrradhelm", 16900, PricingCategory.BasePricing),
                dataFactory.createArticle("Fahrradkarte", 695, PricingCategory.BasePricing, TAXRate.Reduced)
                //
            )
            .stream()
            .filter(a -> a.isPresent())   // check article object is present in Optional
            .filter(a -> a.isPresent())         // remove empty Optionals (with no article object)
            .map(a -> a.get())                  // unpack article object from Optional
            // collect articles as map of article objects indexed by article id
            .collect(Collectors.toMap(a -> a.getId(), a -> a));

        /*
         * List of Order objects (still empty).
         */
        final List<Order> orders = List.of();

        // print numbers of objects in collections
        System.out.println(String.format("(%d) Customer objects built.\n" + "(%d) Article objects built.\n" + "(%d) Order objects built.\n---", customers.spliterator()
            .getExactSizeIfKnown(), articles.size(), orders.size()));

        // build and print Customer table
        StringBuilder sb = printCustomers(customers);
        System.out.println(sb.insert(0, "Kunden:\n")
            .toString());

        // build and print Article tables for different Pricings
        Arrays.stream(PricingCategory.values())
            .forEach(category -> {
                // header: "Artikel (BasePricing, EUR):"
                var header = String.format("Artikel (%s, %s):\n", category, category.pricing()
                    .currency()
                    .code());
                StringBuilder sb2 = printArticles(articles.values()
                    .stream()
                    .toList(), category);
                System.out.println(sb2.insert(0, header)
                    .toString());
            });

        // built and print Order table
        if (orders.size() > 0) {
            StringBuilder sb3 = printOrders(orders);
            System.out.println(sb3.insert(0, "Bestellungen:\n")
                .toString());
        }
    }

    /**
     * JavaVM entry method that calls the {@link Runtime}, which creates an instance of this class and invokes the {@code run(properties, args)} - method.
     *
     * @param args arguments passed from command line
     */
    public static void main(String[] args) {
        Runtime.run(args);
    }

    /**
     * Print objects of class {@link Customer} as table row into a {@link StringBuilder}.
     *
     * @param customers customer objects to print
     * @return StringBuilder with customers rendered as rows in table format
     * @throws IllegalArgumentException with null arguments
     */
    public StringBuilder printCustomers(Collection<Customer> customers) {
        if (customers == null)
            throw new IllegalArgumentException("argument customers: null");
        //
        final TableFormatter tf = new TableFormatter(
            // table column specification
            "| %8s ", "| %-32s", "| %-31s |").line()
            .row("Kund.-ID", "Name", "Kontakt") // table header
            .line();
        //
        // print {@link Customer} rows:

        Comparator<Customer> comparator = (c1, c2) -> c1.getLastName()
            .compareTo(c2.getLastName());
        customers.stream()
            .sorted(comparator)
            .forEach(c -> {
                var id = String.format("%d", c.getId());
                var name = fmtCustomerName(c);
                var contact = fmtCustomerContacts(c, 1);
                //
                tf.row(id, name, contact);  // write row into table
            });
        return tf.line()
            .get();
    }

    /**
     * Print objects of class {@link Article} as table row into a {@link StringBuilder}.
     *
     * @param articles articles to print as row into table
     * @param pricingCategory {@link PricingCategory} used to print articles (tax rate, currency)
     * @return StringBuilder with articles rendered in table format
     * @throws IllegalArgumentException with null arguments
     */
    public StringBuilder printArticles(Collection<Article> articles, PricingCategory pricingCategory) {
        if (articles == null)
            throw new IllegalArgumentException("argument articles: null");
        //
        var pricing = pricingCategory.pricing();
        //
        final TableFormatter tf = new TableFormatter(
            // table column specification
            "|%-10s", "| %-32s", "| %14s", "|%7s", "%-10s|").line()     // table header
            .row("Artikel-ID", "Beschreibung", "Preis " + pricing.currency()
                .code(), "MwSt", ".Satz")
            .line();
        //
        // print {@link Article} rows:
        Comparator<Article> comparator = (a1, a2) -> {
            long p1 = pricing.unitPrice(a1);
            long p2 = pricing.unitPrice(a2);
            return (int) (p2 - p1);
        };
        articles.stream()
            .sorted(comparator)
            .forEach(article -> {
                var id = article.getId();
                var description = article.getDescription();
                var price = fmtPrice(pricing.unitPrice(article), pricing.currency(), 1);
                var taxRate = pricing.taxRate(article);
                double percent = pricing.taxRateAsPercent(article);
                long lrd = (long) (percent * 10.0) % 10;
                var vatRate = String.format((lrd == 0L ? "%.0f%s" : "%.1f%s"), percent, "%");
                String vat = taxRate == TAXRate.Regular ? " normal" : taxRate == TAXRate.Reduced ? " reduziert" : taxRate == TAXRate.Excempt ? " excempt" : "";
                //
                tf.row(id, description, price, vatRate, vat);  // write row into table
            });
        return tf.line()
            .get();
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
            throw new IllegalArgumentException("orders articles: null");
        //
        return new StringBuilder();
    }

    /**
     * Format Customer name according to a format (0 is default):
     * <pre>
     * style: 0: "Meyer, Eric"  10: "MEYER, ERIC"
     *        1: "Eric Meyer"   11: "ERIC MEYER"
     *        2: "Meyer, E."    12: "MEYER, E."
     *        3: "E. Meyer"     13: "E. MEYER"
     *        4: "Meyer"        14: "MEYER"
     *        5: "Eric"         15: "ERIC"
     * </pre>
     *
     * @param customer Customer object
     * @param style name formatting style
     * @return Customer name formatted according to the selcted style
     * @throws IllegalArgumentException with null arguments
     */
    public String fmtCustomerName(Customer customer, int... style) {
        if (customer == null)
            throw new IllegalArgumentException("argument customer: null");
        //
        String ln = customer.getLastName();
        String fn = customer.getFirstName();
        String fn1 = fn.length() == 0
            ? ""
            : fn.substring(0, 1)
                .toUpperCase();
        //
        final int ft = style.length > 0 ? style[0] : 0;  // 0 is default format
        switch (ft) {    // 0 is default
            case 0:
                return String.format(fn.length() > 0 ? "%s, %s" : "%s", ln, fn);
            case 1:
                return String.format(fn.length() > 0 ? "%s %s" : "%s%s", fn, ln);
            case 2:
                return String.format(fn.length() > 0 ? "%s, %s." : "%s", ln, fn1);
            case 3:
                return String.format(fn.length() > 0 ? "%s. %s" : "%s%s", fn1, ln);
            case 4:
                return ln;
            case 5:
                return fn.length() > 0 ? fn : ln;
            //
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return fmtCustomerName(customer, ft - 10).toUpperCase();
            //
            default:
                return fmtCustomerName(customer, 0);
        }
    }

    /**
     * Format Customer contacts according to a format (0 is default):
     * <pre>
     * style: 0: first contact: "anne24@yahoo.de"
     *        1: first contact with extension indicator: "anne24@yahoo.de, (+2 contacts)"
     *        2: all contacts as list: "anne24@yahoo.de, (030) 3481-23352, fax: (030)23451356"
     * </pre>
     *
     * @param customer Customer object
     * @param style name formatting style
     * @return Customer contact information formatted according to the selcted style
     */
    public String fmtCustomerContacts(Customer customer, int... style) {
        if (customer == null)
            throw new IllegalArgumentException("argument customer: null");
        //
        var len = customer.contactsCount();
        final int ft = style.length > 0 ? style[0] : 0;  // 0 is default format
        switch (ft) {    // 0 is default
            case 0:
                var it = customer.getContacts()
                    .iterator();
                return String.format("%s", it.hasNext() ? it.next() : "");

            case 1:
                String ext = len > 1 ? String.format(", (+%d contacts)", len - 1) : "";
                return String.format("%s%s", fmtCustomerContacts(customer, 0), ext);

            case 2:
                StringBuilder sb = new StringBuilder();
                it = customer.getContacts()
                    .iterator();
                while (it.hasNext()) {
                    sb.append(it.next())
                        .append(sb.length() > 0 ? ", " : "");
                }
                return sb.toString();
            //
            default:
                return fmtCustomerContacts(customer, 0);
        }
    }

    /**
     * Format long value to price according to a format (0 is default):
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
        final int ft = style.length > 0 ? style[0] : 0;    // 0 is default format
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
        final Object[][] dec = {{"%,d", 1L},     // no decimal digits:  16,000Y
            {"%,d.%01d", 10L}, {"%,d.%02d", 100L},   // double-digit price: 169.99E
            {"%,d.%03d", 1000L},  // triple-digit unit:  16.999-
        };
        String result;
        String fmt = (String) dec[decimalDigits][0];
        if (unitStr != null && unitStr.length() > 0) {
            fmt += "%s";    // add "%s" to format for unit string
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
     * {@link TableFormatter} produces text in form of a table defined by columns of specified <i>width</i> and <i>alignment</i>. {@code String.format(fmt)} specifications: <i>fmt</i> format cells in
     * a row according to <i>width</i> and <i>alignment</i> specifications.
     *
     * @author sgra64
     */
    class TableFormatter {

        /**
         * Format specifiers for each column.
         */
        private final List<String> fmts;

        /**
         * Width of each column.
         */
        private final List<Integer> widths;

        /**
         * Collect formatted rows.
         */
        private final StringBuilder sb;

        /**
         * Constructor with String.format(fmt) specifiers for each column.
         *
         * @param fmtArgs String.format(fmt) specifiers for each column
         */
        public TableFormatter(String... fmtArgs) {
            this((StringBuilder) null, fmtArgs);
        }

        /**
         * Constructor with external collector of table rows and String.format(fmt) specifiers for each column.
         *
         * @param sb external collector for table rows
         * @param fmtArgs String.format(fmt) specifiers for each column
         */
        public TableFormatter(StringBuilder sb, String... fmtArgs) {
            this.sb = sb != null ? sb : new StringBuilder();
            this.fmts = Arrays.stream(fmtArgs)
                .toList();
            this.widths = fmts.stream()
                .map(fmt -> String.format(fmt, "")
                    .length())
                .toList();
        }

        /**
         * Add row to table. Each cell is formatted according to the column fmt specifier.
         *
         * @param cells variable array of cells
         * @return chainable self-reference
         */
        public TableFormatter row(String... cells) {
            IntStream.range(0, Math.min(fmts.size(), cells.length))
                .forEach(i -> {
                    sb.append(fillCell(i, cells[i], t -> {
                        String fmt = fmts.get(i);
                        int i1 = fmt.indexOf('%');  // offset width by format chars, e.g. '%-20s'
                        int i2 = Math.max(fmt.indexOf('s'), fmt.indexOf('d'));  // end '%s', '%d'
                        int offset = fmt.length() - (i2 - i1) - 1;
                        // cut cell text to effective column width
                        t = t.substring(0, Math.min(t.length(), widths.get(i) - offset));
                        return String.format(fmt, t);
                    }));
                });
            return this.endRow();
        }

        /**
         * Add line comprised of segments for each column to the table. Segments are drawn based on segment spefifiers with:
         * <pre>
         * seg: null    - empty or blank segment
         *      ""      - segment filled with default character: "-"
         *      "="     - segment is filled with provided character.
         * </pre>
         *
         * @param segs variable array of segment specifiers
         * @return chainable self-reference
         */
        public TableFormatter line(String... segs) {
            if (segs.length == 0) {    // print full line when segs is empty
                String[] args = fmts.stream()
                    .map(f -> "")
                    .toArray(String[]::new);
                return line(args);  // invoke recursively with ""-args
            }
            IntStream.range(0, Math.min(fmts.size(), segs.length))
                .forEach(i -> {
                    sb.append(fillCell(i, segs[i], s -> {
                        s = s.length() > 0 ? s.substring(0, 1) : "-"; // filler char
                        return String.format(fmts.get(i), "")
                            .replaceAll("[^\\|]", s)
                            .replaceAll("[\\|]", "+");
                    }));
                });
            return this.endRow();
        }

        /**
         * Getter to collected table content.
         *
         * @return table content
         */
        public StringBuilder get() {return sb;}

        /**
         * Fill table cell with provided text or spaces (blank cell).
         *
         * @param i column index
         * @param text text to fill
         * @param cellFiller upcall to format cell fitted to column
         * @return fitted String with text or spaces
         */
        private String fillCell(int i, String text, Function<String, String> cellFiller) {
            return text != null ? cellFiller.apply(text) : " ".repeat(widths.get(i));
        }

        /**
         * End row with trailing {@code "\n"}.
         *
         * @return chainable self-reference
         */
        private TableFormatter endRow() {
            sb.append("\n");
            return this;
        }
    }
}
