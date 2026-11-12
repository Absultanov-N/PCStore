package com.pcstore.patterns.factory;
import com.pcstore.domain.model.Cpu;
import com.pcstore.domain.model.Product;
import java.math.BigDecimal;
public final class CpuCreator extends ProductCreator {
    @Override public Product createProduct(String id, String name, BigDecimal price, int stock, String... specs) {
        requireSpecificationCount(specs, 1);
        return new Cpu(id, name, price, stock, specs[0]);
    }
}
