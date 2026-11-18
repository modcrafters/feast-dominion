package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.wellness.DietLevels;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VarietyViewTest {
    @Test
    void theSnapshotKeepsTheFoods() {
        MealSlots.Windows meals = new MealSlots.Windows(List.of("minecraft:apple"), List.of(), List.of());
        VarietyView.Snapshot snapshot = VarietyView.Snapshot.of(
                new Variety.Counts(0, 0, 0), HealthSettings.DEFAULT, meals, DietLevels.starting(), 0);
        assertEquals(List.of("minecraft:apple"), snapshot.todayItems());
        assertEquals(List.of(), snapshot.weekItems());
        assertEquals(11.25, snapshot.hearts(), 0.001);
        assertEquals(0, snapshot.tasteMarks());
    }

    @Test
    void aFullTasteMaskFillsTheTasteBar() {
        int mask = 0;
        for (Taste taste : Taste.values()) {
            mask |= 1 << taste.ordinal();
        }
        MealSlots.Windows meals = new MealSlots.Windows(List.of(), List.of(), List.of());
        VarietyView.Snapshot snapshot = VarietyView.Snapshot.of(
                new Variety.Counts(0, 0, 0), HealthSettings.DEFAULT, meals, DietLevels.starting(), mask);
        assertEquals(20, snapshot.tasteMarks());
        assertEquals(mask, snapshot.tasteMask());
        assertEquals(13.75, snapshot.hearts(), 0.001);
    }
}
