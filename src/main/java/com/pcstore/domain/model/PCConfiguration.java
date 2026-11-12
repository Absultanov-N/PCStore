package com.pcstore.domain.model;
import java.math.BigDecimal;
import java.util.*;
public final class PCConfiguration implements Purchasable {
 private final String name; private final List<Product> components;
 public PCConfiguration(String name,List<Product> components){if(name==null||name.isBlank()||components==null||components.isEmpty())throw new IllegalArgumentException("Configuration requires a name and components");this.name=name;this.components=List.copyOf(components);}
 @Override public String getDisplayName(){return name;}
 @Override public BigDecimal getCurrentPrice(){return components.stream().map(Product::getPrice).reduce(BigDecimal.ZERO,BigDecimal::add);}
 @Override public List<Product> getComponents(){return components;}
}
