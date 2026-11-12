package com.pcstore.patterns;

import com.pcstore.domain.model.Order;
import com.pcstore.infrastructure.database.SqliteCheckoutRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: a later stock failure must roll back earlier stock updates
 * and must not persist an order. Uses a temporary SQLite file, not pcstore.db.
 */
class CheckoutTransactionTest {
    private Path dbFile;
    private String previousUrl;

    @BeforeEach void setUp() throws Exception {
        previousUrl = System.getProperty("pcstore.db.url");
        dbFile = Files.createTempFile("pcstore-checkout-test", ".db");
        System.setProperty("pcstore.db.url", "jdbc:sqlite:" + dbFile.toAbsolutePath());
        try (Connection c = DriverManager.getConnection(System.getProperty("pcstore.db.url"));
             Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
            s.execute("CREATE TABLE products(id TEXT PRIMARY KEY, stock INTEGER NOT NULL)");
            s.execute("CREATE TABLE orders(id TEXT PRIMARY KEY, created_at TEXT NOT NULL, subtotal TEXT NOT NULL, discount TEXT NOT NULL, total TEXT NOT NULL)");
            s.execute("CREATE TABLE order_items(id INTEGER PRIMARY KEY AUTOINCREMENT, order_id TEXT NOT NULL, description TEXT NOT NULL, quantity INTEGER NOT NULL, unit_price TEXT NOT NULL, line_total TEXT NOT NULL, FOREIGN KEY(order_id) REFERENCES orders(id))");
            s.execute("CREATE TABLE order_item_components(id INTEGER PRIMARY KEY AUTOINCREMENT, order_item_id INTEGER NOT NULL, product_id TEXT NOT NULL, product_name TEXT NOT NULL, category TEXT NOT NULL, quantity INTEGER NOT NULL, unit_price TEXT NOT NULL, line_total TEXT NOT NULL, FOREIGN KEY(order_item_id) REFERENCES order_items(id))");
            s.execute("INSERT INTO products VALUES('A', 5)");
            s.execute("INSERT INTO products VALUES('B', 0)");
        }
    }

    @AfterEach void tearDown() throws Exception {
        if (previousUrl == null) System.clearProperty("pcstore.db.url");
        else System.setProperty("pcstore.db.url", previousUrl);
        Files.deleteIfExists(dbFile);
    }

    @Test void rollsBackAllStockAndOrderChangesWhenAnyItemIsUnavailable() throws Exception {
        Order order = new Order("ORDER-TEST", LocalDateTime.now(),
                List.of(new Order.OrderLine("Test PC", 1, new BigDecimal("100"),
                        new BigDecimal("100"), List.of())),
                new BigDecimal("100"), BigDecimal.ZERO, new BigDecimal("100"));

        Map<String, Integer> quantities = new LinkedHashMap<>();
        quantities.put("A", 1); // This deduction succeeds first.
        quantities.put("B", 1); // This one fails because stock is zero.

        assertThrows(IllegalStateException.class,
                () -> new SqliteCheckoutRepository().completeCheckout(order, quantities));

        try (Connection c = DriverManager.getConnection(System.getProperty("pcstore.db.url"));
             Statement s = c.createStatement();
             ResultSet stock = s.executeQuery("SELECT stock FROM products WHERE id='A'")) {
            assertTrue(stock.next());
            assertEquals(5, stock.getInt(1), "First deduction must be rolled back");
        }
        try (Connection c = DriverManager.getConnection(System.getProperty("pcstore.db.url"));
             Statement s = c.createStatement();
             ResultSet orders = s.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertTrue(orders.next());
            assertEquals(0, orders.getInt(1), "Failed checkout must not persist an order");
        }
    }
    @Test void commitsStockOrderAndComponentSnapshotTogether() throws Exception {
        Order order = new Order("ORDER-SUCCESS", LocalDateTime.now(),
                List.of(new Order.OrderLine("Test product", 1, new BigDecimal("100"),
                        new BigDecimal("100"), List.of(new Order.ComponentSnapshot(
                        "A", "Product A", "CPU", 1, new BigDecimal("100"), new BigDecimal("100"))))),
                new BigDecimal("100"), BigDecimal.ZERO, new BigDecimal("100"));

        Map<String, Integer> committedStock = new SqliteCheckoutRepository()
                .completeCheckout(order, Map.of("A", 1));

        assertEquals(4, committedStock.get("A"));
        try (Connection c = DriverManager.getConnection(System.getProperty("pcstore.db.url"));
             Statement s = c.createStatement();
             ResultSet stock = s.executeQuery("SELECT stock FROM products WHERE id='A'")) {
            assertTrue(stock.next());
            assertEquals(4, stock.getInt(1));
        }
        try (Connection c = DriverManager.getConnection(System.getProperty("pcstore.db.url"));
             Statement s = c.createStatement();
             ResultSet orders = s.executeQuery("SELECT COUNT(*) FROM orders")) {
            assertTrue(orders.next());
            assertEquals(1, orders.getInt(1));
        }
        try (Connection c = DriverManager.getConnection(System.getProperty("pcstore.db.url"));
             Statement s = c.createStatement();
             ResultSet snapshots = s.executeQuery("SELECT COUNT(*) FROM order_item_components")) {
            assertTrue(snapshots.next());
            assertEquals(1, snapshots.getInt(1));
        }
    }

}
