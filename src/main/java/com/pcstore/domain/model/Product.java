package com.pcstore.domain.model;
import com.pcstore.domain.observer.ProductChangeType;
import java.math.BigDecimal;
import java.util.*;
public abstract class Product extends ObservableProduct implements Purchasable {
    private final String id, name;
    private final ProductCategory category;
    private BigDecimal price;
    private int stock;
    protected Product(String id, String name, ProductCategory category, BigDecimal price, int stock) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) throw new IllegalArgumentException("Product id/name required");
        if (price == null || price.signum() < 0 || stock < 0) throw new IllegalArgumentException("Invalid price or stock");
        this.id=id; this.name=name; this.category=Objects.requireNonNull(category); this.price=price; this.stock=stock;
    }
    public String getId(){return id;} public String getName(){return name;} public ProductCategory getCategory(){return category;}
    public BigDecimal getPrice(){return price;} public int getStock(){return stock;}
    public void changePrice(BigDecimal newPrice){if(newPrice==null||newPrice.signum()<0)throw new IllegalArgumentException("Price must be non-negative"); if(price.compareTo(newPrice)!=0){price=newPrice;notifyObservers(ProductChangeType.PRICE_CHANGED);}}
    public void changeStock(int newStock){if(newStock<0)throw new IllegalArgumentException("Stock cannot be negative"); if(stock!=newStock){stock=newStock;notifyObservers(ProductChangeType.STOCK_CHANGED);}}
    @Override public String getDisplayName(){return name;}
    @Override public BigDecimal getCurrentPrice(){return price;}
    @Override public List<Product> getComponents(){return List.of(this);}
    @Override public String toString(){return id+" | "+name+" | "+price+" KZT | stock: "+stock;}
}
