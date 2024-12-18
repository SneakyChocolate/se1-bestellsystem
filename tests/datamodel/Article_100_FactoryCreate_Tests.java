package datamodel;

import datamodel.Pricing.TAXRate;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static datamodel.Pricing.PricingCategory.BasePricing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Article_100_FactoryCreate_Tests {
    @Test
    @Order(100)
    public void test100_RegularArticleCreation() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Laptop", 125000, BasePricing, TAXRate.Regular);
        assertFalse(optional.isEmpty());
        var article = optional.get();
        assertEquals(article.getDescription(), "Laptop");
    }

    @Test
    @Order(100)
    public void test101_RegularArticleCreation() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Sneaker", 9999, BasePricing, TAXRate.Regular);
        assertFalse(optional.isEmpty());
        var articleSecond = optional.get();
        assertEquals(articleSecond.getDescription(), "Sneaker");
    }

    @Test
    @Order(100)
    public void test102_RegularArticleCreation() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Butter", 239, BasePricing, TAXRate.Reduced);
        assertFalse(optional.isEmpty());
        var articleThird = optional.get();
        assertEquals(articleThird.getDescription(), "Butter");
    }

    @Test
    @Order(110)
    public void test110_ArticleCreationCornerCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("X", 001, BasePricing, TAXRate.Regular);
        assertFalse(optional.isEmpty());
        var article = optional.get();
        assertEquals(article.getDescription(), "X");


    }
    @Test
    @Order(110)
    public void test111_ArticleCreationCornerCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("X", 000, BasePricing, TAXRate.Regular);
        assertFalse(optional.isEmpty());
        var articleSecond = optional.get();
        assertEquals(articleSecond.getDescription(), "X");
    }
    @Test
    @Order(110)
    public void test112_ArticleCreationCornerCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Blaue Wintermütze passend zum hellgrünen Pullover", 999999999, BasePricing, TAXRate.Regular);
        assertFalse(optional.isEmpty());
        var articleThird = optional.get();
        assertEquals(articleThird.getDescription(), "Blaue Wintermütze passend zum hellgrünen Pullover");
    }

    @Test
    @Order(120)
    public void test120_ArticleCreationExceptionCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("", 100, BasePricing, TAXRate.Regular);
        assertTrue(optional.isEmpty());
        assertThrows(NullPointerException.class, () -> factory.createArticle("Hut", 1000, BasePricing, null));

    }
    @Test
    @Order(120)
    public void test121_ArticleCreationExceptionCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle(null, 100, BasePricing, TAXRate.Regular);
        assertTrue(optional.isEmpty());
    }
    @Test
    @Order(120)
    public void test122_ArticleCreationExceptionCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Hut", -001, BasePricing, TAXRate.Regular);
        assertTrue(optional.isEmpty());
    }
    @Test
    @Order(120)
    public void test123_ArticleCreationExceptionCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Hut", -10000, BasePricing, TAXRate.Regular);
        assertTrue(optional.isEmpty());
    }
    @Test
    @Order(120)
    public void test124_ArticleCreationExceptionCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Hut", 1000, null, TAXRate.Regular);
        assertTrue(optional.isEmpty());
    }
    @Test
    @Order(120)
    public void test125_ArticleCreationExceptionCases() {
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle(null, 0, null, null, null);
        assertTrue(optional.isEmpty());
    }
}
