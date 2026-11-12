package com.pcstore.patterns;

import com.pcstore.domain.model.Cpu;
import com.pcstore.domain.model.Gpu;
import com.pcstore.domain.model.Product;
import com.pcstore.domain.model.Ram;
import com.pcstore.patterns.factory.CpuCreator;
import com.pcstore.patterns.factory.GpuCreator;
import com.pcstore.patterns.factory.RamCreator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FactoryMethodPatternTest {
    @Test
    void createsProductsOfTheExpectedTypes() {
        Product cpu = new CpuCreator().createProduct("CPU", "Test CPU", BigDecimal.TEN, 3, "AM5");
        Product gpu = new GpuCreator().createProduct("GPU", "Test GPU", BigDecimal.TEN, 3, "220");
        Product ram = new RamCreator().createProduct("RAM", "Test RAM", BigDecimal.TEN, 3, "DDR5", "32");

        assertInstanceOf(Cpu.class, cpu);
        assertInstanceOf(Gpu.class, gpu);
        assertInstanceOf(Ram.class, ram);
        assertEquals("AM5", ((Cpu) cpu).getSocket());
        assertEquals(220, ((Gpu) gpu).getWattage());
        assertEquals(32, ((Ram) ram).getCapacityGb());
    }

    @Test
    void rejectsMissingOrInvalidSpecifications() {
        assertThrows(IllegalArgumentException.class,
                () -> new CpuCreator().createProduct("CPU", "Test", BigDecimal.TEN, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new GpuCreator().createProduct("GPU", "Test", BigDecimal.TEN, 1, "not-a-number"));
        assertThrows(IllegalArgumentException.class,
                () -> new RamCreator().createProduct("RAM", "Test", BigDecimal.TEN, 1, "DDR5", "0"));
    }
}
