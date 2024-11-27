package datamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests for {@link Customer} class: [100..199] with tested Constructors:
 * 
 * <pre>
 * - Customer()             // default constructor
 * - Customer(String name)  // constructor with name argument
 * </pre>
 * 
 * @author sgra64
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Customer_100_Constructor_Tests {

	/*
	 * Regular test case 100: Default Constructor.
	 */
	@Test
	@Order(100)
	void test100_DefaultConstructor() {
		final Customer c1 = new Customer(); // call default Constructor
		assertEquals(-1, c1.getId()); // returns null for unassigned id
		assertEquals("", c1.getLastName()); // lastName: ""
		assertEquals("", c1.getFirstName()); // firstName: ""
		assertEquals(0, c1.contactsCount()); // 0 contacts
	}

	/*
	 * Regular test case 101: Default Constructor test methods
	 * are chainable.
	 */
	@Test
	@Order(101)
	void test101_DefaultConstructorChainableSetters() {
		final Customer c1 = new Customer();
		// test self-reference is returned for setter methods
		assertSame(c1, c1.setId(0L));
		assertSame(c1, c1.setName("Eric Meyer"));
		assertSame(c1, c1.setName("Eric", "Meyer"));
		assertSame(c1, c1.addContact("eric@gmail.com"));
	}

	/*
	 * Regular test case 102: Default Constructor with setId(id) only
	 * allowed to set id once.
	 */
	@Test
	@Order(102)
	void test102_DefaultConstructorSetIdOnlyOnce() {
		final Customer c1 = new Customer();
		assertEquals(-1, c1.getId()); // id is null (unassigned)
		c1.setId(648L); // set id for the first time
		assertEquals(648L, c1.getId()); // id is 648
		c1.setId(912L); // set id for the second time
		assertEquals(648L, c1.getId()); // id is still 648
	}

	/*
	 * Regular test case 110: Constructor with regular first name last name.
	 * new Customer("Eric Meyer"), expected: firstName: "Eric", lastName: "Meyer"
	 */
	@Test
	@Order(110)
	void test110_ConstructorWithRegularFirstLastName() {
		Customer c1 = new Customer("Eric Meyer");
		assertEquals(c1.getFirstName(), "Eric");
		assertEquals(c1.getLastName(), "Meyer");
	}

	/*
	 * Regular test case 111: Constructor with regular last name comma first name.
	 * new Customer("Meyer, Eric"), expected: firstName: "Eric", lastName: "Meyer"
	 */
	@Test
	@Order(111)
	void test111_ConstructorWithRegularLastCommaFirstName() {
		Customer c1 = new Customer("Meyer, Eric");
		assertEquals(c1.getFirstName(), "Eric");
		assertEquals(c1.getLastName(), "Meyer");
	}

	/*
	 * Regular test case 112: Constructor with regular single last name.
	 * new Customer("Meyer"), expected: firstName: "" (empty), lastName: "Meyer"
	 */
	@Test
	@Order(112)
	void test112_ConstructorWithRegularLastNameOnly() {
		Customer c1 = new Customer("Meyer");
		assertEquals(c1.getFirstName(), "");
		assertEquals(c1.getLastName(), "Meyer");
	}

    /*
     * Corner test case 120: Constructor with shortest allowed first and last name.
     * test three cases:
     *  - new Customer("E M"),  expected: firstName: "E", lastName: "M"
     *  - new Customer("M, E"), expected: firstName: "E", lastName: "M"
     *  - new Customer("M"),    expected: firstName: "", lastName: "M"
     */
    @Test @Order(120)
    void test120_ConstructorWithCornerShortestPossibleFirstAndLastName() {
    	Customer c1 = null;
    	c1 = new Customer("E M");
    	assertEquals(c1.getFirstName(), "E");
    	assertEquals(c1.getLastName(), "M");

    	c1 = new Customer("M, E");
    	assertEquals(c1.getFirstName(), "E");
    	assertEquals(c1.getLastName(), "M");

    	c1 = new Customer("M");
    	assertEquals(c1.getFirstName(), "");
    	assertEquals(c1.getLastName(), "M");
    }

    /*
     * Corner test case 121: Constructor with long first and last name.
     * new Customer("Nadine Ulla Maxine Adriane Blumenfeld")
     *  - expected: firstName: "Nadine Ulla Maxine Adriane", lastName: "Blumenfeld"
     */
    @Test @Order(121)
    void test121_ConstructorWithLongFirstAndLastName() {
    	var c1 = new Customer("Nadine Ulla Maxine Adriane Blumenfeld");
    	assertEquals(c1.getFirstName(), "Nadine Ulla Maxine Adriane");
    	assertEquals(c1.getLastName(), "Blumenfeld");
    }

    /*
     * Corner test case 122: Constructor with long first and multi-part last name.
     * new Customer("Nadine Ulla Maxine Adriane von-Blumenfeld-Bozo")
     *  - expected: firstName: "Nadine Ulla Maxine Adriane", lastName: "von-Blumenfeld-Bozo"
     */
    @Test @Order(122)
    void test122_ConstructorWithLongFirstAndMultipartLastName() {
    	var c1 = new Customer("Nadine Ulla Maxine Adriane von-Blumenfeld-Bozo");
    	assertEquals(c1.getFirstName(), "Nadine Ulla Maxine Adriane");
    	assertEquals(c1.getLastName(), "von-Blumenfeld-Bozo");
    }

    /*
     * Corner test case 123: Constructor with long first and multi-part last name.
     * new Customer("von-Blumenfeld-Bozo, Nadine Ulla Maxine Adriane")
     *  - expected: firstName: "Nadine Ulla Maxine Adriane", lastName: "von-Blumenfeld-Bozo"
     */
    @Test @Order(123)
    void test123_ConstructorWithLongMultipartLastNameAndFirstName() {
    	var c1 = new Customer("von-Blumenfeld-Bozo, Nadine Ulla Maxine Adriane");
    	assertEquals(c1.getFirstName(), "Nadine Ulla Maxine Adriane");
    	assertEquals(c1.getLastName(), "von-Blumenfeld-Bozo");
    }}
