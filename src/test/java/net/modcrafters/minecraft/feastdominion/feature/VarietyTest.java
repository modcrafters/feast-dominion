package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.wellness.DietLevels;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VarietyTest {
    @Test
    void aNewLifeWithNothingEatenSitsAboveTheFloor() {
        assertEquals(11.25, hearts(FoodLog.EMPTY, 10, HealthSettings.DEFAULT), 0.001);
    }

    @Test
    void threeGroupsFillTheDay() {
        FoodLog log = FoodLog.EMPTY
                .remember(10, GroupShares.of(FoodGroup.FRUIT, 1.0F), 4)
                .remember(10, GroupShares.of(FoodGroup.MEAT, 1.0F), 4)
                .remember(10, GroupShares.of(FoodGroup.GRAIN, 1.0F), 4);
        Variety.Counts counts = Variety.counts(log, 10);
        assertEquals(1.0, counts.today(), 0.001);
        assertEquals(12.0 / 60.0, counts.week(), 0.001);
        double month = (double) Variety.MONTH_GROUPS * Variety.MONTH_HUNGER;
        assertEquals(12.0 / month, counts.month(), 0.001);
        assertEquals(12.6857, hearts(log, 10, HealthSettings.DEFAULT), 0.001);
    }

    @Test
    void extraBerriesDoNotFillAnotherGroup() {
        FoodLog log = FoodLog.EMPTY.remember(10, GroupShares.of(FoodGroup.FRUIT, 1.0F), 8);
        assertEquals(4.0 / 12.0, Variety.counts(log, 10).today(), 0.001);
    }

    @Test
    void aSandwichFillsEachGroupInIt() {
        float[] pie = new float[FoodGroup.values().length];
        pie[FoodGroup.GRAIN.ordinal()] = 0.5F;
        pie[FoodGroup.MEAT.ordinal()] = 0.5F;
        FoodLog log = FoodLog.EMPTY.remember(10, GroupShares.from(pie), 8);
        assertEquals(8.0 / 12.0, Variety.counts(log, 10).today(), 0.001);
    }

    @Test
    void weightsUseTheirOwnSum() {
        HealthSettings settings = new HealthSettings(7.5, 20, 50, 30, 30);
        FoodLog log = FoodLog.EMPTY
                .remember(10, GroupShares.of(FoodGroup.FRUIT, 1.0F), 4)
                .remember(10, GroupShares.of(FoodGroup.MEAT, 1.0F), 4)
                .remember(10, GroupShares.of(FoodGroup.GRAIN, 1.0F), 4);
        assertEquals(12.5714, hearts(log, 10, settings), 0.001);
    }

    @Test
    void fullWindowsWithTheGroupsAtHalfStayShort() {
        FoodLog log = FoodLog.EMPTY;
        for (FoodGroup group : FoodGroup.values()) {
            log = log.remember(10, GroupShares.of(group, 1.0F), Variety.MONTH_HUNGER);
        }
        Variety.Counts counts = Variety.counts(log, 10);
        assertEquals(1.0, counts.today(), 0.001);
        assertEquals(1.0, counts.week(), 0.001);
        assertEquals(1.0, counts.month(), 0.001);
        assertEquals(13.75, hearts(log, 10, HealthSettings.DEFAULT), 0.001);
    }

    @Test
    void everyPartFullIsTheCeiling() {
        FoodLog log = FoodLog.EMPTY;
        int[] groups = new int[FoodGroup.values().length];
        Arrays.fill(groups, 20);
        for (FoodGroup group : FoodGroup.values()) {
            log = log.remember(10, GroupShares.of(group, 1.0F), Variety.MONTH_HUNGER);
        }
        assertEquals(20, HealthSettings.DEFAULT.hearts(Variety.counts(log, 10), groups, 1), 0.001);
    }

    @Test
    void aWeekOldMealLeavesTheWeekAndStaysInTheMonth() {
        FoodLog log = FoodLog.EMPTY.remember(100, GroupShares.of(FoodGroup.MEAT, 1.0F), 4);
        Variety.Counts counts = Variety.counts(log, 107);
        assertEquals(0, counts.today(), 0.001);
        assertEquals(0, counts.week(), 0.001);
        assertEquals(4.0 / (Variety.MONTH_GROUPS * Variety.MONTH_HUNGER), counts.month(), 0.001);
        assertEquals(0, Variety.counts(log, 128).month(), 0.001);
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

    /** The food log alone leaves the group bars at the half they start on, and no taste has shown up. */
    private static double hearts(FoodLog log, long today, HealthSettings settings) {
        return settings.hearts(Variety.counts(log, today), DietLevels.starting(), 0);
    }
}
