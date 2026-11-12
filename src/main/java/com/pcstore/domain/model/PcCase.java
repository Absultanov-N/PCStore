package com.pcstore.domain.model;
import java.math.BigDecimal;
public final class PcCase extends Product {
 private final String formFactor;
 public PcCase(String id,String name,BigDecimal price,int stock,String formFactor){super(id,name,ProductCategory.CASE,price,stock);this.formFactor=formFactor;}
 public String getFormFactor(){return formFactor;}
}
