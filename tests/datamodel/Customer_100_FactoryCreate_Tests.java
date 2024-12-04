package datamodel;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;


/**
 * Tests for {@link Customer} class: [100..199] with tested DataFactory creates:
 * <pre>
 * - Customer()             // default DataFactory create
 * - Customer(String name)  // DataFactory create with name argument
 * </pre>
 * @author sgra64
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Customer_100_FactoryCreate_Tests {

    /*
     * Reference to DataFactory.
     */
    private final DataFactory dataFactory = DataFactory.getInstance();


    /*
     * Regular test case 100: DataFactory Singleton.
     */
    @Test @Order(100)
    void test100_DataFactorySingleton() {
        // test getInstance always returns same reference to singleton object
        assertEquals(dataFactory, DataFactory.getInstance());
        assertEquals(dataFactory, DataFactory.getInstance());
        assertEquals(dataFactory, DataFactory.getInstance());
    }

    /*
     * Regular test case 110: created object methods are chainable.
     */
    @Test @Order(101)
    void test101_DefaultDataFactoryChainableSetters() {
        var c = dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        // test self-reference is returned
        assertSame(c1, c1.addContact("eric@gmail.com"));
    }

    // /*
    //  * Regular test case 102: Default DataFactory create with setId(id) only
    //  * allowed to set id once.
    //  */
    // @Test @Order(102)
    // void test102_DefaultDataFactory createSetIdOnlyOnce() {
    //     final Customer c1 = new Customer();
    //     assertEquals(null, c1.getId());     // id is null (unassigned)
    //     c1.setId(648L);                     // set id for the first time
    //     assertEquals(648L, c1.getId());     // id is 648
    //     c1.setId(912L);                     // set id for the second time
    //     assertEquals(648L, c1.getId());     // id is still 648
    // }


    /*
     * Regular test case 110: DataFactory create with regular first name last name.
     * new Customer("Eric Meyer"),  expected: firstName: "Eric", lastName: "Meyer"
     */
    @Test @Order(110)
    void test110_DataFactoryCreateWithRegularFirstLastName() {
        // final Customer c1 = new Customer("Eric Meyer"); // call DataFactory create
        // assertEquals(null, c1.getId());                 // returns null for unassigned id
        var c = dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Meyer", c1.getLastName());        // lastName: "Meyer"
        assertEquals("Eric", c1.getFirstName());        // firstName: "Eric"
        assertEquals(1, c1.contactsCount());            // 1 contact
    }

    /*
     * Regular test case 111: DataFactory create with regular last name comma first name.
     * new Customer("Meyer, Eric"),  expected: firstName: "Eric", lastName: "Meyer"
     */
    @Test @Order(111)
    void test111_DataFactoryCreateWithRegularLastCommaFirstName() {
        // final Customer c1 = new Customer("Meyer, Eric"); // call DataFactory create
        // assertEquals(null, c1.getId());                 // returns null for unassigned id
        var c = dataFactory.createCustomer("Meyer, Eric", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Meyer", c1.getLastName());        // lastName: "Meyer"
        assertEquals("Eric", c1.getFirstName());        // firstName: "Eric"
        assertEquals(1, c1.contactsCount());            // 1 contact
    }

    /*
     * Regular test case 112: DataFactory create with regular single last name.
     * new Customer("Meyer"),  expected: firstName: "" (empty), lastName: "Meyer"
     */
    @Test @Order(112)
    void test112_DataFactoryCreateWithRegularLastNameOnly() {
        // final Customer c1 = new Customer("Meyer");      // call DataFactory create
        // assertEquals(null, c1.getId());                 // returns null for unassigned id
        var c = dataFactory.createCustomer("Meyer", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Meyer", c1.getLastName());        // lastName: "Meyer"
        assertEquals("", c1.getFirstName());            // firstName: ""
        assertEquals(1, c1.contactsCount());            // 0 contacts
    }


    /*
     * Corner test case 120: DataFactory create with shortest allowed first and last name.
     * test three cases:
     *  - new Customer("E M"),  expected: firstName: "E", lastName: "M"
     *  - new Customer("M, E"), expected: firstName: "E", lastName: "M"
     *  - new Customer("M"),    expected: firstName: "", lastName: "M"
     */
    @Test @Order(120)
    void test120_DataFactoryCreateWithCornerShortestPossibleFirstAndLastName() {
        // final Customer c1 = new Customer("E M");
        // assertEquals(null, c1.getId());
        var c = dataFactory.createCustomer("E M", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("M", c1.getLastName());
        assertEquals("E", c1.getFirstName());
        assertEquals(1, c1.contactsCount());
        //
        // final Customer c2 = new Customer("M, E");
        // assertEquals(null, c2.getId());
        c = dataFactory.createCustomer("M, E", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c2 = c.get();
        assertEquals("M", c2.getLastName());
        assertEquals("E", c2.getFirstName());
        assertEquals(1, c2.contactsCount());
        //
        // final Customer c3 = new Customer("M");
        // assertEquals(null, c3.getId());
        c = dataFactory.createCustomer("M", "eric98@yahoo.com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c3 = c.get();
        assertEquals("M", c3.getLastName());
        assertEquals("", c3.getFirstName());
        assertEquals(1, c3.contactsCount());
    }

    /*
     * Corner test case 121: DataFactory create with long first and last name.
     * new Customer("Nadine Ulla Maxine Adriane Blumenfeld")
     *  - expected: firstName: "Nadine Ulla Maxine Adriane", lastName: "Blumenfeld"
     */
    @Test @Order(121)
    void test121_DataFactoryCreateWithLongFirstAndLastName() {
        // final Customer c1 = new Customer("Nadine Ulla Maxine Adriane Blumenfeld");
        // assertEquals(null, c1.getId());
        var c = dataFactory.createCustomer("Nadine Ulla Maxine Adriane Blumenfeld", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("Blumenfeld", c1.getLastName());
        assertEquals("Nadine Ulla Maxine Adriane", c1.getFirstName());
        assertEquals(1, c1.contactsCount());
    }

    /*
     * Corner test case 122: DataFactory create with long first and multi-part last name.
     * new Customer("Nadine Ulla Maxine Adriane von-Blumenfeld-Bozo")
     *  - expected: firstName: "Nadine Ulla Maxine Adriane", lastName: "von-Blumenfeld-Bozo"
     */
    @Test @Order(122)
    void test122_DataFactoryCreateWithLongFirstAndMultipartLastName() {
        // final Customer c1 = new Customer("Nadine Ulla Maxine Adriane von-Blumenfeld-Bozo");
        // assertEquals(null, c1.getId());
        var c = dataFactory.createCustomer("Nadine Ulla Maxine Adriane von-Blumenfeld-Bozo", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("von-Blumenfeld-Bozo", c1.getLastName());
        assertEquals("Nadine Ulla Maxine Adriane", c1.getFirstName());
        assertEquals(1, c1.contactsCount());
    }

    /*
     * Corner test case 123: DataFactory create with long first and multi-part last name.
     * new Customer("von-Blumenfeld-Bozo, Nadine Ulla Maxine Adriane")
     *  - expected: firstName: "Nadine Ulla Maxine Adriane", lastName: "von-Blumenfeld-Bozo"
     */
    @Test @Order(123)
    void test123_DataFactoryCreateWithLongMultipartLastNameAndFirstName() {
        // final Customer c1 = new Customer("von-Blumenfeld-Bozo, Nadine Ulla Maxine Adriane");
        // assertEquals(null, c1.getId());
        var c = dataFactory.createCustomer("von-Blumenfeld-Bozo, Nadine Ulla Maxine Adriane", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertTrue(c.isPresent());
        Customer c1 = c.get();
        assertEquals("von-Blumenfeld-Bozo", c1.getLastName());
        assertEquals("Nadine Ulla Maxine Adriane", c1.getFirstName());
        assertEquals(1, c1.contactsCount());
    }


    /*
     * Exception test case 130: DataFactory create with empty name: "".
     * The exptected reaction is an empty Optional returned from DataFactory
     */
    @Test @Order(130)
    void test130_DataFactoryCreateWithEmptyName() {
        // IllegalArgumentException thrown =
        //     assertThrows(
        //         IllegalArgumentException.class, () -> {
        //             new Customer("");
        // });
        // // test for correct error message
        // assertEquals("name empty", thrown.getMessage());
        var c = dataFactory.createCustomer("", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }

    /*
     * Exception test case 131: DataFactory create with null name argument.
     * The exptected reaction is an empty Optional returned from DataFactory
     */
    @Test @Order(131)
    void test131_DataFactoryCreateWithNullName() {
        // IllegalArgumentException thrown =
        //     assertThrows(
        //         IllegalArgumentException.class, () -> {
        //             new Customer(null);
        // });
        // // test for correct error message
        // assertEquals("name null", thrown.getMessage());
        var c = dataFactory.createCustomer(null, "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }

    /*
     * Exception test case 132: DataFactory create with empty contact: "".
     * The exptected reaction is an empty Optional returned from DataFactory
     */
    @Test @Order(132)
    void test132_DataFactoryCreateWithEmptyContact() {
        // IllegalArgumentException thrown =
        //     assertThrows(
        //         IllegalArgumentException.class, () -> {
        //             new Customer("");
        // });
        // // test for correct error message
        // assertEquals("name empty", thrown.getMessage());
        var c = dataFactory.createCustomer("Eric Meyer", "");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }

    /*
     * Exception test case 133: DataFactory create with null contact argument.
     * The exptected reaction is an empty Optional returned from DataFactory
     */
    @Test @Order(133)
    void test133_DataFactoryCreateWithNullContact() {
        // IllegalArgumentException thrown =
        //     assertThrows(
        //         IllegalArgumentException.class, () -> {
        //             new Customer(null);
        // });
        // // test for correct error message
        // assertEquals("name null", thrown.getMessage());
        var c = dataFactory.createCustomer("Eric Meyer", null);
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }


    /*
     * Exception test case 140: DataFactory create with empty name: "".
     * The exptected reaction is an empty Optional returned from DataFactory
     */
    @Test @Order(140)
    void test140_DataFactoryCreateInvalidName() {
        var c = dataFactory.createCustomer(null, "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer("", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer(",", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer(" ,  ", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer(";", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer(" ;  ", "+49 152-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }


    /*
     * Exception test case 150: DataFactory create with empty name: "".
     * The exptected reaction is an empty Optional returned from DataFactory
     */
    @Test @Order(150)
    void test150_DataFactoryCreateInvalidEmailContact() {
        var c = dataFactory.createCustomer("Eric Meyer", "eric<>gmail.com");    // must have @
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer("Eric Meyer", "e@g.m");  // min 6 characters
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer("Eric Meyer", null);
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer("Eric Meyer", "eric@gmail@com");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }

    /*
     * Exception test case 160: DataFactory create with empty name: "".
     * The exptected reaction is an empty Optional returned from DataFactory
     */
    @Test @Order(160)
    void test160_DataFactoryCreateInvalidPhoneContact() {
        var c = dataFactory.createCustomer("Eric Meyer", "+49 A52-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer("Eric Meyer", "");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer("Eric Meyer", null);
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
        //
        c = dataFactory.createCustomer("Eric Meyer", "-92454");
        assertNotEquals(c, null);
        assertEquals(c.getClass(), Optional.class);
        assertFalse(c.isPresent());
    }
}