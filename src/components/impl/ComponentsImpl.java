package components.impl;

import components.Calculator;
import components.Components;
import components.DataFactory;
import components.Formatter;
import components.Printer;
import components.Validator;

public final class ComponentsImpl implements Components {
    /**
     * static singleton instance of class {@link Components}
     */
    private static final Components instance = new ComponentsImpl();

    /**
     * singleton instance of {@link Calculator} implementation class
     */
    private final Calculator calculator;

    /**
     * singleton instance of {@link DataFactory} implementation class
     */
    private final DataFactory dataFactory;

    /**
     * singleton instance of {@link Validator} implementation class
     */
    private final Validator validator;

    /**
     * singleton instance of {@link Formatter} implementation class
     */
    private final Formatter formatter;

    /**
     * static singleton instance of class {@link Components}
     */
    private final Printer printer;

    /**
     * Getter of {@link Components} implementation class singleton.
     * @return reference to singleton instance of {@link Components} implementation class
     */
    public static Components getInstance() {
        return instance;
    }

    /**
     * Private constructor as part of the singleton pattern that creates
     * singleton instances of {@link ComponentsImpl} implementation classes.
     */
    private ComponentsImpl() {
        var dataFactory = new DataFactoryImpl();
        this.dataFactory = dataFactory;
        this.validator = dataFactory;
        this.calculator = new CalculatorImpl();
        this.formatter = new TableFormatterImpl();
        this.printer = new PrinterImpl();

    }

    public Calculator getCalculator() {
        return calculator;
    }

    @Override
    public DataFactory getDataFactory() {
        return dataFactory;
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

	@Override
	public Formatter getFormatter() {
		return formatter;
	}

    @Override
    public Printer getPrinter() {
        return printer;
    }
}
