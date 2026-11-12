package com.pcstore.patterns.builder;

import com.pcstore.domain.model.PCConfiguration;
import com.pcstore.service.ProductService;

import java.util.Objects;

public final class PCConfigurationDirector {
    private final ProductService products;

    public PCConfigurationDirector(ProductService products) {
        this.products = Objects.requireNonNull(products, "Product service cannot be null");
    }

    public PCConfiguration createGamingPC(PCConfigurationBuilder builder) {
        Objects.requireNonNull(builder, "Builder cannot be null");
        return builder.reset()
                .name("Gaming PC")
                .add(products.requireById("CPU-001"))
                .add(products.requireById("GPU-001"))
                .add(products.requireById("MB-001"))
                .add(products.requireById("RAM-001"))
                .add(products.requireById("SSD-001"))
                .add(products.requireById("PSU-001"))
                .add(products.requireById("CASE-001"))
                .add(products.requireById("COOL-001"))
                .build();
    }
}
