package net.modcrafters.minecraft.feastdominion.wellness;

import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DietLevelsTest {
    @Test
    void aNewLifeStartsAtHalf() {
        int[] points = DietLevels.starting();
        for (int point : points) {
            assertEquals(10, point);
        }
        assertEquals(DietBand.MIDDLE, Wellness.band(10));
    }

    @Test
    void aMealReplacesItsShareOfTheRecentDiet() {
        List<DietLevels.Meal> meals = DietLevels.remember(List.of(), GroupShares.of(FoodGroup.MEAT, 1.0F), 8);
        int[] points = DietLevels.points(meals);
        assertEquals(11, points[FoodGroup.MEAT.ordinal()]);
        assertEquals(9, points[FoodGroup.GRAIN.ordinal()]);
    }

    @Test
    void aSplitMealFillsBothGroups() {
        float[] pie = new float[FoodGroup.values().length];
        pie[FoodGroup.VEGETABLES.ordinal()] = 0.5F;
        pie[FoodGroup.EGGS.ordinal()] = 0.5F;
        int[] points = DietLevels.points(DietLevels.remember(List.of(), GroupShares.from(pie), 8));
        assertEquals(10, points[FoodGroup.VEGETABLES.ordinal()]);
        assertEquals(10, points[FoodGroup.EGGS.ordinal()]);
        assertEquals(9, points[FoodGroup.FRUIT.ordinal()]);
    }

    @Test
    void milkCountsAsAServing() {
        assertEquals(4, DietLevels.serving(0));
        assertEquals(8, DietLevels.serving(8));
        int[] points = DietLevels.points(DietLevels.remember(List.of(), GroupShares.of(FoodGroup.DAIRY, 1.0F), DietLevels.serving(0)));
        assertEquals(11, points[FoodGroup.DAIRY.ordinal()]);
        assertEquals(10, points[FoodGroup.MEAT.ordinal()]);
    }

    @Test
    void eightyHungerOfOneGroupFillsItAndEmptiesTheRest() {
        List<DietLevels.Meal> meals = List.of();
        for (int i = 0; i < 10; i++) {
            meals = DietLevels.remember(meals, GroupShares.of(FoodGroup.FRUIT, 1.0F), 8);
        }
        int[] points = DietLevels.points(meals);
        assertEquals(20, points[FoodGroup.FRUIT.ordinal()]);
        assertEquals(0, points[FoodGroup.MEAT.ordinal()]);
    }

    @Test
    void anOlderMealLeavesOnceTheWindowIsFull() {
        List<DietLevels.Meal> meals = List.of();
        for (int i = 0; i < 10; i++) {
            meals = DietLevels.remember(meals, GroupShares.of(FoodGroup.GRAIN, 1.0F), 8);
        }
        meals = DietLevels.remember(meals, GroupShares.of(FoodGroup.MEAT, 1.0F), 8);
        int[] points = DietLevels.points(meals);
        assertEquals(2, points[FoodGroup.MEAT.ordinal()]);
        assertEquals(18, points[FoodGroup.GRAIN.ordinal()]);
        assertEquals(10, meals.size());
    }

    @Test
    void sixIsLowAndFourteenIsHigh() {
        assertEquals(DietBand.LOW, Wellness.band(6));
        assertEquals(DietBand.MIDDLE, Wellness.band(7));
        assertEquals(DietBand.MIDDLE, Wellness.band(13));
        assertEquals(DietBand.HIGH, Wellness.band(14));
    }
}
