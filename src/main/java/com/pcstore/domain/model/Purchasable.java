package com.pcstore.domain.model;
import java.math.BigDecimal;
import java.util.List;
public interface Purchasable {
    String getDisplayName();
    BigDecimal getCurrentPrice();
    List<Product> getComponents();
}
