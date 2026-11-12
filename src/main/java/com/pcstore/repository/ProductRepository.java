package com.pcstore.repository;

import com.pcstore.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(String id);
    void save(Product product);
}
