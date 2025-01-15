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
    public static Components getInstance() {
        return components.impl.ComponentsImpl.getInstance();
    }
}
