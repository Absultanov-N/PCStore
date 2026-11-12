package com.pcstore.domain.model;
import java.util.Objects;
public record CartItem(Purchasable item,int quantity) {
 public CartItem {Objects.requireNonNull(item);if(quantity<=0)throw new IllegalArgumentException("Quantity must be positive");}
}
