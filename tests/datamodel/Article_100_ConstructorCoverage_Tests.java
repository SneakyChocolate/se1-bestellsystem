package datamodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Article_100_ConstructorCoverage_Tests {
    @Test
    public void test100_ArticleConstructor() {
        Assertions.assertDoesNotThrow(() -> {
            var art = new Article("SKU-203954", "Kanne");
            Assertions.assertNotNull(art);
            Assertions.assertEquals("SKU-203954", art.getId());
            Assertions.assertEquals("Kanne",art.getDescription());
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Customer(-2,"Bernd","Bernd");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Customer(53532,"Bernd","");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Customer(53532,"Bernd",null);
        });
    }

    @Test
    public void test110_ArticleConstructorIdNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Article(null, "Kanne");
        });
    }

    @Test
    public void test120_ArticleConstructorDescriptionNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Article("SKU-203954", null);
        });
    }

    @Test
    public void test121_ArticleConstructorDescriptionEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Article("SKU-203954", "");
            new Article("SKU-203954", "      ");
        });
    }
}
