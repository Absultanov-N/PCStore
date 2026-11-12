package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class Ram extends Product {
 private final String type; private final int capacityGb;
 public Ram(String id,String name,BigDecimal price,int stock,String type,int capacityGb){super(id,name,ProductCategory.RAM,price,stock);this.type=type;this.capacityGb=capacityGb;}
 public String getType(){return type;} public int getCapacityGb(){return capacityGb;}
}
