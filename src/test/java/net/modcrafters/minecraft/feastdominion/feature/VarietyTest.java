package net.modcrafters.minecraft.feastdominion.feature;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VarietyTest {
    @Test
    void nothingRememberedIsTheFloor() {
        assertEquals(7.5, hearts(FoodLog.EMPTY, 10, HealthSettings.DEFAULT), 0.001);
    }

    @Test
    void oneFullDayIsAboutFifteenHearts() {
        FoodLog log = new FoodLog(java.util.Map.of()).remember("a", 10).remember("b", 10).remember("c", 10);
        assertEquals(15.25, hearts(log, 10, HealthSettings.DEFAULT), 0.001);
    }

    @Test
    void weightsUseTheirOwnSum() {
        HealthSettings settings = new HealthSettings(7.5, 20, 50, 30, 30);
        FoodLog log = new FoodLog(java.util.Map.of()).remember("a", 10).remember("b", 10).remember("c", 10);
        double score = (50 + 30 * 0.3 + 30 * 0.15) / 110.0;
        assertEquals(7.5 + score * 12.5, hearts(log, 10, settings), 0.001);
    }

    @Test
    void everyWindowFullIsTheCeiling() {
        FoodLog log = FoodLog.EMPTY;
        for (int i = 0; i < 20; i++) {
            log = log.remember("food" + i, 10);
        }
        Variety.Counts counts = Variety.counts(log, 10);
        assertTrue(counts.today() >= Variety.DAY_CAP);
        assertTrue(counts.week() >= Variety.WEEK_CAP);
        assertTrue(counts.month() >= Variety.MONTH_CAP);
        assertEquals(20, hearts(log, 10, HealthSettings.DEFAULT), 0.001);
    }

    @Test
    void aWeekOldMealLeavesTheWeekAndStaysInTheMonth() {
        FoodLog log = new FoodLog(java.util.Map.of()).remember("stew", 100);
        Variety.Counts counts = Variety.counts(log, 107);
        assertEquals(0, counts.today());
        assertEquals(0, counts.week());
        assertEquals(1, counts.month());
        assertEquals(0, Variety.counts(log, 128).month());
    }

    @Test
    void spiceOfLifeTurnsTheFeatureOff() {
        HealthIncreaseFeature present = new HealthIncreaseFeature(() -> true);
        HealthIncreaseFeature absent = new HealthIncreaseFeature(() -> false);
        assertFalse(present.enabled());
        assertTrue(absent.enabled());
        present.init();
        absent.init();
    }

    private static double hearts(FoodLog log, long today, HealthSettings settings) {
        return settings.hearts(Variety.counts(log, today));
    }
}
