package com.pcstore.patterns;

import com.pcstore.domain.model.Cpu;
import com.pcstore.domain.model.Product;
import com.pcstore.domain.observer.ProductChangeType;
import com.pcstore.domain.observer.ProductObserver;
import com.pcstore.service.ShoppingCart;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObserverPatternTest {

    @Test
    void notifiesObserversWhenPriceAndStockChange() {
        Product product = cpu();
        List<ProductChangeType> events = new ArrayList<>();
        ProductObserver observer = (changedProduct, type) -> {
            assertSame(product, changedProduct);
            events.add(type);
        };
        product.subscribe(observer);

        product.changePrice(new BigDecimal("120"));
        product.changeStock(4);

        assertEquals(List.of(
                ProductChangeType.PRICE_CHANGED,
                ProductChangeType.STOCK_CHANGED
        ), events);
    }

    @Test
    void doesNotNotifyWhenValueHasNotChanged() {
        Product product = cpu();
        List<ProductChangeType> events = new ArrayList<>();
        product.subscribe((changedProduct, type) -> events.add(type));

        product.changePrice(new BigDecimal("100.00"));
        product.changeStock(5);

        assertTrue(events.isEmpty());
    }

    @Test
    void duplicateSubscriptionDoesNotCauseDuplicateNotification() {
        Product product = cpu();
        List<ProductChangeType> events = new ArrayList<>();
        ProductObserver observer = (changedProduct, type) -> events.add(type);

        product.subscribe(observer);
        product.subscribe(observer);
        product.changeStock(4);

        assertEquals(1, events.size());
    }

    @Test
    void cartUsesCurrentProductPriceAfterObserverNotification() {
        Product product = cpu();
        ShoppingCart cart = new ShoppingCart();
        cart.add(product, 2);

        assertEquals(new BigDecimal("200"), cart.subtotal());

        product.changePrice(new BigDecimal("125"));

        assertEquals(new BigDecimal("250"), cart.subtotal());
    }

    @Test
    void removingOneOfDuplicateLinesKeepsCartSubscribed() {
        Product product = cpu();
        ShoppingCart cart = new ShoppingCart();
        cart.add(product, 1);
        cart.add(product, 2);

        cart.removeAt(0);

        String output = captureOutput(() -> product.changeStock(4));
        assertTrue(output.contains("STOCK_CHANGED"),
                "Cart must remain subscribed while another line references the product");
    }

    @Test
    void removingLastLineUnsubscribesCart() {
        Product product = cpu();
        ShoppingCart cart = new ShoppingCart();
        cart.add(product, 1);

        cart.removeAt(0);

        String output = captureOutput(() -> product.changeStock(4));
        assertFalse(output.contains("[Observer]"),
                "Cart must stop receiving events after its last line is removed");
    }

    @Test
    void clearingCartUnsubscribesFromAllProducts() {
        Product first = cpu();
        Product second = new Cpu("CPU-002", "Second CPU",
                new BigDecimal("200"), 3, "AM5");
        ShoppingCart cart = new ShoppingCart();
        cart.add(first, 1);
        cart.add(second, 1);

        cart.clear();

        String output = captureOutput(() -> {
            first.changeStock(4);
            second.changeStock(2);
        });
        assertFalse(output.contains("[Observer]"));
        assertTrue(cart.getItems().isEmpty());
    }

    private static Product cpu() {
        return new Cpu("CPU-001", "Test CPU", new BigDecimal("100"), 5, "AM5");
    }

    private static String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }
}
