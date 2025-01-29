package datamodel;

import components.DataFactory;
import components.impl.ComponentsImpl;

import org.junit.jupiter.api.Test;

import static datamodel.Pricing.PricingCategory.BasePricing;
import static datamodel.Pricing.TAXRate.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Article_500_PriceVAT_Tests {
    @Test
    public void test500_ArticleVATRateRegularCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", 100, BasePricing, Regular);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test501_ArticleVATRateRegularCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", 100, BasePricing, Regular);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test502_ArticleVATRateRegularCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        var optional = factory.createArticle("Hut", 100, BasePricing, Excempt);
        assertFalse(optional.isEmpty());
    }

    @Test
    public void test510_ArticleVATRateExceptionCases() {
        DataFactory factory = ComponentsImpl.getInstance().getDataFactory();
        assertThrows(NullPointerException.class, () -> factory.createArticle("Hut", 100, BasePricing, null));
    }
}
