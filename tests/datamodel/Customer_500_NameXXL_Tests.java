package datamodel;

import components.DataFactory;
import components.impl.ComponentsImpl;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;


/**
 * Tests for Customer class: [500..599] setName(name) extended
 * test cases:
 *  - Test cases 500: setName(name) with '-' connected multi-part last names.
 *  - Test cases 510: setName(name) with double first names.
 *  - Test cases 520: setName(name) with multiple first names.
 *  - Test cases 530: setName(name) with multiple first and last names.
 *  - Test cases 550: setName(name) with multiple, multi-dash first and last names.
 *  - Test cases 550: setName(name) with extreme long names.
 * @author sgra64
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Customer_500_NameXXL_Tests {

    /*
     * Reference to DataFactory.
     */
    private final DataFactory dataFactory = ComponentsImpl.getInstance().getDataFactory();


    /*
     * Test cases 500: setName(name) with '-' connected multi-part last names.
     */
    @Test @Order(500)
    void test500_getNameMultipartLastName() {
        // c1.setName("Tim Schulz-Mueller-Meyer");     // name style 1 with multi-part last name
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller-Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Tim Schulz-Mueller-Meyer", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller-Meyer", c1.getLastName());
    }

    @Test @Order(501)
    void test501_getNameMultipartLastName() {
        // c1.setName("Schulz-Mueller-Meyer, Tim");    // name style 2
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller-Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Schulz-Mueller-Meyer, Tim", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller-Meyer", c1.getLastName());
    }

    @Test @Order(502)
    void test502_getNameMultipartLastName() {
        // c1.setName("Schulz-Mueller-Meyer; Tim");    // name style 3
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller-Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Schulz-Mueller-Meyer; Tim", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller-Meyer", c1.getLastName());
    }


    /*
     * Test cases 510: setName(name) with double first names.
     */
    @Test @Order(510)
    void test510_getNameDoubleFirstName() {
        // c1.setName("Nadine Ulla Blumenfeld");       // name style 1
        // assertEquals("Nadine Ulla", c1.getFirstName());
        // assertEquals("Blumenfeld", c1.getLastName());
        var c = dataFactory.createCustomer("Nadine Ulla Blumenfeld", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Nadine Ulla", c1.getFirstName());
        assertEquals("Blumenfeld", c1.getLastName());
    }

    @Test @Order(511)
    void test511_getNameDoubleFirstName() {
        // c1.setName("Blumenfeld, Nadine Ulla");      // name style 2
        // assertEquals("Nadine Ulla", c1.getFirstName());
        // assertEquals("Blumenfeld", c1.getLastName());
        var c = dataFactory.createCustomer(" Blumenfeld , Nadine  Ulla  ", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Nadine Ulla", c1.getFirstName());
        assertEquals("Blumenfeld", c1.getLastName());
    }

    @Test @Order(512)
    void test512_getNameDoubleFirstName() {
        // c1.setName("Blumenfeld; Nadine Ulla");      // name style 3
        // assertEquals("Nadine Ulla", c1.getFirstName());
        // assertEquals("Blumenfeld", c1.getLastName());
        var c = dataFactory.createCustomer(" Blumenfeld  ;  Nadine  Ulla  ", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Nadine Ulla", c1.getFirstName());
        assertEquals("Blumenfeld", c1.getLastName());
    }


    /*
     * Test cases 520: setName(name) with multiple first names.
     */
    @Test @Order(520)
    void test520_getNameMultipartFirstNames() {
        // c1.setName("Nadine Ulla Maxine Blumenfeld");    // name style 1
        // assertEquals("Nadine Ulla Maxine", c1.getFirstName());
        // assertEquals("Blumenfeld", c1.getLastName());
        var c = dataFactory.createCustomer("Nadine Ulla Maxine Blumenfeld", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Nadine Ulla Maxine", c1.getFirstName());
        assertEquals("Blumenfeld", c1.getLastName());
    }

    @Test @Order(521)
    void test521_getNameMultipartFirstNames() {
        // c1.setName("Blumenfeld, Nadine Ulla Maxine");   // name style 2
        // assertEquals("Nadine Ulla Maxine", c1.getFirstName());
        // assertEquals("Blumenfeld", c1.getLastName());
        var c = dataFactory.createCustomer(" Blumenfeld , Nadine Ulla Maxine ", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Nadine Ulla Maxine", c1.getFirstName());
        assertEquals("Blumenfeld", c1.getLastName());
    }

    @Test @Order(522)
    void test522_getNameMultipartFirstNames() {
        // c1.setName("Blumenfeld; Nadine Ulla Maxine");   // name style 3
        // assertEquals("Nadine Ulla Maxine", c1.getFirstName());
        // assertEquals("Blumenfeld", c1.getLastName());
        var c = dataFactory.createCustomer(" Blumenfeld ; Nadine Ulla Maxine ", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Nadine Ulla Maxine", c1.getFirstName());
        assertEquals("Blumenfeld", c1.getLastName());
    }


    /*
     * Test cases 530: setName(name) with multiple first and last names.
     */
    @Test @Order(530)
    void test530_getNameMultipartFirstNames() {
        // c1.setName("Khaled Mohamed Arif Saad-Abdelalim");   // name style 1
        // assertEquals("Khaled Mohamed Arif", c1.getFirstName());
        // assertEquals("Saad-Abdelalim", c1.getLastName());
        var c = dataFactory.createCustomer(" Khaled Mohamed Arif Saad-Abdelalim ", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Khaled Mohamed Arif", c1.getFirstName());
        assertEquals("Saad-Abdelalim", c1.getLastName());
    }

    @Test @Order(531)
    void test531_getNameMultipartNames() {
        // c1.setName("Saad-Abdelalim, Khaled Mohamed-Arif");  // name style 2
        // assertEquals("Khaled Mohamed-Arif", c1.getFirstName());
        // assertEquals("Saad-Abdelalim", c1.getLastName());
        var c = dataFactory.createCustomer(" Saad-Abdelalim  , Khaled Mohamed-Arif ", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Khaled Mohamed-Arif", c1.getFirstName());
        assertEquals("Saad-Abdelalim", c1.getLastName());
    }


    /*
     * Test cases 550: setName(name) with multiple, multi-dash first and last names.
     */
    @Test @Order(550)
    void test550_getNameMultiDashMultipartFirstNames() {
        // c1.setName("Khaled-Mohamed Arif Saad-Abdelalim");   // name style 1
        // assertEquals("Khaled-Mohamed Arif", c1.getFirstName());
        // assertEquals("Saad-Abdelalim", c1.getLastName());
        var c = dataFactory.createCustomer(" Khaled-Mohamed Arif Saad-Abdelalim ", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Khaled-Mohamed Arif", c1.getFirstName());
        assertEquals("Saad-Abdelalim", c1.getLastName());
    }

    @Test @Order(551)
    void test551_getNameMultiDashMultipartFirstNames() {
        // c1.setName("Khaled-Mohamed-Arif Saad-Abdelalim");   // name style 1
        // assertEquals("Khaled-Mohamed-Arif", c1.getFirstName());
        // assertEquals("Saad-Abdelalim", c1.getLastName());
        var c = dataFactory.createCustomer(" Khaled-Mohamed-Arif Saad-Abdelalim ", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Khaled-Mohamed-Arif", c1.getFirstName());
        assertEquals("Saad-Abdelalim", c1.getLastName());
    }

    @Test @Order(552)
    void test552_getNameMultipartNames() {
        // c1.setName("Khaled Mohamed-Arif Saad-Abdelalim");   // name style 1
        // assertEquals("Khaled Mohamed-Arif", c1.getFirstName());
        // assertEquals("Saad-Abdelalim", c1.getLastName());
        var c = dataFactory.createCustomer(" Khaled Mohamed-Arif Saad-Abdelalim ", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Khaled Mohamed-Arif", c1.getFirstName());
        assertEquals("Saad-Abdelalim", c1.getLastName());
    }

    @Test @Order(553)
    void test553_getNameMultiDashMultipartFirstNames() {
        // c1.setName("Khaled-Mohamed-Arif-Saad-Abdelalim");   // name style 1
        // assertEquals("", c1.getFirstName());
        // assertEquals("Khaled-Mohamed-Arif-Saad-Abdelalim", c1.getLastName());
        var c = dataFactory.createCustomer(" Khaled-Mohamed-Arif-Saad-Abdelalim ", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("", c1.getFirstName());
        assertEquals("Khaled-Mohamed-Arif-Saad-Abdelalim", c1.getLastName());
    }


    /*
     * Test cases 560: setName(name) with extreme long names.
     */
    @Test @Order(560)
    void test560_getNameExtremeLongNames() {
        // c1.setName("Auguste Viktoria Friederike Luise Feodora Jenny "
        //         + "von-Schleswig-Holstein-Sonderburg-Augustenburg");
        var c = dataFactory.createCustomer("Auguste Viktoria Friederike Luise Feodora Jenny "
                + "von-Schleswig-Holstein-Sonderburg-Augustenburg", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Auguste Viktoria Friederike Luise Feodora Jenny", c1.getFirstName());
        assertEquals("von-Schleswig-Holstein-Sonderburg-Augustenburg", c1.getLastName());
    }

    @Test @Order(561)
    void test561_getNameExtremeLongNames() {
        // c1.setName("Auguste Viktoria Friederike Luise Feodora Jenny "
        //         + "von-Schleswig-Holstein-Sonderburg-Augustenburg");
        var c = dataFactory.createCustomer("Auguste Viktoria Friederike Luise Feodora Jenny "
                + "von-Schleswig-Holstein-Sonderburg-Augustenburg", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Auguste Viktoria Friederike Luise Feodora Jenny", c1.getFirstName());
        assertEquals("von-Schleswig-Holstein-Sonderburg-Augustenburg", c1.getLastName());
    }

    @Test @Order(562)
    void test562_getNameExtremeLongNames() {
        // c1.setName("Karl-Theodor Maria Nikolaus Johann Jacob Philipp Franz Joseph Sylvester",
        //         "Buhl-Freiherr von und zu Guttenberg");
        var c = dataFactory.createCustomer("Buhl-Freiherr von und zu Guttenberg" + " , " +
            "Karl-Theodor Maria Nikolaus Johann Jacob Philipp Franz Joseph Sylvester ", "+49 1524-12948210");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Karl-Theodor Maria Nikolaus Johann Jacob Philipp Franz Joseph Sylvester", c1.getFirstName());
        assertEquals("Buhl-Freiherr von und zu Guttenberg", c1.getLastName());
    }
}
