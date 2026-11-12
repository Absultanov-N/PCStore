package com.pcstore.domain.model;
import com.pcstore.domain.observer.*;
import java.util.*;
public abstract class ObservableProduct {
    private final Set<ProductObserver> observers = new HashSet<>();
    public final void subscribe(ProductObserver observer) { observers.add(Objects.requireNonNull(observer)); }
    public final void unsubscribe(ProductObserver observer) { observers.remove(observer); }
    protected final void notifyObservers(ProductChangeType type) {
        for (ProductObserver observer : List.copyOf(observers)) observer.onProductChanged((Product)this, type);
    }
}
