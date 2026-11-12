package com.pcstore.infrastructure.database;

import com.pcstore.domain.model.Order;
import com.pcstore.repository.CheckoutRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public final class SqliteCheckoutRepository implements CheckoutRepository {

    @Override
    public Map<String, Integer> completeCheckout(Order order, Map<String, Integer> requiredQuantities) {
        Map<String, Integer> committedStock = new java.util.HashMap<>();
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                deductStock(connection, requiredQuantities);
                insertOrder(connection, order);
                insertOrderLinesAndComponents(connection, order);
                committedStock.putAll(readStockLevels(connection, requiredQuantities.keySet()));
                connection.commit();
            } catch (Exception exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                if (exception instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw new IllegalStateException("Checkout transaction failed", exception);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Checkout transaction failed", exception);
        }
        return Map.copyOf(committedStock);
    }

    private Map<String, Integer> readStockLevels(Connection connection, java.util.Set<String> productIds)
            throws SQLException {
        Map<String, Integer> stockLevels = new java.util.HashMap<>();
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT stock FROM products WHERE id = ?")) {
            for (String productId : productIds) {
                query.setString(1, productId);
                try (ResultSet result = query.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException("Product disappeared during checkout: " + productId);
                    }
                    stockLevels.put(productId, result.getInt("stock"));
                }
            }
        }
        return stockLevels;
    }

    private void deductStock(Connection connection, Map<String, Integer> requiredQuantities) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?")) {
            for (Map.Entry<String, Integer> entry : requiredQuantities.entrySet()) {
                int quantity = entry.getValue();
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Required quantity must be positive");
                }
                update.setInt(1, quantity);
                update.setString(2, entry.getKey());
                update.setInt(3, quantity);
                if (update.executeUpdate() != 1) {
                    throw new IllegalStateException(
                            "Product not found or insufficient stock: " + entry.getKey());
                }
            }
        }
    }

    private void insertOrder(Connection connection, Order order) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO orders(id, created_at, subtotal, discount, total) VALUES(?,?,?,?,?)")) {
            insert.setString(1, order.id());
            insert.setString(2, order.createdAt().toString());
            insert.setString(3, order.subtotal().toPlainString());
            insert.setString(4, order.discount().toPlainString());
            insert.setString(5, order.total().toPlainString());
            insert.executeUpdate();
        }
    }

    private void insertOrderLinesAndComponents(Connection connection, Order order) throws SQLException {
        String lineSql = "INSERT INTO order_items(order_id, description, quantity, unit_price, line_total) " +
                "VALUES(?,?,?,?,?)";
        String componentSql = "INSERT INTO order_item_components(" +
                "order_item_id, product_id, product_name, category, quantity, unit_price, line_total) " +
                "VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement insertLine = connection.prepareStatement(lineSql);
             PreparedStatement findLineId = connection.prepareStatement("SELECT last_insert_rowid()");
             PreparedStatement insertComponent = connection.prepareStatement(componentSql)) {
            for (Order.OrderLine line : order.lines()) {
                insertLine.setString(1, order.id());
                insertLine.setString(2, line.description());
                insertLine.setInt(3, line.quantity());
                insertLine.setString(4, line.unitPrice().toPlainString());
                insertLine.setString(5, line.lineTotal().toPlainString());
                insertLine.executeUpdate();

                if (!line.components().isEmpty()) {
                    long orderItemId;
                    try (ResultSet result = findLineId.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException("Could not obtain inserted order item id");
                        }
                        orderItemId = result.getLong(1);
                    }

                    for (Order.ComponentSnapshot component : line.components()) {
                        insertComponent.setLong(1, orderItemId);
                        insertComponent.setString(2, component.productId());
                        insertComponent.setString(3, component.productName());
                        insertComponent.setString(4, component.category());
                        insertComponent.setInt(5, component.quantity());
                        insertComponent.setString(6, component.unitPrice().toPlainString());
                        insertComponent.setString(7, component.lineTotal().toPlainString());
                        insertComponent.addBatch();
                    }
                    insertComponent.executeBatch();
                }
            }
        }
    }
}
