package components;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to test the JUnit test setup. @Test methods of this class
 * always pass. If JUnit is setup properly, tests should run in the IDE
 * and in the terminal.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Component_Tests {

    @Test
    @Order(100)
    void test_100_getInstance() {
        var instance = Components.getInstance();
        assertNotNull(instance);
        assertTrue(Components.class.isAssignableFrom(instance.getClass()));
        //
        var instance_2 = Components.getInstance();
        assertNotNull(instance_2);
        assertTrue(Components.class.isAssignableFrom(instance_2.getClass()));
        //
        assertEquals(instance, instance_2);
    }


    @Test
    @Order(200)
    void test_200_getCalculator() {
        var system = Components.getInstance();
        assertNotNull(system);
        //
        var calculator = system.getCalculator();
        assertNotNull(calculator);
        assertTrue(Calculator.class.isAssignableFrom(calculator.getClass()));
        //
        var calculator_2 = system.getCalculator();
        assertNotNull(calculator_2);
        assertTrue(Calculator.class.isAssignableFrom(calculator_2.getClass()));
        //
        assertEquals(calculator, calculator_2);
    }
}