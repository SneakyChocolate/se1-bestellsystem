package components.impl;

import components.Components;
import components.Printer;
import components.TableFormatter;
import datamodel.Article;
import datamodel.Customer;
import datamodel.Order;
import datamodel.Pricing;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

final class PrinterImpl implements Printer {
    /**
     * Print objects of class {@link Article} as table row into a {@link StringBuilder}.
     *
     * @param articles articles to print as row into table
     * @param pricingCategory {@link Pricing.PricingCategory} used to print articles (tax rate, currency)
     * @return StringBuilder with articles rendered in table format
     * @throws IllegalArgumentException with null arguments
     */
    public StringBuilder printArticles(Collection<Article> articles, Pricing.PricingCategory pricingCategory) {
        if (articles == null)
            throw new IllegalArgumentException("argument articles: null");
        //
        var pricing = pricingCategory.pricing();
        //
        final TableFormatter tf = Components.getInstance().getFormatter().createTableFormatter(
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
                var price = Components.getInstance().getFormatter().fmtPrice(pricing.unitPrice(article), pricing.currency(), 1);
                var taxRate = pricing.taxRate(article);
                double percent = pricing.taxRateAsPercent(article);
                long lrd = (long) (percent * 10.0) % 10;
                var vatRate = String.format((lrd == 0L ? "%.0f%s" : "%.1f%s"), percent, "%");
                String vat = taxRate == Pricing.TAXRate.Regular ? " normal" : taxRate == Pricing.TAXRate.Reduced ? " reduziert" : taxRate == Pricing.TAXRate.Excempt ? " excempt" : "";
                //
                tf.row(id, description, price, vatRate, vat);  // write row into table
            });
        return tf.line()
            .get();
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
        final TableFormatter tf = Components.getInstance().getFormatter().createTableFormatter(
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
                var name = Components.getInstance().getFormatter().fmtCustomerName(c,0);
                var contact = Components.getInstance().getFormatter().fmtCustomerContact(c, 1);
                //
                tf.row(id, name, contact);  // write row into table
            });
        return tf.line()
            .get();
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
            return cur== Pricing.Currency.Euro? "" : String.format(" (in %s)", cur.code());
        }).orElse("");
        //
        // final var tf = new TableFormatter(
        final var tf = Components.getInstance().getFormatter().createTableFormatter(
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
                long orderValue = Components.getInstance().getCalculator().calculateOrderValue(order);
                long orderVAT = Components.getInstance().getCalculator().calculateOrderVAT(order);
                //
                // print Order as row:
                printOrder(order, orderValue, orderVAT, tf).line();
                //
                // compound order and tax values
                compound[0] += orderValue;
                compound[1] += orderVAT;
            });
        //
        tf.row(null, null, null, null, "Gesamt:", Components.getInstance().getFormatter().fmtPrice(compound[1], Pricing.Currency.Euro,0), Components.getInstance().getFormatter().fmtPrice(compound[0], Pricing.Currency.Euro,0));
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
            long value = Components.getInstance().getCalculator().calculateOrderItemValue(item, pricing);
            long vat = Components.getInstance().getCalculator().calculateOrderItemVAT(item, pricing);
            var taxRate = pricing.taxRate(article);
            var reducedTaxMarker = taxRate== Pricing.TAXRate.Reduced? "*" : "";
            String itemDescr = String.format(" - %dx %s%s",
                unitsOrdered, descr, unitsOrdered > 1?
                    String.format(", %dx %s", unitsOrdered, Components.getInstance().getFormatter().fmtPrice(unitPrice, currency,0)) :
                    String.format("")
            );
            String[] totals = i < order.itemsCount() - 1?   // last row?
                new String[] { "", ""} :
                new String[] { Components.getInstance().getFormatter().fmtPrice(orderVAT, currency,0), Components.getInstance().getFormatter().fmtPrice(orderValue, currency,0) };
            //
            // item rows with item description, VAT, value and totals in the last row
            tf.row("", itemDescr, Components.getInstance().getFormatter().fmtPrice(vat, currency,0), reducedTaxMarker, Components.getInstance().getFormatter().fmtPrice(value, currency,0), totals[0], totals[1]);
        };
        return tf;
    }
}
