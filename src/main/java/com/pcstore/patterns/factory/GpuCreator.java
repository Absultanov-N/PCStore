package com.pcstore.patterns.factory;
import com.pcstore.domain.model.Gpu;
import com.pcstore.domain.model.Product;
import java.math.BigDecimal;
public final class GpuCreator extends ProductCreator {
    @Override public Product createProduct(String id, String name, BigDecimal price, int stock, String... specs) {
        requireSpecificationCount(specs, 1);
        return new Gpu(id, name, price, stock, parsePositiveInt(specs[0], "GPU wattage"));
    }
}
