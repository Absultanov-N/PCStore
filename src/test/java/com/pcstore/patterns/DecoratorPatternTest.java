package com.pcstore.patterns;

import com.pcstore.domain.model.*;
import com.pcstore.patterns.decorator.ExtendedWarrantyDecorator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DecoratorPatternTest {
    @Test void warrantyAddsFeeWithoutChangingPhysicalComponents() {
        Product cpu = new Cpu("CPU-T", "Test CPU", new BigDecimal("100"), 3, "AM5");
        PCConfiguration pc = new PCConfiguration("Test PC", List.of(cpu));
        var decorated = new ExtendedWarrantyDecorator(pc, new BigDecimal("20"));

        assertEquals(new BigDecimal("120"), decorated.getCurrentPrice());
        assertEquals(1, decorated.getComponents().size());
        assertSame(cpu, decorated.getComponents().get(0));
        assertEquals(3, cpu.getStock());
    }

    @Test void rejectsNullWarrantyFee() {
        Product cpu = new Cpu("CPU-T", "Test CPU", BigDecimal.TEN, 1, "AM5");
        PCConfiguration pc = new PCConfiguration("Test PC", List.of(cpu));
        assertThrows(NullPointerException.class, () -> new ExtendedWarrantyDecorator(pc, null));
    }

    @Test void rejectsNegativeWarrantyFee() {
        Product cpu = new Cpu("CPU-T", "Test CPU", BigDecimal.TEN, 1, "AM5");
        PCConfiguration pc = new PCConfiguration("Test PC", List.of(cpu));
        assertThrows(IllegalArgumentException.class,
                () -> new ExtendedWarrantyDecorator(pc, new BigDecimal("-1")));
    }
}
