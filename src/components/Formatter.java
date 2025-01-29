package components;

import datamodel.Customer;
import datamodel.Pricing.Currency;

/**
 * Formatter
 */
public interface Formatter {
	String fmtCustomerName(Customer customer, int style);
	String fmtCustomerContact(Customer customer, int style);
	String fmtPrice(long price, Currency currency, int... style);
	String fmtDecimal(long value, int decimalDigits, String... unit);
	TableFormatter createTableFormatter(String... columnSpecs);
}
