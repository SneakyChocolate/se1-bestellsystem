package datamodel;

import components.DataFactory;
import components.impl.ComponentsImpl;

import org.junit.jupiter.api.Test;
import static datamodel.Pricing.PricingCategory.BasePricing;
import static datamodel.Pricing.TAXRate.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Article_400_Price_Tests {
    @Test
    public void test400_ArticlePriceRegularCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", 10000, BasePricing, Regular);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test401_ArticlePriceRegularCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Rad", 10000, BasePricing, Regular);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test410_ArticlePriceCornerCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", 001, BasePricing, Regular);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test411_ArticlePriceCornerCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", 000, BasePricing, Regular);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test412_ArticlePriceCornerCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", 999999999, BasePricing, Regular);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test420_ArticleDescriptionExceptionCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", -001, BasePricing, Regular);
        assertTrue(optional.isEmpty());
    }

    @Test
    public void test421_ArticleDescriptionExceptionCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", -10000, BasePricing, Regular);
        assertTrue(optional.isEmpty());
    }
}
