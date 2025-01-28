package datamodel;

import components.DataFactory;
import components.impl.ComponentsImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Article_200_Id_Tests {
    private final static Pattern pattern = Pattern.compile("SKU-[0-9]{6}");

    private boolean doesMatchIdPattern(String toCheck) {
        return pattern.matcher(toCheck)
            .find();
    }

    @Test
    public void test200_ArticleIdPattern() {
        DataFactory factory = ComponentsImpl.getInstance()
            .getDataFactory();
        var optional = factory.createArticle("Eimer", 1000, Pricing.PricingCategory.BasePricing, Pricing.TAXRate.Regular);
        assertFalse(optional.isEmpty());
        var article = optional.get();
        String actual = article.getId();
        assertTrue(doesMatchIdPattern(actual));
        assertEquals(10, actual.length());
    }

    @Test
    public void test210_ArticleIdUniqueness() {
        DataFactory factory = ComponentsImpl.getInstance()
            .getDataFactory();
        for (int i = 0; i < 1000; i++) {
            var optional = factory.createArticle("Eimer",1000, Pricing.PricingCategory.BasePricing, Pricing.TAXRate.Regular);
            var article = optional.get();
            assertTrue(doesMatchIdPattern(article.getId()));
        }
    }
}
