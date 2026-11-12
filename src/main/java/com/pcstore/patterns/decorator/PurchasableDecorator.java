package com.pcstore.patterns.decorator;
import com.pcstore.domain.model.*; import java.math.BigDecimal; import java.util.*;
public abstract class PurchasableDecorator implements Purchasable {
 protected final Purchasable wrapped;
 protected PurchasableDecorator(Purchasable wrapped){this.wrapped=Objects.requireNonNull(wrapped);}
 public List<Product> getComponents(){return wrapped.getComponents();}
}
