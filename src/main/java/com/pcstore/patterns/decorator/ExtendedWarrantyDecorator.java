package com.pcstore.patterns.decorator;

import com.pcstore.domain.model.Purchasable;
import java.math.BigDecimal;
import java.util.Objects;

public final class ExtendedWarrantyDecorator extends PurchasableDecorator {
    private final BigDecimal fee;

    public ExtendedWarrantyDecorator(Purchasable purchasable, BigDecimal fee) {
        super(purchasable);
        this.fee = Objects.requireNonNull(fee, "Warranty fee cannot be null");
        if (fee.signum() < 0) {
            throw new IllegalArgumentException("Warranty fee cannot be negative");
        }
    }

    @Override
    public String getDisplayName() {
        return wrapped.getDisplayName() + " + Extended warranty";
    }

    @Override
    public BigDecimal getCurrentPrice() {
        return wrapped.getCurrentPrice().add(fee);
    }
}
