package components.impl;

import components.Formatter;
import components.TableFormatter;
import datamodel.Customer;
import datamodel.Pricing;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

final public class TableFormatterImpl implements TableFormatter, Formatter {
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
    public TableFormatterImpl(String... fmtArgs) {
        this((StringBuilder) null, fmtArgs);
    }

    /**
     * Constructor with external collector of table rows and String.format(fmt) specifiers for each column.
     *
     * @param sb external collector for table rows
     * @param fmtArgs String.format(fmt) specifiers for each column
     */
    public TableFormatterImpl(StringBuilder sb, String... fmtArgs) {
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
    private TableFormatterImpl endRow() {
        sb.append("\n");
        return this;
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
    public String fmtCustomerContact(Customer customer, int style) {
        if (customer == null)
            throw new IllegalArgumentException("argument customer: null");
        //
        var len = customer.contactsCount();
        final int ft = style;  // 0 is default format
        switch (ft) {    // 0 is default
            case 0:
                var it = customer.getContacts()
                    .iterator();
                return String.format("%s", it.hasNext() ? it.next() : "");

            case 1:
                String ext = len > 1 ? String.format(", (+%d contacts)", len - 1) : "";
                return String.format("%s%s", fmtCustomerContact(customer, 0), ext);

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
                return fmtCustomerContact(customer, 0);
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
    public String fmtCustomerName(Customer customer, int style) {
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
        switch (style) {    // 0 is default
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
                return fmtCustomerName(customer, style - 10).toUpperCase();
            //
            default:
                return fmtCustomerName(customer, 0);
        }
    }

    @Override
    public TableFormatter createTableFormatter(String... columnSpecs) {
        return new TableFormatterImpl(columnSpecs);
    }
}
