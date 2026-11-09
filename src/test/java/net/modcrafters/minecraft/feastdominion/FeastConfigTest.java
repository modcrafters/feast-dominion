package net.modcrafters.minecraft.feastdominion;

import net.modcrafters.minecraft.feastdominion.feature.HealthSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeastConfigTest {
    @Test
    void missingKeySpendsTheBars() {
        assertTrue(FeastConfig.parse("# nothing here\n"));
    }

    @Test
    void falseIsVanillaPeaceful() {
        assertFalse(FeastConfig.parse("""
                # Peaceful difficulty.
                drain_in_peaceful=false
                """));
        assertTrue(FeastConfig.parse("drain_in_peaceful=TRUE"));
    }

    @Test
    void aFractionalWeightKeepsTheDefault() {
        HealthSettings settings = FeastConfig.healthSettings("""
                health_weight_day=50.5
                health_weight_week=-3
                health_floor_hearts=8
                """);
        assertEquals(50, settings.dayWeight());
        assertEquals(30, settings.weekWeight());
        assertEquals(20, settings.monthWeight());
        assertEquals(8, settings.floor(), 0.001);
        assertEquals(20, settings.ceiling(), 0.001);
    }
}
