package com.pcstore.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Immutable order snapshot. Prices and component details represent checkout time. */
public record Order(
        String id,
        LocalDateTime createdAt,
        List<OrderLine> lines,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal total
) {
    public Order {
        lines = List.copyOf(lines);
    }

    /** A purchased top-level item, such as one complete Gaming PC. */
    public record OrderLine(
            String description,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal,
            List<ComponentSnapshot> components
    ) {
        public OrderLine {
            if (description == null || description.isBlank()) {
                throw new IllegalArgumentException("Order line description is required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Order line quantity must be positive");
            }
            components = List.copyOf(components);
        }

        public OrderLine(String description, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
            this(description, quantity, unitPrice, lineTotal, List.of());
        }
    }

    /** Historical copy of a physical component; deliberately has no FK to products. */
    public record ComponentSnapshot(
            String productId,
            String productName,
            String category,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
        public ComponentSnapshot {
            if (productId == null || productId.isBlank()
                    || productName == null || productName.isBlank()
                    || category == null || category.isBlank()) {
                throw new IllegalArgumentException("Component snapshot fields are required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Component quantity must be positive");
            }
        }
    }
}
