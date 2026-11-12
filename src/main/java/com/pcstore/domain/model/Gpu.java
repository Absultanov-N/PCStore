package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class Gpu extends Product {
 private final int wattage;
 public Gpu(String id,String name,BigDecimal price,int stock,int wattage){super(id,name,ProductCategory.GPU,price,stock);if(wattage<=0)throw new IllegalArgumentException("Wattage must be positive");this.wattage=wattage;}
 public int getWattage(){return wattage;}
}
