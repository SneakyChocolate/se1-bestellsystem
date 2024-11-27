package datamodel;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a customer with an ID, name, and a list of contacts.
 */
public class Customer {
	/**
	 * The unique identifier for the customer. Defaults to -1.
	 */
	long id = -1;

	/**
	 * The first name of the customer. Defaults to an empty string.
	 */
	String firstName = "";

	/**
	 * The last name of the customer. Defaults to an empty string.
	 */
	String lastName = "";

	/**
	 * A list of contact information for the customer.
	 */
	final List<String> contacts = new LinkedList<>();

	/**
	 * Default constructor. Initializes an empty customer.
	 */
	public Customer() {
	}

	/**
	 * Constructs a customer with a given full name.
	 *
	 * @param name the full name of the customer, where the last name is
	 *             separated by a space. Must not be {@code null}.
	 * @throws IllegalArgumentException if the name is {@code null} or does not
	 *                                  contain a space.
	 */
	public Customer(String name) {
		setName(name);
	}

	/**
	 * Retrieves the ID of the customer.
	 *
	 * @return the customer's ID.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the customer. The ID can only be set if it has not been
	 * assigned before.
	 *
	 * @param id the new ID to set. Must be non-negative.
	 * @return the current {@code Customer} instance.
	 * @throws IllegalArgumentException if the ID is negative or already set.
	 */
	public Customer setId(long id) {
		if (this.id < 0) {
			if (id >= 0) {
				this.id = id;
			} else {
				throw new IllegalArgumentException("id is invalid");
			}
		}
		return this;
	}

	/**
	 * Retrieves the first name of the customer.
	 *
	 * @return the customer's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Retrieves the last name of the customer.
	 *
	 * @return the customer's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the first and last names of the customer.
	 *
	 * @param first the first name. Can be {@code null}.
	 * @param last  the last name. Can be {@code null}.
	 * @return the current {@code Customer} instance.
	 */
	public Customer setName(String first, String last) {
		if (first != null)
			firstName = first;
		if (last != null)
			lastName = last;
		return this;
	}

	/**
	 * Sets the customer's full name by splitting it into first and last names.
	 *
	 * @param name the full name. Must not be {@code null} and must contain a space.
	 * @return the current {@code Customer} instance.
	 * @throws IllegalArgumentException if the name is {@code null} or does not
	 *                                  contain a space.
	 */
	public Customer setName(String name) {
		if (name.contains(", ")) {
			var split = name.split(", ");
			firstName = split[1];
			lastName = split[0];
			return this;
		}
		
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("illegal name");
		}
		name = name.replaceAll(",", "");
		int index = name.lastIndexOf(" ");
		if (index > 0) {
			firstName = name.substring(0, index);
			lastName = name.substring(index).trim();
		}
		else {
			lastName = name;
		}
		return this;
	}

	/**
	 * Retrieves the number of contacts associated with the customer.
	 *
	 * @return the count of contacts.
	 */
	public int contactsCount() {
		return contacts.size();
	}

	/**
	 * Retrieves an iterable of the customer's contacts.
	 *
	 * @return the customer's contacts.
	 */
	public Iterable<String> getContacts() {
		return contacts;
	}

	/**
	 * Adds a new contact to the customer's list of contacts.
	 *
	 * @param contact the contact to add. Must not be {@code null} or empty.
	 * @return the current {@code Customer} instance.
	 * @throws IllegalArgumentException if the contact is {@code null} or empty.
	 */
	public Customer addContact(String contact) {
		if (contact == null || contact.isEmpty()) {
			throw new IllegalArgumentException("Contact must not be null or empty");
		} else if (contacts.contains(contact)) {
			return this;
		}
		contacts.add(contact);
		return this;
	}

	/**
	 * Deletes a contact from the customer's list of contacts by its index.
	 *
	 * @param i the index of the contact to delete. If the index is out of bounds,
	 *          the method does nothing.
	 */
	public void deleteContact(int i) {
		if (i < 0 || i >= contactsCount()) {
			return;
		}
		contacts.remove(i);
	}

	/**
	 * Deletes all contacts associated with the customer.
	 */
	public void deleteAllContacts() {
		contacts.clear();
	}
}
