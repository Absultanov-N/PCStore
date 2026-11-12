package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class PowerSupply extends Product {
 private final int wattage;
 public PowerSupply(String id,String name,BigDecimal price,int stock,int wattage){super(id,name,ProductCategory.POWER_SUPPLY,price,stock);this.wattage=wattage;}
 public int getWattage(){return wattage;}
}
