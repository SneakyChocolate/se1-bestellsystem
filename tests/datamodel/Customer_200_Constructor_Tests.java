package datamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class Customer_200_Constructor_Tests {

	/*
	 * Regular test case 100: Default Constructor.
	 */
	@Test
	@Order(200)
	void test200_DefaultConstructor() {
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
	@Order(201)
	void test201_DefaultConstructorChainableSetters() {
		final Customer c1 = new Customer();
		// test self-reference is returned for setter methods
		c1.setId(0L);
		assertEquals(0L, c1.getId());
		assertSame(c1, c1.setName("Eric Meyer"));
		assertSame(c1, c1.setName("Eric", "Meyer"));
		assertSame(c1, c1.addContact("eric@gmail.com"));
	}

	/*
	 * Regular test case 102: Default Constructor with setId(id) only
	 * allowed to set id once.
	 */
	@Test
	@Order(202)
	void test202_DefaultConstructorSetIdOnlyOnce() {
		final Customer c1 = new Customer();
		assertEquals(-1, c1.getId()); // id is null (unassigned)
		c1.setId(648L); // set id for the first time
		assertEquals(648L, c1.getId()); // id is 648
		c1.setId(912L); // set id for the second time
		assertEquals(648L, c1.getId()); // id is still 648
	}

	/*
	 * test setId(x) with minimum allowed value x and value x+1.
	 */
	@Test
	@Order(210)
	void test210_DefaultConstructor() {
		final Customer c1 = new Customer();
		c1.setId(1);
		assertEquals(c1.getId(), 1);
	}

	/*
	 * test setId(x) with maximum allowed value x and value x-1.
	 */
	@Test
	@Order(211)
	void test211_DefaultConstructor() {
		final Customer c1 = new Customer();
		assertThrows(IllegalArgumentException.class, () -> c1.setId(-1));
	}

	/*
	 * test setId(0) with value zero.
	 */
	@Test
	@Order(212)
	void test212_DefaultConstructor() {
		final Customer c1 = new Customer();
		c1.setId(0);
		assertEquals(c1.getId(), 0);
	}

	/*
	 * test setId(-1) illegal (exception) case that expects the method to throw an
	 * IllegalArgumentException with message: "invalid id (negative)". Test both,
	 * that the exception is thrown and the exception message.
	 */
	@Test
	@Order(220)
	void test220_DefaultConstructor() {
		final Customer c1 = new Customer();
		assertThrows(IllegalArgumentException.class, () -> c1.setId(-1));
	}

	/*
	 * test setId(Long.MIN_VALUE) illegal (exception) case that expects the method
	 * to throw an IllegalArgumentException with message: "invalid id (negative)".
	 * Test both, that the exception is thrown and the exception message.
	 */
	@Test
	@Order(221)
	void test221_DefaultConstructor() {
		final Customer c1 = new Customer();
		assertThrows(IllegalArgumentException.class, () -> c1.setId(Long.MIN_VALUE));
	}
}
