package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class Cpu extends Product {
 private final String socket;
 public Cpu(String id,String name,BigDecimal price,int stock,String socket){super(id,name,ProductCategory.CPU,price,stock);this.socket=socket;}
 public String getSocket(){return socket;}
}
