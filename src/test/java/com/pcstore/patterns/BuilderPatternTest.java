package com.pcstore.patterns;

import com.pcstore.domain.model.*;
import com.pcstore.patterns.builder.PCConfigurationBuilder;
import com.pcstore.patterns.builder.StandardPCConfigurationBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BuilderPatternTest {
    private PCConfigurationBuilder validBuilder() {
        return new StandardPCConfigurationBuilder().reset().name("Test PC")
                .add(new Cpu("CPU-T", "Test CPU", new BigDecimal("100"), 2, "AM5"))
                .add(new Motherboard("MB-T", "Test board", new BigDecimal("100"), 2, "AM5", "DDR5", "ATX"))
                .add(new Ram("RAM-T", "Test RAM", new BigDecimal("50"), 2, "DDR5", 16))
                .add(new Storage("SSD-T", "Test SSD", new BigDecimal("25"), 2, 512, "NVMe"))
                .add(new PowerSupply("PSU-T", "Test PSU", new BigDecimal("50"), 2, 650))
                .add(new PcCase("CASE-T", "Test case", new BigDecimal("25"), 2, "ATX"))
                .add(new Cooler("COOL-T", "Test cooler", new BigDecimal("20"), 2, "AM5"));
    }

    @Test
    void buildsCompatibleConfigurationAndIncludesCooler() {
        var config = validBuilder().build();
        assertEquals("Test PC", config.getDisplayName());
        assertEquals(7, config.getComponents().size());
        assertEquals(new BigDecimal("370"), config.getCurrentPrice());
    }

    @Test
    void rejectsIncompatibleCpuAndMotherboardDuringBuild() {
        var builder = validBuilder();
        builder.reset().name("Bad PC")
                .add(new Cpu("CPU-T", "Test CPU", BigDecimal.TEN, 2, "AM5"))
                .add(new Motherboard("MB-T", "Test board", BigDecimal.TEN, 2, "LGA1700", "DDR5", "ATX"))
                .add(new Ram("RAM-T", "Test RAM", BigDecimal.TEN, 2, "DDR5", 16))
                .add(new Storage("SSD-T", "Test SSD", BigDecimal.TEN, 2, 512, "NVMe"))
                .add(new PowerSupply("PSU-T", "Test PSU", BigDecimal.TEN, 2, 650))
                .add(new PcCase("CASE-T", "Test case", BigDecimal.TEN, 2, "ATX"))
                .add(new Cooler("COOL-T", "Test cooler", BigDecimal.TEN, 2, "AM5"));
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void rejectsOutOfStockComponentDuringBuild() {
        var builder = new StandardPCConfigurationBuilder().reset().name("Out of stock PC")
                .add(new Cpu("CPU-T", "Test CPU", BigDecimal.TEN, 0, "AM5"))
                .add(new Motherboard("MB-T", "Test board", BigDecimal.TEN, 2, "AM5", "DDR5", "ATX"))
                .add(new Ram("RAM-T", "Test RAM", BigDecimal.TEN, 2, "DDR5", 16))
                .add(new Storage("SSD-T", "Test SSD", BigDecimal.TEN, 2, 512, "NVMe"))
                .add(new PowerSupply("PSU-T", "Test PSU", BigDecimal.TEN, 2, 650))
                .add(new PcCase("CASE-T", "Test case", BigDecimal.TEN, 2, "ATX"))
                .add(new Cooler("COOL-T", "Test cooler", BigDecimal.TEN, 2, "AM5"));
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void rejectsIncompatibleCoolerSocket() {
        var builder = validBuilder();
        builder.reset().name("Bad cooler PC")
                .add(new Cpu("CPU-T", "Test CPU", BigDecimal.TEN, 2, "AM5"))
                .add(new Motherboard("MB-T", "Test board", BigDecimal.TEN, 2, "AM5", "DDR5", "ATX"))
                .add(new Ram("RAM-T", "Test RAM", BigDecimal.TEN, 2, "DDR5", 16))
                .add(new Storage("SSD-T", "Test SSD", BigDecimal.TEN, 2, 512, "NVMe"))
                .add(new PowerSupply("PSU-T", "Test PSU", BigDecimal.TEN, 2, 650))
                .add(new PcCase("CASE-T", "Test case", BigDecimal.TEN, 2, "ATX"))
                .add(new Cooler("COOL-T", "Wrong cooler", BigDecimal.TEN, 2, "LGA1700"));
        assertThrows(IllegalStateException.class, builder::build);
    }
}
