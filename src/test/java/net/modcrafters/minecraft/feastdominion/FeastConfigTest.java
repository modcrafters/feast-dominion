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
    void deathKeepsTheLeanUnlessTheConfigSaysSo() {
        assertFalse(FeastConfig.resetLeanOnDeath("# nothing here\n"));
        assertFalse(FeastConfig.resetLeanOnDeath("reset_lean_on_death=false"));
        assertTrue(FeastConfig.resetLeanOnDeath("reset_lean_on_death=TRUE"));
    }

    @Test
    void aMissKeepsThreeQuartersUnlessToldOtherwise() {
        assertEquals(75, FeastConfig.leanMissPercent("# nothing here\n"));
        assertEquals(75, FeastConfig.leanMissPercent("lean_miss_percent=75"));
        assertEquals(100, FeastConfig.leanMissPercent("lean_miss_percent=100"));
        assertEquals(0, FeastConfig.leanMissPercent("lean_miss_percent=0"));
        assertEquals(75, FeastConfig.leanMissPercent("lean_miss_percent=75.5"));
        assertEquals(75, FeastConfig.leanMissPercent("lean_miss_percent=101"));
    }

    @Test
    void debugCommandsStayOffUnlessAsked() {
        assertFalse(FeastConfig.debugCommands("# nothing here\n"));
        assertFalse(FeastConfig.debugCommands("add_debug_commands=false"));
        assertTrue(FeastConfig.debugCommands("add_debug_commands=TRUE"));
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
