package datamodel;

import java.time.LocalDateTime;
import components.DataFactory;

/**
 * Factory class that injects creator functions of datamodel classes only to
 * {@link DataFactory} in a protected way just after {@link DataFactory} itself
 * has been created and the {@code inject()} method has been called.
 * Class has no effect after this first invocation.
 * <br>
 * Datamodel classes have protected constructors and are not allowed to be
 * created through {@code new} from outside this package with the exception
 * of the {@link DataFactory} component providing the only means of creating
 * datamodel objects from validated arguments. {@link DataFactory} requires
 * a facility in package {@link datamodel} to invoke protected constructors,
 * which is provided via creator functions.
 */
public final class ProtectedFactory {

    /**
     * Definition of lambda function to create a {@link Customer} object.
     */
    @FunctionalInterface
    public interface CustomerCreator {
        Customer createCustomer(long id, String firstName, String lastName);
    }

    /**
     * Definition of lambda function to create an {@link Article} object.
     */
    @FunctionalInterface
    public interface ArticleCreator {
        Article createArticle(String id, String description);
    }

    /**
     * Definition of lambda function to create an {@link Order} object.
     */
    @FunctionalInterface
    public interface OrderCreator {
        Order createOrder(long id, Customer customer, Pricing pricing, LocalDateTime created);
    }

    /**
     * Definition of lambda function to inject creator functions upon firts invocation.
     */
    @FunctionalInterface
    public interface Injector {
        void inject(CustomerCreator customerCreator, ArticleCreator articleCreator, OrderCreator orderCreator);
    }

    /**
     * Flag to indicate first invocation.
     */
    private static boolean firstCall = true;


    /**
     * Private constructor to prevent object instantiation. Cconstructor is never called
     * (example of "dead code with purpose").
     */
    private ProtectedFactory() { }

    /**
     * Static accessor method to {@link ProtectedFactory} <i>singleton</i> instance.
     * Function only injects creator functions upon first invocation, which occurs when
     * {@link DataFactory} instance is created.
     * @param datafactory reference to {@link DataFactory} to inject creator functions
     * @param injector injector to which creator functions are injected upon first invocation
     */
    public static void inject(DataFactory datafactory, Injector injector) {
        if(firstCall && datafactory != null && injector != null) {
            firstCall = false;
            injector.inject(
                (id, firstName, lastName) -> new Customer(id, firstName, lastName),
                (id, description) -> new Article(id, description),
                (id, customer, pricing, created) -> new Order(id, customer, pricing, created)
            );
        }
    }
}