package application;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class to test the JUnit test setup. @Test methods of this class
 * always pass. If JUnit is setup properly, tests should run in the IDE
 * and in the terminal.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Application_0_always_pass_Tests {

    /**
     * Method is executed once before any @Test method.
     * @throws Exception if any exception occurs
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        System.out.println("\nsetUpBeforeClass() runs once before any @Test method");
    }

    /**
     * Method is executed before each @Test method
     * @throws Exception if any exception occurs
     */
    @BeforeEach
    public void setUpBeforeEach() throws Exception {
        System.out.println("setUpBeforeEach() runs before each @Test method");
    }

    /**
     * Method is executed after each @Test method
     * @throws Exception if any exception occurs
     */
    @AfterEach
    public void tearDownAfterEach() throws Exception {
        System.out.println("tearDownAfterEach() runs after each @Test method");
    }

    /**
     * First @Test method (always passes).
     */
    @Test
    @Order(001)
    void test_001_always_pass() {
        int expected = 10;
        int actual = 10;
        assertEquals(expected, actual);
    }

    /**
     * Second @Test method (always passes).
     */
    @Test
    @Order(002)
    void test_002_always_pass() {
        int expected = 10;
        int actual = 10;
        assertEquals(expected, actual);
    }

    /**
     * Method is executed once after all @Test methods have finished.
     * @throws Exception if any exception occurs
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        System.out.println("tearDownAfterClass() runs after all @Test methods have finished");
    }
}