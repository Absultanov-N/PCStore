package com.pcstore.repository;

import com.pcstore.domain.model.Order;

import java.util.Map;

/**
 * Persists an order and deducts stock in one database transaction.
 * Returns the committed stock levels for the affected products.
 */
public interface CheckoutRepository {
    Map<String, Integer> completeCheckout(Order order, Map<String, Integer> requiredQuantities);
}
