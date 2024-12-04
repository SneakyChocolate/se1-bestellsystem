package datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * DataFactory
 */
public class DataFactory {
	public static DataFactory df = new DataFactory();

	public static DataFactory getInstance() {
		return df;
	}

	/**
	 * <i>Factory</i> method to create an object of class {@link Customer2}
	 * from validated parameters. The <i>id</i> attribute is internally
	 * provided. No object is created when arguments are not valid.
	 * 
	 * @param name    single-String name parameter
	 * @param contact contact parameter validated as an email address
	 *                containing '@' or a phone number, invalid if null or empty
	 * @return created {@link Customer2} object with valid parameters or empty
	 */
	public Optional<Customer2> createCustomer(String name, String contact) {
		var nameParts = validateSplitName(name);
		if (nameParts.isPresent()) {
			long id = customerIdPool.next();
			var validContact = validateContact(contact);
			if (validContact.isPresent()) {
				// only create Customer when all conditions are met
				Customer2 c = new Customer2(id, nameParts.get().first(), nameParts.get().last());
				c.addContact(validContact.get());
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	/**
	 * Random generator.
	 */
	private final Random rand = new Random();

	/**
	 * {@link IdPool} for {@link Customer2} objects with 6-digit random numbers.
	 */
	private final IdPool<Long> customerIdPool = new IdPool<>(
		() -> 100000L + rand.nextLong(900000L),
		Arrays.asList( // initial Customer ids
				892474L, 643270L, 286516L, 412396L, 456454L, 651286L)
		);

	/**
	 * Internal class to manage pool of unique {@code ids} of type {@code T}.
	 * param T generic type of id
	 * 
	 * @param <T> The type of {@code ids} maintained in the pool.
	 */
	private class IdPool<T> {

		/** supplier for id's of type {@code T} */
		private final Supplier<T> supplier;

		/** pool of used or available id */
		private final List<T> pool;

		/** [0..i-1]: used id, [i..cap-1]: available id */
		private int i = 0;

		/** pool capacity */
		private int capacity;

		/**
		 * Constructor of id pool of {@code T}.
		 * 
		 * @param supplier   external supplier of id to fill the pool
		 * @param initialIds id to initialize the pool
		 */
		IdPool(Supplier<T> supplier, List<T> initialIds) {
			this.supplier = supplier;
			this.pool = new ArrayList<>(Optional.ofNullable(initialIds).orElse(List.of()));
			this.capacity = this.pool.size();
		}

		/**
		 * Return next id from the pool, resupply pool if capacity is exceeded.
		 * 
		 * @return next id of type {@code T}
		 */
		T next() {
			if (i >= capacity) { // add 10 supplied ids to the pool
				capacity += Stream.generate(supplier)
						.filter(n -> !pool.contains(n))
						.limit(10)
						.peek(n -> pool.add(n))
						.count();
			}
			return pool.get(i++);
		}
	}

	/**
	 * Regular expression to validate a name or name parts. A valid name must
	 * start with a letter, followed by a combination of letters, "-", "." or
	 * white spaces. Valid names are: "E", "E.", "Eric", "Ulla-Nadine",
	 * "Eric Meyer", "von-Blumenfeld". Names do not include numbers or other
	 * special characters.
	 * For the use of regular expressions, see
	 * https://stackoverflow.com/questions/8204680/java-regex-email
	 */
	private final Pattern nameRegex = Pattern.compile("^[A-Za-z][A-Za-z-\\s.]*$");

	/**
	 * Regular expression to validate an email address.
	 */
	private final Pattern emailRegex = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z0-9_]+$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Regular expression to validate a phone or fax number.
	 */
	private final Pattern phoneRegex = Pattern.compile("^(phone:|fax:|\\+[0-9]+){0,1}\\s*[\\s0-9()][\\s0-9()-]*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Validate contact for acceptable email address or phone number and
	 * return contact or empty result.
	 * <br>
	 * Rules for validating a <i>email</i> addresses and <i>phone</i>
	 * numbers are defined by regular expressions:
	 * <ul>
	 * <li><i>email address:</i>
	 * {@code "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z0-9_]+$"}.
	 * <li><i>phone number:</i>
	 * {@code "^(phone:|fax:|\\+[0-9]+){0,1}\\s*[\\s0-9()][\\s0-9()-]*"}.
	 * <li>leading and trailing white spaces {@code [\s]}, commata {@code [,;]}
	 * and quotes {@code ["']} are trimmed from contacts before validation.
	 * </ul>
	 * 
	 * @param contact contact to validate
	 * @return possibly modified (e.g. dequoted, trimmed) valid contact or empty
	 *         result
	 */
	public Optional<String> validateContact(String contact) {
		if (contact != null) {
			var cont = trimQuotesAndWhiteSpaces(contact);
			final int minLength = 6;
			boolean valid = cont.length() >= minLength;
			if (valid && (emailRegex.matcher(cont).matches() ||
					phoneRegex.matcher(cont).matches())) {
				return Optional.of(cont);
			}
		}
		return Optional.empty();
	}

	/**
	 * Validate name and return name or empty result. A valid name must
	 * start with a letter, followed by a combination of letters, "-",
	 * "." or white spaces. Valid names are: "E", "E.", "Eric",
	 * "Ulla-Nadine", "Eric Meyer", "von-Blumenfeld".
	 * Names do not include numbers or other special characters.
	 * <br>
	 * Rules for validating a <i>name</i> are defined by a regular expression:
	 * <ul>
	 * <li>{@code "^[A-Za-z][A-Za-z-\\s.]*$"}.
	 * <li>leading and trailing white spaces {@code [\s]}, commata {@code [,;]} and
	 * quotes {@code ["']} are trimmed from names before validation, e.g.
	 * {@code "  'Schulz-Müller, Tim Anton'  "}.
	 * </ul>
	 * 
	 * <pre>
	 * Examples:
	 * +------------------------------------+---------------------------------------+
	 * |name to validate                    |valid, possibly modified name          |
	 * +------------------------------------+---------------------------------------+
	 * |"Eric"                              |"Eric"                                 |
	 * |"Ulla-Nadine"                       |"Ulla-Nadine"                          |
	 * |"E", "E.", "von-A"                  |"E", "E.", "von-A"                     |
	 * +------------------------------------+---------------------------------------+
	 *
	 * Trim leading, trailing white spaces and quotes:
	 * +------------------------------------+---------------------------------------+
	 * |"  Anne  "   (lead/trailing spaces) |"Anne"                                 |
	 * |"  'Meyer'  "   (quotes)            |"Meyer"                                |
	 * +------------------------------------+---------------------------------------+
	 * </pre>
	 * 
	 * @param name            name to validate
	 * @param acceptEmptyName accept empty ("") name, e.g. as first name
	 * @return valid, possibly modified (e.g. dequoted, trimmed) name or empty
	 *         result
	 */
	public Optional<String> validateName(String name, boolean acceptEmptyName) {
		if (name != null) {
			name = trimQuotesAndWhiteSpaces(name);
			if (nameRegex.matcher(name).matches() || (name.length() == 0 && acceptEmptyName))
				return Optional.of(name);
		}
		return Optional.empty();
	}

	/**
	 * Record of first and last name parts of a name.
	 * 
	 * @param first first name parts
	 * @param last  last name parts
	 * @hidden exclude from documentation
	 */
	public record NameParts(String first, String last) {
	}

	/**
	 * Split single-String name into first and last name parts and
	 * validate parts, e.g. "Meyer, Eric" is split into first: "Eric"
	 * and last name: "Meyer".
	 * <br>
	 * Rules of splitting a single-String name into last- and first name parts:
	 * <ul>
	 * <li>if a name contains no seperators (comma or semicolon {@code [,;]}), the
	 * trailing
	 * consecutive part is the last name, all prior parts are first name parts, e.g.
	 * {@code "Tim Anton Schulz-Müller"}, splits into <i>first name:</i>
	 * {@code "Tim Anton"} and <i>last name:</i> {@code "Schulz-Müller"}.
	 * <li>names with seperators (comma or semicolon {@code [,;]}) split into a last
	 * name
	 * part before the seperator and a first name part after the seperator, e.g.
	 * {@code "Schulz-Müller, Tim Anton"} splits into <i>first name:</i>
	 * {@code "Tim Anton"} and <i>last name:</i> {@code "Schulz-Müller"}.
	 * <li>leading and trailing white spaces {@code [\s]}, commata {@code [,;]} and
	 * quotes
	 * {@code ["']} are trimmed from names before validation, e.g.
	 * {@code "  'Schulz-Müller, Tim Anton'  "}.
	 * <li>interim white spaces between name parts are removed, e.g.
	 * {@code "Schulz-Müller, <white-spaces> Tim <white-spaces> Anton <white-spaces> "}.
	 * </ul>
	 * 
	 * <pre>
	 * Examples:
	 * +------------------------------------+-------------------+-------------------+
	 * |Single-String name                  |first name parts   |last name parts    |
	 * +------------------------------------+-------------------+-------------------+
	 * |"Eric Meyer"                        |"Eric"             |"Meyer"            |
	 * |"Meyer, Anne"                       |"Anne"             |"Meyer"            |
	 * |"Meyer; Anne"                       |"Anne"             |"Meyer"            |
	 * |"Tim Schulz‐Mueller"                |"Tim"              |"Schulz‐Mueller"   |
	 * |"Nadine Ulla Blumenfeld"            |"Nadine Ulla"      |"Blumenfeld"       |
	 * |"Nadine‐Ulla Blumenfeld"            |"Nadine‐Ulla"      |"Blumenfeld"       |
	 * |"Khaled Mohamed Abdelalim"          |"Khaled Mohamed"   |"Abdelalim"        |
	 * +------------------------------------+-------------------+-------------------+
	 *
	 * Trim leading, trailing and interim white spaces and quotes:
	 * +------------------------------------+-------------------+-------------------+
	 * |" 'Eric Meyer'  "                   |"Eric"             |"Meyer"            |
	 * |"Nadine     Ulla     Blumenfeld"    |"Nadine Ulla"      |"Blumenfeld"       |
	 * +------------------------------------+-------------------+-------------------+
	 * </pre>
	 * 
	 * @param name single-String name to split into first- and last name parts
	 * @return record with valid, possibly modified (e.g. dequoted, trimmed) first
	 *         and last name parts or empty result
	 */
	public Optional<NameParts> validateSplitName(String name) {
		if (name != null && name.length() > 0) {
			String first = "", last = "";
			String[] spl1 = name.split("[,;]");
			if (spl1.length > 1) {
				// two-part name with last name first
				last = spl1[0];
				first = spl1[1];
			} else {
				// no separator [,;] -> split by white spaces;
				for (String s : name.split("\\s+")) {
					if (last.length() > 0) {
						// collect firstNames in order and lastName as last
						first += (first.length() == 0 ? "" : " ") + last;
					}
					last = s;
				}
			}
			var lastName = validateName(last, false);
			if (lastName.isPresent()) {
				var firstName = validateName(first, true);
				if (firstName.isPresent()) {
					return Optional.of(new NameParts(firstName.get(), lastName.get()));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Trim leading and trailing white spaces, commata {@code [,;]} and
	 * quotes {@code ["']} from a String.
	 * 
	 * @param s String to trim
	 * @return trimmed String
	 */
	private String trimQuotesAndWhiteSpaces(String s) {
		s = s.replaceAll("^[\\s\"',;]*", ""); // trim leading white spaces[\s], commata[,;] and quotes['"]
		s = s.replaceAll("[\\s\"',;]*$", ""); // trim trailing white spaces[\s], commata[,;] and quotes['"]
		s = s.replaceAll("[\\s]+", " "); // remove white spaces sequences, "Eric Meyer" -> "Eric Meyer"
		return s;
	}
}
