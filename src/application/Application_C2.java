package application;

import application.Runtime.Bean;
import datamodel.Customer;

import java.util.*;


/**
 * Driver class for the <i>c2-customer</i> assignment. Class creates some
 * {@link Customer} objects and outputs some results.
 * Class implements the {@link Runtime.Runnable} interface.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
@Bean(priority=2)
public class Application_C2 implements Runtime.Runnable {

    /**
     * None-public default constructor (avoid javadoc warning).
     */
    public Application_C2() { }


    /**
     * Method of the {@link Runtime.Runnable} interface called on an instance
     * of this class created by the {@link Runtime}. Program execution starts here.
     * @param properties properties from the {@code application.properties} file
     * @param args arguments passed from the command line
     */
    @Override
    public void run(Properties properties, String[] args) {

        final Customer eric = new Customer("Eric Meyer")
            .setId(892474L)     // set id, first time
            .setId(947L)        // ignored, since id can only be set once
            .addContact("eric98@yahoo.com")
            .addContact("eric98@yahoo.com") // ignore duplicate contact
            .addContact("(030) 3945-642298");

        final Customer anne = new Customer("Anne Bayer")
            .setId(643270L)
            .addContact("anne24@yahoo.de")
            .addContact("(030) 3481-23352")
            .addContact("fax: (030)23451356");

        final Customer tim = new Customer("Tim Schulz-Mueller")
            .setId(286516L)
            .addContact("tim2346@gmx.de");

        final Customer nadine = new Customer("Nadine-Ulla Blumenfeld")
            .setId(412396L)
            .addContact("+49 152-92454");

        final Customer khaled = new Customer()
            .setName("Khaled Saad Mohamed Abdelalim")
            .setId(456454L)
            .addContact("+49 1524-12948210");

        List<Customer> customers = List.of(eric, anne, tim, nadine, khaled);
        //
        // print customer list
        customers.stream()
            .map(c -> print(c))     // .map(this::print)
            .forEach(System.out::println);
    }

    /**
     * Print customer attributes to StringBuffer and return as String.
     * @param customer object to print
     * @return customer attributes as String
     */
    String print(Customer customer) {
        StringBuffer sb = new StringBuffer(" - customer id: ");
        sb.append(customer.getId())
            .append(", name: ")
            .append(customer.getLastName())
            .append(", ")
            .append(customer.getFirstName());
        return sb.toString();
    }

    /**
     * JavaVM entry method that calls the {@link Runtime}, which creates
     * an instance of this class and invokes the
     * {@code run(properties, args)} - method.
     * @param args arguments passed from command line
     */
    public static void main(String[] args) {
        Runtime.run(args);
    }
}

