package components;

/**
 * Public interface with {@code getInstance()} methods for obtaining references
 * to component singleton objects.
 */
public interface Components {

	/**
	 * Getter of {@link Components} implementation class singleton.
	 * @return reference to singleton instance of {@link Components} implementation class
	 */
	static Components getInstance() {
		return components.impl.ComponentsImpl.getInstance();
	}

	/**
	 * Getter of {@link Calculator} component implementation class singleton.
	 * @return reference to singleton instance of {@link Calculator} implementation class
	 */
	Calculator getCalculator();

	/**
	 * Getter of {@link DataFactory} component implementation class singleton.
	 * @return reference to singleton instance of {@link DataFactory} implementation class
	 */
	public DataFactory getDataFactory();

	/**
	 * Getter of {@link Validator} component implementation class singleton.
	 * @return reference to singleton instance of {@link Validator} implementation class
	 */
	public Validator getValidator();

	/**
     * Getter of {@link Formatter} component implementation class singleton.
     * @return reference to singleton instance of {@link Formatter} implementation class
     */
	public Formatter getFormatter();

	/**
	 * Getter of {@link Printer} component implementation class singleton.
	 * @return reference to singleton instance of {@link Printer} implementation class
	 */
	public Printer getPrinter();
}


