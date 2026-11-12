package com.pcstore.patterns.strategy;
import java.math.*;
public final class PercentageDiscountStrategy implements DiscountStrategy {
 private final BigDecimal percent;
 public PercentageDiscountStrategy(BigDecimal percent){if(percent==null||percent.signum()<0||percent.compareTo(new BigDecimal("100"))>0)throw new IllegalArgumentException("Percent must be between 0 and 100");this.percent=percent;}
 public BigDecimal calculateDiscount(BigDecimal subtotal){return subtotal.multiply(percent).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);}
 public String description(){return percent.stripTrailingZeros().toPlainString()+"% discount";}
}
