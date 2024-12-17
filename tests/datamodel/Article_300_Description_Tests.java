package datamodel;

import org.junit.jupiter.api.Test;
import static datamodel.Pricing.PricingCategory.BasePricing;
import static datamodel.Pricing.TAXRate.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Article_300_Description_Tests {
    @Test
    public void test300_ArticleDescriptionRegularCases(){
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Hut",100, BasePricing, Regular);
        assertFalse(optional.isEmpty());
        var article = optional.get();
        assertEquals("Hut", article.getDescription());
    }

    @Test
    public void test301_ArticleDescriptionRegularCases(){
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Bohrhammer",100, BasePricing, Regular);
        assertFalse(optional.isEmpty());
        var article = optional.get();
        assertEquals("Bohrhammer", article.getDescription());
    }

    @Test
    public void test310_ArticleDescriptionCornerCases(){
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("X",10000, BasePricing, Regular);
        assertFalse(optional.isEmpty());
        var article = optional.get();
        assertEquals("X", article.getDescription());
    }

    @Test
    public void test311_ArticleDescriptionCornerCases(){
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("Blaue Wintermütze passend zum hellgrünen Pullover",10000, BasePricing, Regular);
        assertFalse(optional.isEmpty());
        var article = optional.get();
        assertEquals("Blaue Wintermütze passend zum hellgrünen Pullover", article.getDescription());
    }

    @Test
    public void test320_ArticleDescriptionExceptionCases(){
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle("",10000, BasePricing, Regular);
        assertTrue(optional.isEmpty());
    }

    @Test
    public void test321_ArticleDescriptionExceptionCases(){
        DataFactory factory = DataFactory.getInstance();
        var optional = factory.createArticle(null,10000, BasePricing, Regular);
        assertTrue(optional.isEmpty());
    }
}
