package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class Storage extends Product {
 private final int capacityGb; private final String interfaceType;
 public Storage(String id,String name,BigDecimal price,int stock,int capacityGb,String interfaceType){super(id,name,ProductCategory.STORAGE,price,stock);this.capacityGb=capacityGb;this.interfaceType=interfaceType;}
 public int getCapacityGb(){return capacityGb;} public String getInterfaceType(){return interfaceType;}
}
