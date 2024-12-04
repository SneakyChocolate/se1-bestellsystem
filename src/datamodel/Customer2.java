package datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable entity class representing a <i>Customer2</i>, a person who creates
 * and holds (owns) orders in the system.
 * <br>
 * An <i>immutable</i> class does not allow changes to attributes.
 * {@link DataFactory} is the only class that creates {@link Customer2}
 * objects from validated arguments.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public final class Customer2 {

    /**
     * Unique Customer2 id attribute. Must be {@code > 0}).
     */
    private final long id;

    /**
     * Customer2 surname attribute. Must not be {@code null} and not empty {@code ""}.
     */
    private final String lastName;

    /**
     * Customer2 none-surname parts. Must not be {@code null}, can be empty {@code ""}.
     */
    private final String firstName;

    /**
     * Contact information with multiple entries, e.g. email addresses
     * or phone numbers. The attribute is exposed to {@link DataFactory}
     * in the same package.
     */
    private final List<String> contacts = new ArrayList<>();


    /**
     * None-public constructor used by {@link DataFactory} preventing object
     * creation outside this package.
     * @param id customer identifier supplied by {@link DataFactory}
     * @param firstName first name attribute, must not be {@code null}, can be empty {@code ""}
     * @param lastName last name attribute, must not be {@code null} and not empty {@code ""}.
     * @throws IllegalArgumentException if {@code id} is negative, firstName is {@code null}
     *      or lastName is {@code null} or empty {@code ""}
     */
    protected Customer2(long id, String firstName, String lastName) {
        if(id < 0L)
            throw new IllegalArgumentException("id negative");
        if(lastName==null || lastName.length()==0)
            throw new IllegalArgumentException("lastName null or empty");
        //
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Id attribute getter.
     * @return customer id
     */
    public long getId() {
        return id;
    }

    /**
     * LastName attribute getter.
     * @return value of lastName attribute
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * FirstName attribute getter.
     * @return value of firstName attribute
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Return the number of contacts.
     * @return number of contacts
     */
    public int contactsCount() {
        return contacts.size();
    }

    /**
     * Contacts getter (as immutable {@link Iterable<String>}).
     * @return contacts (as immutable {@link Iterable<String>})
     */
    public Iterable<String> getContacts() {
        return contacts;
    }

    /**
     * Add new contact validated through {@link DataFactory}. Method has
     * no effect if contact is not valid.
     * @param contact contact added validated through {@link DataFactory}
     * @return chainable self-reference
     */
    public Customer2 addContact(String contact) {
        DataFactory.getInstance().validateContact(contact)
            .filter(cont -> ! contacts.contains(contact))
            .ifPresent(c -> ((List<String>)contacts).add(c));
        return this;
    }

    /**
     * Delete the i-th contact with {@code i >= 0} and {@code i < contactsCount()}.
     * Method has no effect for {@code i} outside valid bounds.
     * @param i index of contact to delete
     */
    public void deleteContact(int i) {
        if( i >= 0 && i < contacts.size() ) {
            contacts.remove(i);
        }
    }

    /**
     * Delete all contacts.
     */
    public void deleteAllContacts() {
        contacts.clear();
    }
}
