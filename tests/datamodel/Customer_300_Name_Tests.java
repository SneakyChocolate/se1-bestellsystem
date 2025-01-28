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
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;


/**
 * Tests for Customer class: [300..399] setName(first, last), setName(name)
 * simple test cases:
 *  - Test cases 300: setName(firstName, lastName) with two arguments.
 *  - Test cases 310: setName(name) with single-string name argument.
 *  - Test cases 320: setName(name) with '-' connected double-part last names.
 *  - Test cases 330: single-Argument name Constructor
 * @author sgra64
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Customer_300_Name_Tests {

    /*
     * Reference to DataFactory.
     */
    private final DataFactory dataFactory = ComponentsImpl.getInstance().getDataFactory();

    /*
     * Test cases 300: setName(firstName, lastName) with two arguments.
     */
    @Test @Order(300)
    void test300_getNameFirstAndLastName() {
        // c1.setName("Eric", "Meyer");
        // assertEquals("Eric", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Eric", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
        var contacts = c1.getContacts();
        assertNotEquals(contacts, null);
        assertTrue(Iterable.class.isAssignableFrom(contacts.getClass()));
        var it = contacts.iterator();
        assertTrue(it.hasNext());
        var contact = it.next();
        assertEquals("eric98@yahoo.com", contact);
        assertFalse(it.hasNext());
    }

    @Test @Order(301)
    void test301_getNameFirstAndLastName() {
        // c1.setName("", "Meyer");    // lastName only
        // assertEquals("", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Meyer", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }

    @Test @Order(302)
    void test302_getNameFirstAndLastName() {
        // IllegalArgumentException thrown =
        //     assertThrows(
        //         IllegalArgumentException.class, () -> {
        //             c1.setName("Eric", "");     // last name empty is illegal
        // });
        // // test for correct error message
        // assertEquals("last name empty", thrown.getMessage());
        // // names remain initial (unchanged)
        // assertEquals("", c1.getFirstName());
        // assertEquals("", c1.getLastName());
        var c = dataFactory.createCustomer(", Eric", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }

    @Test @Order(303)
    void test303_getNameFirstAndLastName() {
        // IllegalArgumentException thrown =
        //     assertThrows(
        //         IllegalArgumentException.class, () -> {
        //             c1.setName("", "");         // last name empty is illegal
        // });
        // // test for correct error message
        // assertEquals("last name empty", thrown.getMessage());
        // // names remain initial (unchanged)
        // assertEquals("", c1.getFirstName());
        // assertEquals("", c1.getLastName());
        var c = dataFactory.createCustomer("", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }


    /*
     * Test cases 310: setName(name) with single-string name argument.
     */
    @Test @Order(310)
    void test310_getNameSingleName() {
        // c1.setName("Eric Meyer");       // name style 1: "first lastName"
        // assertEquals("Eric", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Eric", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }

    @Test @Order(311)
    void test311_getNameSingleName() {
        // c1.setName("Meyer, Eric");      // name style 2: "lastName, firstName"
        // assertEquals("Eric", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Meyer, Eric", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Eric", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }

    @Test @Order(312)
    void test312_getNameSingleName() {
        // c1.setName("Meyer; Eric");      // name style 3 with semicolon
        // assertEquals("Eric", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("Meyer; Eric", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Eric", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }

    @Test @Order(313)
    void test313_getNameSingleName() {
        // c1.setName("E. Meyer");         // name style 4: "F. lastName"
        // assertEquals("E.", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("E. Meyer", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("E.", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }


    /*
     * Test cases 320: setName(name) with '-' connected double-part last names.
     */
    @Test @Order(320)
    void test320_getNameDoubleLastName() {
        // c1.setName("Tim Schulz-Mueller");   // name style 1 with double last name
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller", c1.getLastName());
        var c = dataFactory.createCustomer("Tim Schulz-Mueller", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller", c1.getLastName());
    }

    @Test @Order(321)
    void test321_getNameDoubleLastName() {
        // c1.setName("Schulz-Mueller, Tim");  // name style 2
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller", c1.getLastName());
        var c = dataFactory.createCustomer("Schulz-Mueller, Tim", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller", c1.getLastName());
    }

    @Test @Order(322)
    void test322_getNameDoubleLastName() {
        // c1.setName("Schulz-Mueller; Tim");  // name style 3
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller", c1.getLastName());
        var c = dataFactory.createCustomer("Schulz-Mueller; Tim", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller", c1.getLastName());
    }


    /*
     * Test cases 330: single-Argument name Constructor
     */
    @Test @Order(330)
    void test330_getNameSingleArgumentConstructor() {
        // final Customer c1 = new Customer("Eric Meyer");     // name style 1
        // assertEquals("Eric", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer(" Eric    Meyer  ", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Eric", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }

    @Test @Order(331)
    void test331_getNameSingleArgumentConstructor() {
        // final Customer c1 = new Customer("Meyer, Eric");    // name style 3
        // assertEquals("Eric", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("  Meyer , Eric ", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Eric", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }

    @Test @Order(332)
    void test332_getNameSingleArgumentConstructor() {
        // final Customer c1 = new Customer("Meyer; Eric");    // name style 3
        // assertEquals("Eric", c1.getFirstName());
        // assertEquals("Meyer", c1.getLastName());
        var c = dataFactory.createCustomer("  Meyer  ; Eric  ", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Eric", c1.getFirstName());
        assertEquals("Meyer", c1.getLastName());
    }

    @Test @Order(333)
    void test333_getNameSingleArgumentConstructor() {
        // Customer c1 = new Customer("Tim Schulz-Mueller");   // name style 1
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller", c1.getLastName());
        var c = dataFactory.createCustomer(" Tim Schulz-Mueller ", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller", c1.getLastName());
    }

    @Test @Order(334)
    void test334_getNameSingleArgumentConstructor() {
        // Customer c1 = new Customer("Schulz-Mueller, Tim");  // name style 2
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller", c1.getLastName());
        var c = dataFactory.createCustomer("  Schulz-Mueller  , Tim  ", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller", c1.getLastName());
    }

    @Test @Order(335)
    void test335_getNameSingleArgumentConstructor() {
        // Customer c1 = new Customer("Schulz-Mueller; Tim");  // name style 3
        // assertEquals("Tim", c1.getFirstName());
        // assertEquals("Schulz-Mueller", c1.getLastName());
        var c = dataFactory.createCustomer("  Schulz-Mueller  ; Tim  ", "tim2346@gmx.de");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Tim", c1.getFirstName());
        assertEquals("Schulz-Mueller", c1.getLastName());
    }
}
