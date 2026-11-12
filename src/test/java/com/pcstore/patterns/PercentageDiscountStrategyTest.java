package com.pcstore.patterns;

import com.pcstore.patterns.strategy.PercentageDiscountStrategy;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PercentageDiscountStrategyTest {
    @Test void calculatesFivePercent() {
        assertEquals(new BigDecimal("5.00"),
                new PercentageDiscountStrategy(new BigDecimal("5"))
                        .calculateDiscount(new BigDecimal("100")));
    }

    @Test void acceptsZeroAndOneHundredPercent() {
        assertEquals(new BigDecimal("0.00"),
                new PercentageDiscountStrategy(BigDecimal.ZERO).calculateDiscount(new BigDecimal("100")));
        assertEquals(new BigDecimal("100.00"),
                new PercentageDiscountStrategy(new BigDecimal("100")).calculateDiscount(new BigDecimal("100")));
    }

    @Test void rejectsPercentageOutsideRange() {
        assertThrows(IllegalArgumentException.class,
                () -> new PercentageDiscountStrategy(new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class,
                () -> new PercentageDiscountStrategy(new BigDecimal("101")));
    }
}
