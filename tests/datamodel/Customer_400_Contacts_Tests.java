package datamodel;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.StreamSupport;


/**
 * Tests for Customer class: [400..499] contacts-related tests.
 * @author sgra64
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Customer_400_Contacts_Tests {
    //
    private final Customer c1 = new Customer();


    /*
     * Test cases 400: add contacts.
     */
    @Test @Order(400)
    void test400_addContactsRegularCases() {
        assertEquals(0, c1.contactsCount());
        c1.addContact("eric@gmail.com");
        assertEquals(1, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com"
        }, contacts(c1));
        c1.addContact("(0152) 38230529");
        assertEquals(2, c1.contactsCount());
        assertArrayEquals(new String[] {    // must maintain order
            "eric@gmail.com", "(0152) 38230529"
        }, contacts(c1));
        c1.addContact("(030) 3534346-6336");
        assertEquals(3, c1.contactsCount());
        assertArrayEquals(new String[] {    // must maintain order
            "eric@gmail.com", "(0152) 38230529", "(030) 3534346-6336"
        }, contacts(c1));
    }

    @Test @Order(401)
    void test401_addContactsCornerCases() {
        assertEquals(0, c1.contactsCount());
        // test leading and trailing spaces are trimmed
        c1.addContact(" eric@gmail.com  ");
        assertEquals(1, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com"
        }, contacts(c1));
        // test more leading and trailing white spaces are trimmed
        c1.addContact("\t(0152) 38230529\t \n\t");
        assertEquals(2, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com", "(0152) 38230529"
        }, contacts(c1));
    }

    @Test @Order(402)
    void test402_addContactsCornerCases() {
        assertEquals(0, c1.contactsCount());
        // test leading and trailing spaces are trimmed
        c1.addContact("\"eric@gmail.com\"");
        assertEquals(1, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com"
        }, contacts(c1));
        // test more leading and trailing white spaces are trimmed
        c1.addContact("\" \"'\"(0152) 38230529';'\" ,\t\n\"");
        assertEquals(2, c1.contactsCount());    // test special chars: quotes["'] and [;,.]
        assertArrayEquals(new String[] {
            "eric@gmail.com", "(0152) 38230529"
        }, contacts(c1));
    }

    @Test @Order(403)
    void test403_addContactsMinimumLength() {
        assertEquals(0, c1.contactsCount());
        c1.addContact("e@gm.c");    // minimum length for meaningful contact is 6 characters
        assertEquals(1, c1.contactsCount());
        assertArrayEquals(new String[] {
            "e@gm.c"
        }, contacts(c1));
        //
        final String contact = "e@g.c";     // contact < 6 characters as contact is illegal
        IllegalArgumentException thrown =
            assertThrows(
                IllegalArgumentException.class, () -> {
                    c1.addContact(contact);
        });
        // test for correct error message
        assertEquals("contact less than 6 characters: \"" + contact +
                "\".", thrown.getMessage());
        //
        final String contact2 = "\"  e@g.c \t\"";   // input > 6, but not after trimming
        thrown = assertThrows(
            IllegalArgumentException.class, () -> {
                c1.addContact(contact2);
        });
        // test for correct error message
        assertEquals("contact less than 6 characters: \"" + contact2 +
                "\".", thrown.getMessage());
    }

    @Test @Order(404)
    void test404_addContactsIgnoreDuplicates() {
        // duplicate entries for contacts are ignored
        assertEquals(0, c1.contactsCount());
        c1.addContact("eric@gmail.com");
        assertEquals(1, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com"
        }, contacts(c1));
        c1.addContact("eric@gmail.com");    // duplicate contact
        assertEquals(1, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com"                        // +1 duplicate contact
        }, contacts(c1));
        c1.addContact("eric@gmail.com");    // +2 duplicate contact
        assertEquals(1, c1.contactsCount());        // ignored
        assertArrayEquals(new String[] {
            "eric@gmail.com"
        }, contacts(c1));
    }


    /*
     * Test cases 410: delete contacts.
     */
    @Test @Order(410)
    void test410_deleteContactRegularCases() {
        c1.addContact("eric@gmail.com")
            .addContact("(0152) 38230529")
            .addContact("(030) 3534346-6336");
        assertEquals(3, c1.contactsCount());
        assertArrayEquals(new String[] {    // must maintain order
            "eric@gmail.com", "(0152) 38230529", "(030) 3534346-6336"
        }, contacts(c1));
        c1.deleteContact(2);    // delete last contact
        assertEquals(2, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com", "(0152) 38230529"
        }, contacts(c1));
        c1.deleteContact(0);    // delete first contact
        assertEquals(1, c1.contactsCount());
        assertArrayEquals(new String[] {
            "(0152) 38230529"
        }, contacts(c1));
    }

    @Test @Order(411)
    void test411_deleteContactOutOfBoundsCases() {
        c1.addContact("eric@gmail.com")
            .addContact("(0152) 38230529")
            .addContact("(030) 3534346-6336");
        assertEquals(3, c1.contactsCount());
        assertArrayEquals(new String[] {        // must maintain order
            "eric@gmail.com", "(0152) 38230529", "(030) 3534346-6336"
        }, contacts(c1));
        c1.deleteContact(300);    // delete > upper bound, ignore
        c1.deleteContact(3);
        assertEquals(3, c1.contactsCount());    // still 3 contacts
        c1.deleteContact(-1);       // delete < upper bound, ignore
        c1.deleteContact(-100);
        assertEquals(3, c1.contactsCount());    // still 3 contacts
        assertArrayEquals(new String[] {        // must maintain order
            "eric@gmail.com", "(0152) 38230529", "(030) 3534346-6336"
        }, contacts(c1));
    }

    @Test @Order(412)
    void test412_deleteAllContacts() {
        c1.deleteAllContacts();     // nothing to delete
        assertEquals(0, c1.contactsCount());
        assertArrayEquals(new String[] { }, contacts(c1));
        //
        c1.addContact("eric@gmail.com")
            .addContact("(0152) 38230529")
            .addContact("(030) 3534346-6336");
        //
        assertEquals(3, c1.contactsCount());
        assertArrayEquals(new String[] {
            "eric@gmail.com", "(0152) 38230529", "(030) 3534346-6336"
        }, contacts(c1));
        //
        c1.deleteAllContacts(); // all contacts deleted
        assertEquals(0, c1.contactsCount());
        assertArrayEquals(new String[] { }, contacts(c1));
        //
        c1.deleteAllContacts(); // repeat
        assertEquals(0, c1.contactsCount());
        assertArrayEquals(new String[] { }, contacts(c1));
    }

    /**
     * Convert return type of getContacts() method to String[].
     * @param c Customer object
     * @return contacts as String[]
     */
    private String[] contacts(Customer c) {
        var contacts = c.getContacts();
        return StreamSupport.stream(contacts.spliterator(), false).toArray(String[]::new);
    }
}
