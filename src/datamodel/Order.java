package datamodel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order
 */
public class Order {
	private final long id;
	private final Customer customer;
	private final double pricing;
	private final LocalDateTime created;
	private final List<OrderItem> orderItems;

	public Order(long id, Customer customer, double pricing, LocalDateTime created, List<OrderItem> orderItems) {
		this.id = id;
		this.customer = customer;
		this.pricing = pricing;
		this.created = created;
		this.orderItems = orderItems;
	}

	// getter
	public long get_id() {
		return id;
	}
	public Customer get_customer() {
		return customer;
	}
	public double get_pricing() {
		return pricing;
	}
	public LocalDateTime get_created() {
		return created;
	}
	public Iterable<OrderItem> get_orderItems() {
		return orderItems;
	}

	// other methods
	public long itemsCount() {
		return orderItems.size();
	}

	public Order addItem(Article article, long unitsOrdered) {
		var item = new OrderItem(article, unitsOrdered);
		orderItems.add(item);
		return this;
	}
	public void deleteItem(int i) {
		orderItems.remove(i);
	}
	public void deleteItems(Iterable<Integer> indicies) {
		for (Integer i : indicies) {
			orderItems.remove((int) i);
		}
	}
}
