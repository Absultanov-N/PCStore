package com.pcstore.service;
import com.pcstore.domain.model.Product; import com.pcstore.repository.ProductRepository; import java.util.*;
public final class ProductService {
 private final ProductRepository repository;
 public ProductService(ProductRepository repository){this.repository=repository;}
 public List<Product> findAll(){return repository.findAll();}
 public Product requireById(String id){return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found: "+id));}
 public void add(Product p){repository.save(p);}
}
