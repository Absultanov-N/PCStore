package com.pcstore.patterns.builder;

import com.pcstore.domain.model.PCConfiguration;
import com.pcstore.domain.model.Product;

public interface PCConfigurationBuilder {
    PCConfigurationBuilder reset();
    PCConfigurationBuilder name(String name);
    PCConfigurationBuilder add(Product product);
    PCConfiguration build();
}
