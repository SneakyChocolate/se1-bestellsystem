package components.impl;

import components.Calculator;
import components.Components;

public final class ComponentsImpl implements Components {

    /**
     * singleton instance of {@link Calculator} implementation class
     */
    private final Calculator calculator;

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
        this.calculator = new CalculatorImpl();
    }

    /**
     * Getter of {@link Calculator} component implementation class singleton.
     * @return reference to singleton instance of {@link Calculator} implementation class
     */
    public Calculator getCalculator() {
        return calculator;
    }
}