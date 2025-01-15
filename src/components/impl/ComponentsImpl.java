package components.impl;

import components.Calculator;
import components.Components;


/**
 * Implementation class of interface {@link Component} with {@code getInstance()}
 * methods for component singleton objects.
 *
 * Class itself is a <i>singleton</i> object. It creates component singleton objects
 * and holds their references.
 */
public final class ComponentsImpl implements Components {

    /**
     * static singleton instance of class {@link ComponentImpl}
     */
    private static final Components instance = new ComponentsImpl();


    /**
     * Private constructor as part of the singleton pattern that creates
     * singleton instances of {@link Component} implementation classes.
     */
    private ComponentsImpl() { }

    /**
     * Getter of {@link Component} implementation class singleton.
     * @return reference to singleton instance of {@link CalComponent} implementation class
     */
    public static Components getInstance() {
        return instance;
    }
}