package net.modcrafters.minecraft.feastdominion.feature;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VarietyViewTest {
    @Test
    void theSnapshotKeepsTheFoods() {
        MealSlots.Windows meals = new MealSlots.Windows(List.of("minecraft:apple"), List.of(), List.of());
        VarietyView.Snapshot snapshot = VarietyView.Snapshot.of(
                new Variety.Counts(0, 0, 0), HealthSettings.DEFAULT, meals);
        assertEquals(List.of("minecraft:apple"), snapshot.todayItems());
        assertEquals(List.of(), snapshot.weekItems());
    }
}
