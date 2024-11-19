
package datamodel;

import java.util.LinkedList;
import java.util.List;

public class Customer {
    long id = -1;
    String firstName = "";
    String lastName = "";
    final List<String> contacts = new LinkedList<>();

    public Customer() {
    }
    public Customer(String name) {
        setName(name);
    }
    public long getId() {
        return id;
    }
    public Customer setId(long id) {
        if (this.id < 0) {
            if (id >= 0) {
                this.id = id;
            }
            else {
                throw new IllegalArgumentException("id is invalid");
            }
        }
        else {
            throw new RuntimeException("Id was already assigned");
        }
        return this;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public Customer setName(String first, String last) {
        if (first != null) firstName = first;
        if (last != null) lastName = last;
        return this;
    }
    public Customer setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        String[] split = name.split(" ");
        if (split.length != 2) {
            throw new IllegalArgumentException("name string has wrong size");
        }
        else {
            firstName = split[0];
            lastName = split[1];
        }
        return this;
    }
    public int contactsCount() {
        return contacts.size();
    }
    public Iterable<String> getContact() {
        return contacts;
    }
    public Customer addContact(String contact) {
        if (contact == null || contact.isEmpty() || contacts.contains(contact)) {
            throw new IllegalArgumentException("");
        }
        contacts.add(contact);
        return this;
    }
    public void deleteContact(int i) {
        if (i < 0 || i > contactsCount()) {
            return;
        }
        contacts.remove(i);
    }
    public void deleteAllContacts() {
        contacts.clear();
    }
}
