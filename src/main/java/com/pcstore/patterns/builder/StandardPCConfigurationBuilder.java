package com.pcstore.patterns.builder;

import com.pcstore.domain.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class StandardPCConfigurationBuilder implements PCConfigurationBuilder {
    private String name;
    private final List<Product> parts = new ArrayList<>();

    @Override
    public PCConfigurationBuilder reset() {
        name = null;
        parts.clear();
        return this;
    }

    @Override
    public PCConfigurationBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public PCConfigurationBuilder add(Product product) {
        parts.add(Objects.requireNonNull(product, "Component cannot be null"));
        return this;
    }

    public PCConfigurationBuilder validate() {
        Set<ProductCategory> categories = new HashSet<>();
        for (Product product : parts) {
            if (!categories.add(product.getCategory())) {
                throw new IllegalStateException("Duplicate component category: " + product.getCategory());
            }
            if (product.getStock() < 1) {
                throw new IllegalStateException("Out of stock: " + product.getName());
            }
        }

        for (ProductCategory required : List.of(
                ProductCategory.CPU, ProductCategory.MOTHERBOARD, ProductCategory.RAM,
                ProductCategory.STORAGE, ProductCategory.POWER_SUPPLY, ProductCategory.CASE,
                ProductCategory.COOLER)) {
            if (!categories.contains(required)) {
                throw new IllegalStateException("Missing required component: " + required);
            }
        }

        Cpu cpu = find(Cpu.class);
        Motherboard motherboard = find(Motherboard.class);
        Ram ram = find(Ram.class);
        PowerSupply powerSupply = find(PowerSupply.class);
        PcCase pcCase = find(PcCase.class);
        Cooler cooler = find(Cooler.class);

        if (!cpu.getSocket().equals(motherboard.getSocket())) {
            throw new IllegalStateException("CPU and motherboard sockets do not match");
        }
        if (!ram.getType().equals(motherboard.getRamType())) {
            throw new IllegalStateException("RAM type is incompatible with motherboard");
        }

        Gpu gpu = findOptional(Gpu.class);
        if (gpu != null && gpu.getWattage() > powerSupply.getWattage()) {
            throw new IllegalStateException("Power supply wattage is insufficient for the GPU");
        }
        if (!cpu.getSocket().equalsIgnoreCase(cooler.getSocket())) {
            throw new IllegalStateException("CPU and cooler sockets do not match");
        }
        if (!pcCase.getFormFactor().equalsIgnoreCase(motherboard.getFormFactor())) {
            throw new IllegalStateException("Motherboard form factor is incompatible with the case");
        }
        return this;
    }

    @Override
    public PCConfiguration build() {
        validate();
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Configuration name is required");
        }
        return new PCConfiguration(name, parts);
    }

    private <T extends Product> T find(Class<T> type) {
        return parts.stream().filter(type::isInstance).map(type::cast).findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing component: " + type.getSimpleName()));
    }

    private <T extends Product> T findOptional(Class<T> type) {
        return parts.stream().filter(type::isInstance).map(type::cast).findFirst().orElse(null);
    }
}
