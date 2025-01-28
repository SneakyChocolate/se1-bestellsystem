package components;

import datamodel.Order;

import java.util.Optional;
import java.util.function.Consumer;

public interface OrderBuilder {
    Optional<Order> buildOrder(String customerSpec, Consumer<BuildState> buildState);
}
