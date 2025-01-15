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
}