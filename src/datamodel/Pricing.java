package datamodel;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Immutable class to store pricing information organized by
 * {@link PricingCategory}
 * instances such as:
 * 
 * <pre>
 * - <i>BasePricing(Country.Germany)</i>,
 * - <i>BlackFridayPricing(Country.Germany)</i> (discounts),
 * - <i>SwissPricing(Country.Switzerland)</i>,
 * - <i>UKPricing(Country.UnitedKingdom)</i>.
 * </pre>
 * 
 * Pricing includes a {@link TAXRate} applicable to each {@link Article}
 * and a {@link Currency} that is associated with a {@link Country} and
 * in which an {@link Article} is priced.
 * 
 * Value-added {@link TAXRate} (VAT) categories used in different countries
 * are:
 * 
 * <pre>
 * - https://de.wikipedia.org/wiki/Umsatzsteuer_(Deutschland)
 * - https://de.wikipedia.org/wiki/Mehrwertsteuer_(Schweiz)
 * - https://www.gov.uk/vat-rates
 * </pre>
 * 
 * @version <code style=
 *          color:green>{@value application.package_info#Version}</code>
 * @author <code style=
 *         color:blue>{@value application.package_info#Author}</code>
 */
@Accessors(fluent = true)
@Getter
public class Pricing {
	private final double[] taxRateValues;

	/**
	 * Enum of pricing categories
	 */
	@Getter
	public enum PricingCategory {
		/** regular prices in Germany */
		BasePricing(Country.Germany),
		SwissPricing(Country.Swiss),
		UKPricing(Country.UK),
		BlackFridayPricing(Country.Germany),
		;

		private final Country country;
		private final Pricing pricing;
		public Country country() {
			return country;
		}
		public Pricing pricing() {
			return pricing;
		}

		PricingCategory(Country country) {
			this.country = country;
			this.pricing = new Pricing(country, this);
		}
	}

	/**
	 * Enum of TAXRates for {@link Article} instances.
	 */
	public enum TAXRate {
		/** German regular rate 19% */
		Regular,
		/** German reduced rate 7% */
		Reduced,
		/** Swiss 3rd special rate */
		Special,
		/** Tax-free rate 0% */
		Excempt;

		/**
		 * Parse Enum value from a String.
		 * 
		 * @param s String to parse
		 * @return TAXRate parsed from String
		 * @throws ParseException thrown when TAXRate could not be parsed
		 */
		public static TAXRate parse(String s) throws ParseException {
			switch (s) {
				case "regular":
					return TAXRate.Regular;
				case "reduced":
					return TAXRate.Reduced;
				case "special":
					return TAXRate.Special;
				default:
					return TAXRate.Excempt;
			}
		}
	}

	/**
	 * Enum or countries with currencies used.
	 */
	@Getter
	public enum Country {
		Germany(Currency.Euro),
		Swiss(Currency.SwissFranc),
		UK(Currency.PoundSterling),
		;

		private final Currency currency;
		public Currency currency() {
			return currency;
		}
		Country(Currency currency) {
			this.currency = currency;
		}
	}

	/**
	 * Enum of currency with three-letter code and Unicode symbol.
	 */
	@Getter
	public enum Currency {
		Euro("EUR", "\u20AC"),
		SwissFranc("CHF", "\u20A3"),
		PoundSterling("GBP", "\u20A5"),
		;

		private final String code;
		private final String unicode;
		public String code() {
			return code;
		}
		public String unicode() {
			return unicode;
		}
		Currency(String code, String unicode) {
			this.code = code;
			this.unicode = unicode;
		}
	}

	/**
	 * Record stored in {@link articlePriceMap} for an article.
	 */
	private record PriceRecord(long unitPrice, TAXRate taxRate) {
	}

	/**
	 * Map of articles with associated {@link PriceRecord}
	 * (not exposed as getter).
	 */
	@Getter(AccessLevel.NONE)
	private final Map<Article, PriceRecord> articlePriceMap = new HashMap<>();
	public Map<Article, PriceRecord> articlePriceMap() {
		return articlePriceMap;
	};

	/**
	 * Country of this {@link Pricing} instance.
	 */
	private final Country country;

	/**
	 * Currency associated with this {@link Pricing} instance.
	 */
	private final Currency currency;
	private final PricingCategory category;

	public Country country() {
		return country;
	}
	public Currency currency() {
		return currency;
	}
	public PricingCategory category() {
		return category;
	}
	/**
	 * Private constructor.
	 * 
	 * @param country {@link Country} associated with this instance
	 */
	private Pricing(Country country, PricingCategory pricingCategory) {
		this.country = country;
		this.currency = country.currency();
		this.category = pricingCategory;
		switch (pricingCategory) {
			case BasePricing:
				this.taxRateValues = new double[]{19,7,0,0};
				break;
			case SwissPricing:
				this.taxRateValues = new double[]{8.1,2.6,3.8,0};
				break;
			case UKPricing:
				this.taxRateValues = new double[]{20,5,0,0};
				break;
			case BlackFridayPricing:
				this.taxRateValues = new double[]{19,7,0,0};
				break;
			default:
				this.taxRateValues = new double[]{0,0,0,0};
				break;
		}
	}

	/**
	 * Store or update {@link Article} with new {@link PriceRecord} in
	 * article price map.
	 * 
	 * @param article   article to store or update in article price map
	 * @param unitPrice price associated with one unit of the {@link Article}
	 * @param taxRate   tax rate applicable to {@link Article}
	 * @return chainable self-reference
	 */
	public Pricing put(Article article, long unitPrice, TAXRate taxRate, PricingCategory pricingCategory) {
		switch (pricingCategory) {
			case BasePricing:
				PricingCategory.BasePricing.pricing().articlePriceMap.put(article, new PriceRecord(unitPrice, taxRate));
				PricingCategory.BlackFridayPricing.pricing().articlePriceMap.put(article, new PriceRecord(adjustPrice(unitPrice, 0.8), taxRate));
				PricingCategory.SwissPricing.pricing().articlePriceMap.put(article, new PriceRecord(adjustPrice(unitPrice, 1.8), taxRate));
				PricingCategory.UKPricing.pricing().articlePriceMap.put(article, new PriceRecord(adjustPrice(unitPrice, 1.15), taxRate));
				break;
			default:
				var priceRecord = new PriceRecord(unitPrice, taxRate);
				articlePriceMap.put(article, priceRecord);
				break;
		}
		return this;
	}

	/**
	 * Return unit price for {@link Article}.
	 * 
	 * @param article subject of unit price request
	 * @return article unit price or {@code 0} if article is not in price map
	 */
	public long unitPrice(Article article) {
		return Optional.ofNullable(articlePriceMap.get(article))
			.map(p -> p.unitPrice).orElse(0L);
	}

	/**
	 * Return {@link TAXRate} for {@link Article}.
	 * 
	 * @param article subject of tax rate request
	 * @return tax rate that applies to article
	 */
	public TAXRate taxRate(Article article) {
		return Optional.ofNullable(articlePriceMap.get(article))
				.map(p -> p.taxRate()).orElse(TAXRate.Regular);
	}

	/**
	 * Return {@link TAXRate} as percent value for {@link Article}, e.g.
	 * value 19.0 for tax rate of 19%.
	 * 
	 * @param article subject of tax rate request
	 * @return tax rate as percent value that applies to article
	 */
	public double taxRateAsPercent(Article article) {
		var taxRate = taxRate(article);
		switch (taxRate) {
			case Regular:
				return taxRateValues[0];
			case Reduced:
				return taxRateValues[1];
			case Special:
				return taxRateValues[2];
			case Excempt:
				return taxRateValues[3];
			default:
				return 0.0;
		}
	}

	/**
	 * Adjust price to a factor, e.g. a currency exchange rate or a discount
	 * rate. The adjustment is mapped to a trailing 5 or 9, e.g. price: 2497
	 * is mapped to: 2499.
	 * 
	 * @param price  price to adjust
	 * @param factor exchange or discount rate
	 * @return adjusted price
	 */
	private long adjustPrice(long price, double factor) {
		long newPrice = (long) (price * factor);
		long base = (newPrice / 10) * 10;
		long dig = (newPrice % 10 <= 5 && newPrice >= 20) ? 5 : 9;
		return base + dig;
	}
}
