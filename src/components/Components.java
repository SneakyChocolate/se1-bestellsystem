package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
}