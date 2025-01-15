package application;

import application.Runtime.Bean;
import datamodel.Customer;
import datamodel.DataFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;


/**
 * Driver class for the <i>c4-customer</i> assignment. Class creates {@link Customer}
 * objects and prints a customer table.
 * Class implements the {@link Runtime.Runnable} interface.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
@Bean(priority=4)
public class Application_C4 implements Runtime.Runnable {

    /**
     * Reference to {@link DataFactory} singleton.
     */
    private final DataFactory dataFactory = DataFactory.getInstance();

    /**
     * None-public default constructor (to avoid javadoc warning).
     */
    public Application_C4() { }


    /**
     * Method of the {@link Runtime.Runnable} interface called by {@link Runtime}.
     * Program execution starts here.
     * @param properties properties from the {@code application.properties} file
     * @param args arguments passed from the command line
     */
    @Override
    public void run(Properties properties, String[] args) {
        //
        List<Customer> customers = List.of(
            // 
            dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com")
                .map(c -> c.addContact("eric98@yahoo.com").addContact("(030) 3945-642298")),
            // 
            dataFactory.createCustomer("Anne Bayer", "anne24@yahoo.de")
                .map(c -> c.addContact("(030) 3481-23352").addContact("fax: (030)23451356")),
            // 
            dataFactory.createCustomer("Schulz-Mueller, Tim", "tim2346@gmx.de"),
            dataFactory.createCustomer("Blumenfeld, Nadine-Ulla", "+49 152-92454"),
            dataFactory.createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210"),
            // 
            // attempts to create Customer objects from invalid arguments
            // invalid email address, no object is created
            dataFactory.createCustomer("Mandy Mondschein", "locomandy<>gmx.de")
                .map(c -> c.addContact("+49 030-3956256")), // and no other (valid) contact is added
            dataFactory.createCustomer("", "nobody@gmx.de") // invalid name, no object is created
        //
        ).stream()
            .filter(c -> c.isPresent())
            .map(c -> c.get())
            .toList();

        // define table for output
        final TableFormatter tf = new TableFormatter(
                "| %8s ", "| %-32s", "| %-31s |")   // column specification
            .line()
            .row("Kund.-ID", "Name", "Kontakt")     // table header
            .line();

        // fill objects as rows into Customer table
        customers.stream()
            // @REMOVE
            // .sorted((c1, c2) -> c1.getLastName().compareTo(c2.getLastName()))
            // @/REMOVE
            .forEach(c -> {
                String id=String.format("%d", c.getId());
                String name=fmtCustomerName(c, 0);
                String contact=fmtCustomerContacts(c, 1);
                // 
                tf.row(id, name, contact);  // write row into table
            });
        //
        tf.line();

        // print numbers of objects in collections
        System.out.println(String.format(
            "(%d) Customer objects built.\n---",
            customers.size()));

        // print Customer table
        System.out.println(tf.get().toString());
    }

    /**
     * JavaVM entry method that calls the {@link Runtime}, which creates
     * an instance of this class and invokes the
     * {@code run(properties, args)} - method.
     * @param args arguments passed from command line
     */
    public static void main(String[] args) {
        Runtime.run(args);
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
     * @param customer Customer object
     * @param style name formatting style
     * @return Customer name formatted according to the selcted style
     * @throws IllegalArgumentException with null arguments
     */
    public String fmtCustomerName(Customer customer, int... style) {
        if(customer==null)
            throw new IllegalArgumentException("argument customer: null");
        //
        String ln = customer.getLastName();
        String fn = customer.getFirstName();
        String fn1 = fn.length()==0? "" : fn.substring(0, 1).toUpperCase();
        //
        final int ft = style.length > 0? style[0] : 0;  // 0 is default format
        switch(ft) {    // 0 is default
        case 0: return String.format(fn.length() > 0? "%s, %s" : "%s", ln, fn);
        case 1: return String.format(fn.length() > 0? "%s %s" : "%s%s", fn, ln);
        case 2: return String.format(fn.length() > 0? "%s, %s." : "%s", ln, fn1);
        case 3: return String.format(fn.length() > 0? "%s. %s" : "%s%s", fn1, ln);
        case 4: return ln;
        case 5: return fn.length() > 0? fn : ln;
        //
        case 10: case 11: case 12: case 13: case 14: case 15:
            return fmtCustomerName(customer, ft - 10).toUpperCase();
        //
        default: return fmtCustomerName(customer, 0);
        }
    }

    /**
     * Format Customer contacts according to a format (0 is default):
     * <pre>
     * style: 0: first contact: "anne24@yahoo.de"
     *        1: first contact with extension indicator: "anne24@yahoo.de, (+2 contacts)"
     *        2: all contacts as list: "anne24@yahoo.de, (030) 3481-23352, fax: (030)23451356"
     * </pre>
     * @param customer Customer object
     * @param style name formatting style
     * @return Customer contact information formatted according to the selcted style
     */
    public String fmtCustomerContacts(Customer customer, int... style) {
        if(customer==null)
            throw new IllegalArgumentException("argument customer: null");
        //
        var len = customer.contactsCount();
        final int ft = style.length > 0? style[0] : 0;  // 0 is default format
        switch(ft) {    // 0 is default
        case 0:
            var it = customer.getContacts().iterator();
            return String.format("%s", it.hasNext()? it.next() : "");

        case 1:
            String ext = len > 1? String.format(", (+%d contacts)", len - 1) : "";
            return String.format("%s%s", fmtCustomerContacts(customer, 0), ext);

        case 2:
            StringBuilder sb = new StringBuilder();
            it = customer.getContacts().iterator();
            while(it.hasNext()) {
                sb.append(it.next()).append(sb.length() > 0? ", " : "");
            }
            return sb.toString();
        //
        default: return fmtCustomerContacts(customer, 0);
        }
    }


    /**
     * {@link TableFormatter} produces text in form of a table defined by columns
     * of specified <i>width</i> and <i>alignment</i>. {@code String.format(fmt)}
     * specifications: <i>fmt</i> format cells in a row according to <i>width</i>
     * and <i>alignment</i> specifications.
     * @author sgra64
     */
    static class TableFormatter {

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
         * @param fmtArgs String.format(fmt) specifiers for each column
         */
        public TableFormatter(String... fmtArgs) {
            this((StringBuilder)null, fmtArgs);
        }

        /**
         * Constructor with external collector of table rows and String.format(fmt)
         * specifiers for each column.
         * @param sb external collector for table rows
         * @param fmtArgs String.format(fmt) specifiers for each column
         */
        public TableFormatter(StringBuilder sb, String... fmtArgs) {
            this.sb = sb != null? sb : new StringBuilder();
            this.fmts = Arrays.stream(fmtArgs).toList();
            this.widths = fmts.stream().map(fmt -> String.format(fmt, "").length()).toList();
        }

        /**
         * Add row to table. Each cell is formatted according to the column fmt specifier.
         * @param cells variable array of cells
         * @return chainable self-reference
         */
        public TableFormatter row(String... cells) {
            IntStream.range(0, Math.min(fmts.size(), cells.length)).forEach(i -> {
                sb.append(fillCell(i, cells[i], t -> {
                    String fmt = fmts.get(i);
                    int i1 = fmt.indexOf('%');  // offset width by format chars, e.g. '%-20s'
                    int i2 = Math.max(fmt.indexOf('s'), fmt.indexOf('d'));  // end '%s', '%d'
                    int offset = fmt.length() - (i2 - i1) -1;
                    // cut cell text to effective column width
                    t = t.substring(0, Math.min(t.length(), widths.get(i) - offset));
                    return String.format(fmt, t);
                }));
            });
            return this.endRow();
        }

        /**
         * Add line comprised of segments for each column to the table.
         * Segments are drawn based on segment spefifiers with:
         * <pre>
         * seg: null    - empty or blank segment
         *      ""      - segment filled with default character: "-"
         *      "="     - segment is filled with provided character.
         * </pre>
         * @param segs variable array of segment specifiers
         * @return chainable self-reference
         */
        public TableFormatter line(String... segs) {
            if(segs.length==0) {    // print full line when segs is empty
                String[] args = fmts.stream().map(f -> "").toArray(String[]::new);
                return line(args);  // invoke recursively with ""-args
            }
            IntStream.range(0, Math.min(fmts.size(), segs.length)).forEach(i -> {
                sb.append(fillCell(i, segs[i], s -> {
                    s = s.length() > 0? s.substring(0, 1) : "-"; // filler char
                    return String.format(fmts.get(i), "")
                            .replaceAll("[^\\|]", s).replaceAll("[\\|]", "+");
                }));
            });
            return this.endRow();
        }

        /**
         * Getter to collected table content.
         * @return table content
         */
        public StringBuilder get() { return sb; }

        /**
         * Fill table cell with provided text or spaces (blank cell).
         * @param i column index
         * @param text text to fill
         * @param cellFiller upcall to format cell fitted to column
         * @return fitted String with text or spaces
         */
        private String fillCell(int i, String text, Function<String, String> cellFiller) {
            return text != null? cellFiller.apply(text) : " ".repeat(widths.get(i));
        }

        /**
         * End row with trailing {@code "\n"}.
         * @return chainable self-reference
         */
        private TableFormatter endRow() { sb.append("\n"); return this; }
    }
}
