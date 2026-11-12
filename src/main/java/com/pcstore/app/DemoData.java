package com.pcstore.app;

import com.pcstore.domain.model.*;
import com.pcstore.patterns.factory.CpuCreator;
import com.pcstore.patterns.factory.GpuCreator;
import com.pcstore.patterns.factory.ProductCreator;
import com.pcstore.patterns.factory.RamCreator;
import com.pcstore.service.ProductService;

import java.math.BigDecimal;

public final class DemoData {

    private DemoData() {
    }

    public static void seed(ProductService productService) {
        if (!productService.findAll().isEmpty()) {
            return;
        }

        // Factory Method: concrete creators instantiate category-specific products.
        ProductCreator cpuCreator = new CpuCreator();
        productService.add(cpuCreator.createProduct(
                "CPU-001",
                "AMD Ryzen 7 7800X3D",
                new BigDecimal("185000"),
                8,
                "AM5"
        ));

        ProductCreator gpuCreator = new GpuCreator();
        productService.add(gpuCreator.createProduct(
                "GPU-001",
                "NVIDIA RTX 4070 Super",
                new BigDecimal("320000"),
                5,
                "220"
        ));

        ProductCreator ramCreator = new RamCreator();
        productService.add(ramCreator.createProduct(
                "RAM-001",
                "32GB DDR5 6000MHz",
                new BigDecimal("52000"),
                12,
                "DDR5",
                "32"
        ));

        // Other product types currently have no dedicated concrete creators.
        productService.add(new Motherboard(
                "MB-001", "B650 Gaming WiFi", new BigDecimal("95000"),
                6, "AM5", "DDR5", "ATX"
        ));
        productService.add(new Storage(
                "SSD-001", "1TB NVMe SSD", new BigDecimal("38000"),
                10, 1024, "NVMe"
        ));
        productService.add(new PowerSupply(
                "PSU-001", "750W 80+ Gold", new BigDecimal("65000"),
                7, 750
        ));
        productService.add(new PcCase(
                "CASE-001", "Mid Tower Airflow Case", new BigDecimal("42000"),
                4, "ATX"
        ));
        productService.add(new Cooler(
                "COOL-001", "Tower CPU Cooler", new BigDecimal("18000"),
                5, "AM5"
        ));
    }
}
