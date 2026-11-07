package net.modcrafters.minecraft.feastdominion.wellness;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WellnessIconsTest {
    @Test
    void aNewLifeSitsInTheMiddleOfEveryGroup() {
        for (FoodGroup group : FoodGroup.values()) {
            assertEquals(DietBand.MIDDLE, Wellness.band(group));
            assertEquals(WellnessIcons.Fill.HALF, WellnessIcons.fill(Wellness.band(group)));
        }
        assertEquals(7, FoodGroup.values().length);
        assertEquals(10, Wellness.progress(FoodGroup.MEAT));
        assertEquals(10, Wellness.median());
    }

    @Test
    void lowIsEmptyAndHighIsFull() {
        assertEquals(WellnessIcons.Fill.EMPTY, WellnessIcons.fill(DietBand.LOW));
        assertEquals(WellnessIcons.Fill.FULL, WellnessIcons.fill(DietBand.HIGH));
    }

    @Test
    void theBarUsesTheMiddleOfTheSeven() {
        assertEquals(0, Wellness.medianOf(0, 0, 0, 0, 20, 20, 20));
        assertEquals(20, Wellness.medianOf(0, 0, 0, 20, 20, 20, 20));
        assertEquals(10, Wellness.medianOf(0, 0, 10, 10, 10, 20, 20));
    }
}
