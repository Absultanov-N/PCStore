package com.pcstore.patterns.strategy;
import java.math.BigDecimal;
public final class NoDiscountStrategy implements DiscountStrategy {public BigDecimal calculateDiscount(BigDecimal s){return BigDecimal.ZERO;}public String description(){return "No discount";}}
