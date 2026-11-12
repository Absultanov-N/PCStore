package com.pcstore.domain.observer;
import com.pcstore.domain.model.Product;
public interface ProductObserver { void onProductChanged(Product product, ProductChangeType changeType); }
