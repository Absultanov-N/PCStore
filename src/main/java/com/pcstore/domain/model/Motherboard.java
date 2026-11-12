package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class Motherboard extends Product {
 private final String socket, ramType, formFactor;
 public Motherboard(String id,String name,BigDecimal price,int stock,String socket,String ramType,String formFactor){super(id,name,ProductCategory.MOTHERBOARD,price,stock);this.socket=socket;this.ramType=ramType;this.formFactor=formFactor;}
 public String getSocket(){return socket;} public String getRamType(){return ramType;} public String getFormFactor(){return formFactor;}
}
