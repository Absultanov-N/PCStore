package com.pcstore.service;

import com.pcstore.domain.model.CartItem;
import com.pcstore.domain.model.Product;
import com.pcstore.domain.model.Purchasable;
import com.pcstore.domain.observer.ProductChangeType;
import com.pcstore.domain.observer.ProductObserver;
import com.pcstore.patterns.strategy.DiscountStrategy;
import com.pcstore.patterns.strategy.NoDiscountStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Shopping cart and Observer for products contained in its purchasable items.
 *
 * A product is subscribed once while at least one cart line references it.
 * The reference count prevents removing one duplicate cart line from
 * accidentally unsubscribing the cart while another line still uses the product.
 */
public final class ShoppingCart implements ProductObserver {
    private final List<CartItem> items = new ArrayList<>();
    private final Map<Product, Integer> observedProducts = new HashMap<>();
    private DiscountStrategy discountStrategy = new NoDiscountStrategy();

    public void add(Purchasable item, int quantity) {
        CartItem cartItem = new CartItem(item, quantity); // Validate before changing cart state.
        items.add(cartItem);
        for (Product product : item.getComponents()) {
            int references = observedProducts.getOrDefault(product, 0);
            if (references == 0) {
                product.subscribe(this);
            }
            observedProducts.put(product, references + 1);
        }
    }

    /**
     * Removes the cart line at the specified zero-based index.
     * Returns the removed line.
     */
    public CartItem removeAt(int index) {
        CartItem removed = items.remove(index);
        for (Product product : removed.item().getComponents()) {
            int references = observedProducts.getOrDefault(product, 0);
            if (references <= 1) {
                observedProducts.remove(product);
                product.unsubscribe(this);
            } else {
                observedProducts.put(product, references - 1);
            }
        }
        return removed;
    }

    public void replaceAt(int index, Purchasable replacement, int quantity) {
        CartItem validated = new CartItem(replacement, quantity);
        List<Product> replacementComponents = List.copyOf(replacement.getComponents());
        CartItem previous = items.get(index);

        // Update subscriptions only for components that actually change.
        for (Product product : previous.item().getComponents()) {
            int references = observedProducts.getOrDefault(product, 0);
            if (references <= 1) {
                observedProducts.remove(product);
                product.unsubscribe(this);
            } else {
                observedProducts.put(product, references - 1);
            }
        }

        items.set(index, validated);
        for (Product product : replacementComponents) {
            int references = observedProducts.getOrDefault(product, 0);
            if (references == 0) {
                product.subscribe(this);
            }
            observedProducts.put(product, references + 1);
        }
    }

    public void clear() {
        for (Product product : observedProducts.keySet()) {
            product.unsubscribe(this);
        }
        observedProducts.clear();
        items.clear();
        discountStrategy = new NoDiscountStrategy();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public void setDiscountStrategy(DiscountStrategy strategy) {
        discountStrategy = Objects.requireNonNull(strategy, "Discount strategy cannot be null");
    }

    public String discountDescription() {
        return discountStrategy.description();
    }

    public BigDecimal subtotal() {
        return items.stream()
                .map(item -> item.item().getCurrentPrice()
                        .multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal discount() {
        BigDecimal subtotal = subtotal();
        BigDecimal calculated = Objects.requireNonNull(
                discountStrategy.calculateDiscount(subtotal),
                "Discount strategy returned null"
        );
        return calculated.max(BigDecimal.ZERO).min(subtotal);
    }

    public BigDecimal total() {
        return subtotal().subtract(discount());
    }

    @Override
    public void onProductChanged(Product product, ProductChangeType changeType) {
        System.out.println("[Observer] " + product.getName() + " changed: " + changeType
                + "; cart totals will use current values.");
    }
}
