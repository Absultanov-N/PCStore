package com.pcstore.patterns.strategy;
import java.math.BigDecimal;
public interface DiscountStrategy { BigDecimal calculateDiscount(BigDecimal subtotal); String description(); }
