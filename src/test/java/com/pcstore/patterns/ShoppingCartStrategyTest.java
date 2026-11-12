package com.pcstore.patterns;

import com.pcstore.domain.model.Cpu;
import com.pcstore.service.ShoppingCart;
import com.pcstore.patterns.strategy.PercentageDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartStrategyTest {
    @Test
    void clearResetsDiscountToNoDiscount() {
        ShoppingCart cart = new ShoppingCart();
        cart.add(new Cpu("CPU-T", "Test CPU", new BigDecimal("1000"), 5, "AM5"), 1);
        cart.setDiscountStrategy(new PercentageDiscountStrategy(new BigDecimal("5")));
        assertEquals(new BigDecimal("50.00"), cart.discount());

        cart.clear();
        cart.add(new Cpu("CPU-2", "Second CPU", new BigDecimal("1000"), 5, "AM5"), 1);

        assertEquals(BigDecimal.ZERO, cart.discount());
        assertEquals("No discount", cart.discountDescription());
    }
}
