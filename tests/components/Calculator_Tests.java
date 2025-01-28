package components;

import components.impl.ComponentsImpl;
import components.impl.OrderBuilderImpl;
import datamodel.OrderItem;
import org.junit.jupiter.api.*;

import datamodel.Article;
import datamodel.Customer;
import datamodel.Pricing.PricingCategory;
import datamodel.Pricing.TAXRate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;


/**
 * Test class to test calculator methods in Application_e12.java: - 100: long calculateIncludedVAT(long grossValue, double taxRate) { return 0L; } - 200: long calculateOrderItemValue(OrderItem item,
 * Pricing pricing) { return 0L; } - 300: long calculateOrderItemVAT(OrderItem item, Pricing pricing) { return 0L; } - 400: long calculateOrderValue(Order order) { return 0L; } - 500: long
 * calculateOrderVAT(Order order) { return 0L; }
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Calculator_Tests {
    static final Calculator e12 = ComponentsImpl.getInstance().getCalculator();

    static final double GermanVAT = 19.0;

    static final double GermanVAT_reduced = 7.0;

    static final Map<Long, Customer> customers = new HashMap<>();

    static final Map<String, Article> articles = new HashMap<>();

    static final DataFactory dataFactory = DataFactory.getInstance();

    static final OrderBuilderImpl orderBuilderBasePricing =
        dataFactory.createOrderBuilder(PricingCategory.BasePricing, customerSpec -> findCustomerBySpec(customerSpec), articleSpec -> findArticleBySpec(articleSpec));

    static final OrderBuilderImpl orderBuilderSwissPricing =
        dataFactory.createOrderBuilder(PricingCategory.SwissPricing, customerSpec -> findCustomerBySpec(customerSpec), articleSpec -> findArticleBySpec(articleSpec));

    /**
     * Find {@link Customer} object in {@link customers} map by a specification, which is the first match by {@code id} or in the {@code lastName} or in the {@code firstName} attribute (in that
     * order).
     *
     * @param customerSpec specification of a customer by id or by name
     * @return {@link Customer} object or empty Optional
     */
    private static Optional<Customer> findCustomerBySpec(String customerSpec) {
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
    private static Optional<Article> findArticleBySpec(String articleSpec) {
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
     * Method is executed once before any @Test method.
     *
     * @throws Exception if any exception occurs
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        final DataFactory dataFactory = DataFactory.getInstance();
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

        List.of(dataFactory.createArticle("Tasse", 299, PricingCategory.BasePricing), dataFactory.createArticle("Becher", 149, PricingCategory.BasePricing),
                dataFactory.createArticle("Kanne", 1999, PricingCategory.BasePricing), dataFactory.createArticle("Teller", 649, PricingCategory.BasePricing),
                dataFactory.createArticle("Buch 'Java'", 4990, PricingCategory.BasePricing, TAXRate.Reduced), dataFactory.createArticle("Buch 'UML'", 7995, PricingCategory.BasePricing, TAXRate.Reduced),
                dataFactory.createArticle("Pfanne", 4999, PricingCategory.BasePricing), dataFactory.createArticle("Fahrradhelm", 16900, PricingCategory.BasePricing),
                dataFactory.createArticle("Fahrradkarte", 695, PricingCategory.BasePricing, TAXRate.Reduced))
            .stream()
            .flatMap(Optional::stream)
            .forEach(article -> articles.put(article.getId(), article));
    }

    // build Eric's order for BasePricing:
    // +----------+-------------------------------------------------+-----------------+
    // |Bestell-ID| Bestellungen                     MwSt*     Preis|   MwSt    Gesamt|
    // +----------+-------------------------------------------------+-----------------+
    // |8592356245| Eric's Bestellung:                              |                 |
    // |          |  - 4x Teller, 4x 6.49            4.14      25.96|                 |
    // |          |  - 8x Becher, 8x 1.49            1.90      11.92|                 |
    // |          |  - 1x Buch 'UML'                 5.23*     79.95|                 |
    // |          |  - 4x Tasse, 4x 2.99             1.91      11.96|  13.18    129.79|
    // +----------+-------------------------------------------------+-----------------+
    final datamodel.Order ericsOrderBasePricing = orderBuilderBasePricing.buildOrder("Eric", order -> order.item(4, "Teller")                 // + item: 4 Teller, 4x 6.49 €
            .item(8, "Becher")                 // + item: 8 Becher, 8x 1.49 €
            .item(1, "Buch 'UML'")             // + item: 1 Buch "UML", 1x 79.95 €, 7% MwSt (5.23€)
            .item(4, "Tasse")                  // + item: 4 Tassen, 4x 2.99 €
        )
        .get();

    // build Eric's order for SwissPricing:
    // +----------+-------------------------------------------------+-----------------+
    // |8592356245| Eric's Bestellung (in CHF):                     |                 |
    // |          |  - 4x Teller, 4x 11.69           3.50      46.76|                 |
    // |          |  - 8x Becher, 8x 2.69            1.61      21.52|                 |
    // |          |  - 1x Buch 'UML'                 3.65*    143.95|                 |
    // |          |  - 4x Tasse, 4x 5.39             1.62      21.56|  10.38    233.79|
    // +----------+-------------------------------------------------+-----------------+
    final datamodel.Order ericsOrderSwissPricing = orderBuilderSwissPricing.buildOrder("Eric", order -> order.item(4, "Teller")                 // + item: 4 Teller, 4x 6.49 €
            .item(8, "Becher")                 // + item: 8 Becher, 8x 1.49 €
            .item(1, "Buch 'UML'")             // + item: 1 Buch "UML", 1x 79.95 €, 7% MwSt (5.23€)
            .item(4, "Tasse")                  // + item: 4 Tassen, 4x 2.99 €
        )
        .get();


    @Test
    @Order(100)
    void test_100_calculateIncludedVAT_regular() {
        // calculate included VAT tax of 100.00 EUR at 19%, which is 15.97 EUR
        long actual = e12.calculateIncludedVAT(10000, GermanVAT);
        assertEquals(1597L, actual);
        //
        actual = e12.calculateIncludedVAT(999, GermanVAT);
        assertEquals(160L, actual);
        //
        actual = e12.calculateIncludedVAT(199999, GermanVAT);
        assertEquals(31933L, actual);
        //
        actual = e12.calculateIncludedVAT(14661, GermanVAT);
        assertEquals(2341L, actual);
    }

    @Test
    @Order(110)
    void test_110_calculateIncludedVAT_rounding_tests() {
        // see https://hilfe.sevdesk.de/de/articles/9423755-die-kaufmannische-rundungsdifferenz-darum-unterscheidet-sich-der-endbetrag-von-brutto-und-nettorechnungen
        //
        // 19%: 16.7181 -> 16.72
        long actual = e12.calculateIncludedVAT(10471, GermanVAT);
        assertEquals(1672, actual);
        //
        // 19%: 119.00 -> 19.0000000 -> 19.00
        assertEquals(19, e12.calculateIncludedVAT(119, GermanVAT));
        //
        // 19%: 52.36 -> 8.360000000 -> 8.36
        assertEquals(836, e12.calculateIncludedVAT(5236, GermanVAT));
        //
        // 19%: 99.92 -> 15.953613 -> 15.95
        assertEquals(1595, e12.calculateIncludedVAT(9992, GermanVAT));
        //
        // 19%: 99.93 -> 15.955210 -> 15.96
        assertEquals(1596, e12.calculateIncludedVAT(9993, GermanVAT));
    }

    @Test
    @Order(120)
    void test_120_calculateIncludedVAT_corner_cases_tests() {
        long actual = e12.calculateIncludedVAT(0, GermanVAT);
        assertEquals(0L, actual);
        //
        actual = e12.calculateIncludedVAT(Long.MAX_VALUE, GermanVAT);
        assertEquals(1472639232775132416L, actual);
        //
        // return 0L for negative gross values, e.g. Long.MIN_VALUE
        actual = e12.calculateIncludedVAT(Long.MIN_VALUE, GermanVAT);
        assertEquals(0L, actual);
    }

    /*
     * expected included tax (in cent) for input: index * 100L at GermanVAT, 19.0%
     */
    static final int[] p19percent =
        {0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 224, 239, 255, 271, 287, 303, 319, 335, 351, 367, 383, 399, 415, 431, 447, 463, 479, 495, 511, 527, 543, 559, 575, 591, 607, 623,
            639, 655, 671, 687, 703, 718, 734, 750, 766, 782, 798, 814, 830, 846, 862, 878, 894, 910, 926, 942, 958, 974, 990, 1006, 1022, 1038, 1054, 1070, 1086, 1102, 1118, 1134, 1150, 1166, 1182,
            1197, 1213, 1229, 1245, 1261, 1277, 1293, 1309, 1325, 1341, 1357, 1373, 1389, 1405, 1421, 1437, 1453, 1469, 1485, 1501, 1517, 1533, 1549, 1565, 1581, 1597, 1613, 1629, 1645, 1661, 1676,
            1692, 1708, 1724, 1740, 1756, 1772, 1788, 1804, 1820, 1836, 1852, 1868, 1884, 1900,};

    /*
     * expected included tax (in cent) for input: index * 100L at reduced GermanVAT, 7.0%
     */
    static final int[] p7percent =
        {0, 7, 13, 20, 26, 33, 39, 46, 52, 59, 65, 72, 79, 85, 92, 98, 105, 111, 118, 124, 131, 137, 144, 150, 157, 164, 170, 177, 183, 190, 196, 203, 209, 216, 222, 229, 236, 242, 249, 255, 262, 268,
            275, 281, 288, 294, 301, 307, 314, 321, 327, 334, 340, 347, 353, 360, 366, 373, 379, 386, 393, 399, 406, 412, 419, 425, 432, 438, 445, 451, 458, 464, 471, 478, 484, 491, 497, 504, 510,
            517, 523, 530, 536, 543, 550, 556, 563, 569, 576, 582, 589, 595, 602, 608, 615, 621, 628, 635, 641, 648, 654, 661, 667, 674, 680, 687, 693, 700, 707, 713, 720, 726, 733, 739, 746, 752,
            759, 765, 772, 779,};

    @Test
    @Order(130)
    void test_130_calculateIncludedVAT_bulk_19pct_tests() {
        IntStream.range(0, p19percent.length)
            .forEach(i -> {
                long actual = e12.calculateIncludedVAT((long) i * 100L, GermanVAT);
                long expected = p19percent[i];
                assertEquals(expected, actual);
            });
    }

    @Test
    @Order(131)
    void test_131_calculateIncludedVAT_bulk_7pct_tests() {
        IntStream.range(0, 2) //p7percent.length)
            .forEach(i -> {
                long actual = e12.calculateIncludedVAT((long) i * 100L, GermanVAT_reduced);
                long expected = p7percent[i];
                assertEquals(expected, actual);
            });
    }


    @Test
    @Order(200)
    void test_200_calculateOrderItemValue_tests() {
        var order = ericsOrderBasePricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        // extract order items to items[] array
        OrderItem items[] = StreamSupport.stream(order.getOrderItems()
                .spliterator(), false)
            .toArray(OrderItem[]::new);
        //
        // test order items values
        // item 0: 4 Teller, 4x 6.49 = 25.96
        long actual = e12.calculateOrderItemValue(items[0], order.getPricing());
        assertEquals(2596, actual);
        //
        // item 1: 8 Becher, 8x 1.49 = 11.92
        actual = e12.calculateOrderItemValue(items[1], order.getPricing());
        assertEquals(1192, actual);
        //
        // item 2: 1 Buch "UML", 1x 79.95
        actual = e12.calculateOrderItemValue(items[2], order.getPricing());
        assertEquals(7995, actual);
        //
        // item 3: 4 Tassen, 4x 2.99 = 11.96
        actual = e12.calculateOrderItemValue(items[3], order.getPricing());
        assertEquals(1196, actual);
    }

    @Test
    @Order(210)
    void test_210_calculateOrderItemValue_SwissPricing_tests() {
        //
        // Eric's order in SwissPricing:
        // +----------+-------------------------------------------------+-----------------+
        // |8592356245| Eric's Bestellung (in CHF):                     |                 |
        // |          |  - 4x Teller, 4x 11.69           3.50      46.76|                 |
        // |          |  - 8x Becher, 8x 2.69            1.61      21.52|                 |
        // |          |  - 1x Buch 'UML'                 3.65*    143.95|                 |
        // |          |  - 4x Tasse, 4x 5.39             1.62      21.56|  10.38    233.79|
        // +----------+-------------------------------------------------+-----------------+
        //
        var order = ericsOrderSwissPricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        // extract order items to items[] array
        OrderItem items[] = StreamSupport.stream(order.getOrderItems()
                .spliterator(), false)
            .toArray(OrderItem[]::new);
        //
        // test order items values
        // item 0: 4 Teller, 4x 11.69 = 46.76 CHF
        long actual = e12.calculateOrderItemValue(items[0], order.getPricing());
        assertEquals(4676, actual);
        //
        // item 1: 8 Becher, 8x 2.69 = 21.52 CHF
        actual = e12.calculateOrderItemValue(items[1], order.getPricing());
        assertEquals(2152, actual);
        //
        // item 2: 1 Buch "UML", 1x 143.95 CHF
        actual = e12.calculateOrderItemValue(items[2], order.getPricing());
        assertEquals(14395, actual);
        //
        // item 3: 4 Tassen, 4x 5.39 = 21.56 CHF
        actual = e12.calculateOrderItemValue(items[3], order.getPricing());
        assertEquals(2156, actual);
    }


    @Test
    @Order(300)
    void test_300_calculateOrderItemVAT_tests() {
        var order = ericsOrderBasePricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        // extract order items to items[] array
        OrderItem items[] = StreamSupport.stream(order.getOrderItems()
                .spliterator(), false)
            .toArray(OrderItem[]::new);
        //
        // test order items VAT in BasePricing
        // item 0: 4 Teller, 4x 6.49 = 25.96, VAT of 19%: 4.14 EUR
        long actual = e12.calculateOrderItemVAT(items[0], order.getPricing());
        assertEquals(414, actual);
        //
        // item 1: 8 Becher, 8x 1.49 = 11.92, VAT of 19%: 1.90 EUR
        actual = e12.calculateOrderItemVAT(items[1], order.getPricing());
        assertEquals(190, actual);
        //
        // item 2: 1 Buch "UML", 1x 79.95, VAT of 7% (reduced): 5.23 EUR
        actual = e12.calculateOrderItemVAT(items[2], order.getPricing());
        assertEquals(523, actual);
        //
        // item 3: 4 Tassen, 4x 2.99 = 11.96, VAT of 19%: 1.91 EUR
        actual = e12.calculateOrderItemVAT(items[3], order.getPricing());
        assertEquals(191, actual);
    }

    @Test
    @Order(310)
    void test_310_calculateOrderItemVAT_SwissPricing_tests() {
        //
        // Eric's order in SwissPricing:
        // +----------+-------------------------------------------------+-----------------+
        // |8592356245| Eric's Bestellung (in CHF):                     |                 |
        // |          |  - 4x Teller, 4x 11.69           3.50      46.76|                 |
        // |          |  - 8x Becher, 8x 2.69            1.61      21.52|                 |
        // |          |  - 1x Buch 'UML'                 3.65*    143.95|                 |
        // |          |  - 4x Tasse, 4x 5.39             1.62      21.56|  10.38    233.79|
        // +----------+-------------------------------------------------+-----------------+
        //
        var order = ericsOrderSwissPricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        // extract order items to items[] array
        OrderItem items[] = StreamSupport.stream(order.getOrderItems()
                .spliterator(), false)
            .toArray(OrderItem[]::new);
        //
        // test order items VAT in SwissPricing
        // item 0: 4 Teller, 4x 11.69 = 46.76 CHF, VAT of 8.1%: 3.50 CHF
        long actual = e12.calculateOrderItemVAT(items[0], order.getPricing());
        assertEquals(350, actual);
        //
        // item 1: 8 Becher, 8x 2.69 = 21.52 CHF, VAT of 8.1%: 1.61 CHF
        actual = e12.calculateOrderItemVAT(items[1], order.getPricing());
        assertEquals(161, actual);
        //
        // item 2: 1 Buch "UML", 1x 143.95 CHF, VAT of 2.6% (reduced): 3.65
        actual = e12.calculateOrderItemVAT(items[2], order.getPricing());
        assertEquals(365, actual);
        //
        // item 3: 4 Tassen, 4x 5.39 = 21.56 CHF, VAT of 8.1%: 1.62
        actual = e12.calculateOrderItemVAT(items[3], order.getPricing());
        assertEquals(162, actual);
    }


    @Test
    @Order(400)
    void test_400_calculateOrderValue_tests() {
        var order = ericsOrderBasePricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        long actual = e12.calculateOrderValue(order);
        assertEquals(12979, actual);
    }

    @Test
    @Order(410)
    void test_410_calculateOrderValue_SwissPricing_tests() {
        var order = ericsOrderSwissPricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        long actual = e12.calculateOrderValue(order);
        assertEquals(23379, actual);
    }


    @Test
    @Order(500)
    void test_500_calculateOrderVAT_tests() {
        var order = ericsOrderBasePricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        long actual = e12.calculateOrderVAT(order);
        assertEquals(1318, actual);
    }

    @Test
    @Order(510)
    void test_510_calculateOrderVAT_SwissPricing_tests() {
        var order = ericsOrderSwissPricing;
        assertNotNull(order);
        assertEquals(order.itemsCount(), 4);
        //
        long actual = e12.calculateOrderVAT(order);
        assertEquals(1038, actual);
    }
}