package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class Cooler extends Product {
 private final String socket;
 public Cooler(String id,String name,BigDecimal price,int stock,String socket){super(id,name,ProductCategory.COOLER,price,stock);this.socket=socket;}
 public String getSocket(){return socket;}
}
