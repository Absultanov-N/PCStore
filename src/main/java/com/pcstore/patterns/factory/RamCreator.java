package com.pcstore.patterns.factory;
import com.pcstore.domain.model.Product;
import com.pcstore.domain.model.Ram;
import java.math.BigDecimal;
public final class RamCreator extends ProductCreator {
    @Override public Product createProduct(String id, String name, BigDecimal price, int stock, String... specs) {
        requireSpecificationCount(specs, 2);
        return new Ram(id, name, price, stock, specs[0], parsePositiveInt(specs[1], "RAM capacity"));
    }
}
