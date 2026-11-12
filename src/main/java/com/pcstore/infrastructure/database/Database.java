package com.pcstore.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static String url() { return System.getProperty("pcstore.db.url", "jdbc:sqlite:pcstore.db"); }

    private Database() { }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url());
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initialize() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS products(
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        price TEXT NOT NULL,
                        stock INTEGER NOT NULL,
                        spec1 TEXT,
                        spec2 TEXT,
                        spec3 TEXT
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS orders(
                        id TEXT PRIMARY KEY,
                        created_at TEXT NOT NULL,
                        subtotal TEXT NOT NULL,
                        discount TEXT NOT NULL,
                        total TEXT NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS order_items(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        order_id TEXT NOT NULL,
                        description TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        unit_price TEXT NOT NULL,
                        line_total TEXT NOT NULL,
                        FOREIGN KEY(order_id) REFERENCES orders(id)
                    )
                    """);
            // Migration-safe: existing orders/order_items remain untouched.
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS order_item_components(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        order_item_id INTEGER NOT NULL,
                        product_id TEXT NOT NULL,
                        product_name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        unit_price TEXT NOT NULL,
                        line_total TEXT NOT NULL,
                        FOREIGN KEY(order_item_id) REFERENCES order_items(id) ON DELETE CASCADE
                    )
                    """);
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_order_item_components_parent " +
                    "ON order_item_components(order_item_id)");
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot initialize database", exception);
        }
    }
}
