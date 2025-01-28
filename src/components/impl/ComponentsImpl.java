package components.impl;

import components.Calculator;
import components.Components;
import components.DataFactory;
import components.Formatter;
import components.Validator;

public final class ComponentsImpl implements Components {
    /**
     * singleton instance of {@link Calculator} implementation class
     */
    private final Calculator calculator;

    /**
     * singleton instance of {@link Calculator} implementation class
     */
    private final DataFactory dataFactory;

    /**
     * singleton instance of {@link Calculator} implementation class
     */
    private final Validator validator;

    private final Formatter formatter;

    /**
     * static singleton instance of class {@link ComponentImpl}
     */
    private static final Components instance = new ComponentsImpl();

    /**
     * Getter of {@link Component} implementation class singleton.
     * @return reference to singleton instance of {@link CalComponent} implementation class
     */
    public static Components getInstance() {
        return instance;
    }

    /**
     * Private constructor as part of the singleton pattern that creates
     * singleton instances of {@link Component} implementation classes.
     */
    private ComponentsImpl() {
        var dataFactory = new DataFactoryImpl();
        this.calculator = new CalculatorImpl();
        this.dataFactory = dataFactory;
        this.validator = dataFactory;
        this.formatter = null;
    }

    /**
     * Getter of {@link Calculator} component implementation class singleton.
     * @return reference to singleton instance of {@link Calculator} implementation class
     */
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
}
