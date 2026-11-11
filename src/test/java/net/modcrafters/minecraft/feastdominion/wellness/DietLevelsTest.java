package net.modcrafters.minecraft.feastdominion.wellness;

import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DietLevelsTest {
    @Test
    void aNewLifeStartsInTheMiddle() {
        int[] points = DietLevels.starting();
        for (int point : points) {
            assertEquals(10, point);
        }
        assertEquals(DietBand.MIDDLE, Wellness.band(10));
    }

    @Test
    void eatingAddsTheGroupsShareOfTheHunger() {
        int[] eaten = DietLevels.eat(DietLevels.starting(), GroupShares.of(FoodGroup.MEAT, 1.0F), 8);
        assertEquals(18, eaten[FoodGroup.MEAT.ordinal()]);
        assertEquals(10, eaten[FoodGroup.GRAIN.ordinal()]);

        float[] pie = new float[FoodGroup.values().length];
        pie[FoodGroup.VEGETABLES.ordinal()] = 0.5F;
        pie[FoodGroup.EGGS.ordinal()] = 0.5F;
        int[] mixed = DietLevels.eat(DietLevels.starting(), GroupShares.from(pie), 8);
        assertEquals(14, mixed[FoodGroup.VEGETABLES.ordinal()]);
        assertEquals(14, mixed[FoodGroup.EGGS.ordinal()]);
    }

    @Test
    void aGroupStopsAtFull() {
        int[] full = DietLevels.eat(DietLevels.starting(), GroupShares.of(FoodGroup.FRUIT, 1.0F), 20);
        assertEquals(20, full[FoodGroup.FRUIT.ordinal()]);
    }

    @Test
    void onePointFadesEveryThirtySeconds() {
        DietLevels.Step step = new DietLevels.Step(DietLevels.starting(), 0);
        for (int tick = 0; tick < 599; tick++) {
            step = DietLevels.tick(step.points(), step.drain());
        }
        assertEquals(10, step.points()[FoodGroup.MEAT.ordinal()]);
        step = DietLevels.tick(step.points(), step.drain());
        assertEquals(9, step.points()[FoodGroup.MEAT.ordinal()]);
        assertEquals(9, step.points()[FoodGroup.LEGUMES.ordinal()]);
        assertEquals(0, step.drain());
    }

    @Test
    void anEmptyDietDoesNotKeepDraining() {
        DietLevels.Step step = DietLevels.tick(new int[FoodGroup.values().length], 400);
        assertEquals(0, step.points()[FoodGroup.MEAT.ordinal()]);
        assertEquals(0, step.drain());
    }

    @Test
    void sixIsLowAndFourteenIsHigh() {
        assertEquals(DietBand.LOW, Wellness.band(6));
        assertEquals(DietBand.MIDDLE, Wellness.band(7));
        assertEquals(DietBand.MIDDLE, Wellness.band(13));
        assertEquals(DietBand.HIGH, Wellness.band(14));
    }
}
