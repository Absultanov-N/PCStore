package com.pcstore.patterns.facade;

import com.pcstore.domain.model.*;
import com.pcstore.repository.CheckoutRepository;
import com.pcstore.service.ShoppingCart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public final class CheckoutFacade {
    private final ShoppingCart cart;
    private final CheckoutRepository checkoutRepository;

    public CheckoutFacade(ShoppingCart cart, CheckoutRepository checkoutRepository) {
        this.cart = Objects.requireNonNull(cart);
        this.checkoutRepository = Objects.requireNonNull(checkoutRepository);
    }

    public Order checkout() {
        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        BigDecimal subtotal = cart.subtotal();
        BigDecimal discount = cart.discount();
        BigDecimal total = subtotal.subtract(discount);

        List<Order.OrderLine> lines = new ArrayList<>();
        Map<String, Integer> requiredQuantities = new HashMap<>();
        Map<String, Product> productsById = new HashMap<>();

        for (CartItem item : items) {
            Purchasable purchasable = item.item();
            BigDecimal unitPrice = purchasable.getCurrentPrice();
            List<Product> components = purchasable.getComponents();
            List<Order.ComponentSnapshot> componentSnapshots = new ArrayList<>();

            // Keep a historical snapshot for every physical component.
            // For a standalone product this list contains exactly one component.
            if (!components.isEmpty()) {
                for (Product product : components) {
                    int componentQuantity = item.quantity();
                    BigDecimal componentUnitPrice = product.getPrice();
                    componentSnapshots.add(new Order.ComponentSnapshot(
                            product.getId(),
                            product.getName(),
                            product.getCategory().name(),
                            componentQuantity,
                            componentUnitPrice,
                            componentUnitPrice.multiply(BigDecimal.valueOf(componentQuantity))
                    ));
                }
            }

            lines.add(new Order.OrderLine(
                    purchasable.getDisplayName(),
                    item.quantity(),
                    unitPrice,
                    unitPrice.multiply(BigDecimal.valueOf(item.quantity())),
                    componentSnapshots
            ));

            // Decorators delegate getComponents(), so only physical products affect stock.
            for (Product product : components) {
                requiredQuantities.merge(product.getId(), item.quantity(), Integer::sum);
                productsById.put(product.getId(), product);
            }
        }

        Order order = new Order(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                lines,
                subtotal,
                discount,
                total
        );

        // The repository commits stock deductions, order, lines and component snapshots atomically.
        Map<String, Integer> committedStock =
                checkoutRepository.completeCheckout(order, requiredQuantities);

        // Synchronize domain objects from committed database values, not stale in-memory values.
        // Observer failures must not make a committed order look like a failed checkout.
        for (Map.Entry<String, Integer> entry : committedStock.entrySet()) {
            Product product = productsById.get(entry.getKey());
            try {
                product.changeStock(entry.getValue());
            } catch (RuntimeException observerFailure) {
                System.err.println("Order was committed, but an observer failed: "
                        + observerFailure.getMessage());
            }
        }

        cart.clear();
        return order;
    }
}
