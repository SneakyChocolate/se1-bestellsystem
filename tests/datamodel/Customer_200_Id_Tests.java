package datamodel;

import components.DataFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;


/**
 * Tests for {@link Customer} class: [200..299] Id-tests with tested
 * methods:
 * <pre>
 * - getId()
 * </pre>
 * @author sgra64
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Customer_200_Id_Tests {

    /*
     * Reference to DataFactory.
     */
    private final DataFactory dataFactory = DataFactory.getInstance();


    /*
     * Test case that {@link DataFactory} assigns correct {@code id} values.
     */
    @Test @Order(200)
    void test200_getId() {
        // DataFactory singleton has already exhausted initial id pool
        // with test test200_getIdFromInitialPool()
        //
        // get more objects from factory after initial id pool has been exhausted
        int objectNumber = 25;
        var moreIds = IntStream.range(0, objectNumber)
            .boxed()
            .map(i -> dataFactory.createCustomer("Eric Meyer", "eric98@yahoo.com"))
            .filter(c -> c.isPresent())
            .map(o -> o.get().getId())
            .toList();
        //
        long validIds = moreIds.stream()
            .filter(id -> id >= 100000L && id <= 999999L)
            .count();
        //
        assertEquals(objectNumber, validIds);
    }
}