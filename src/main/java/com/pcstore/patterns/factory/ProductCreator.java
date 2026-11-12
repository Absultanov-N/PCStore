package com.pcstore.patterns.factory;

import com.pcstore.domain.model.Product;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class ProductCreator {
    public abstract Product createProduct(String id, String name, BigDecimal price, int stock,
                                          String... specifications);

    protected final void requireSpecificationCount(String[] specifications, int expected) {
        Objects.requireNonNull(specifications, "Specifications cannot be null");
        if (specifications.length != expected) {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + " requires exactly " + expected + " specification(s)");
        }
        for (String specification : specifications) {
            if (specification == null || specification.isBlank()) {
                throw new IllegalArgumentException("Product specifications cannot be blank");
            }
        }
    }

    protected final int parsePositiveInt(String value, String fieldName) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) throw new IllegalArgumentException(fieldName + " must be positive");
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid integer", exception);
        }
    }
}
