package net.modcrafters.minecraft.feastdominion.feature;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MealSlotsTest {
    @Test
    void theSameItemStaysOneSlotOnTheLaterDay() {
        FoodLog log = FoodLog.EMPTY.eat(1, "minecraft:apple").eat(2, "minecraft:apple");
        assertEquals(List.of(new FoodLog.Eaten("minecraft:apple", 2L)), log.eaten());
    }

    @Test
    void aFoodTwentyEightDaysOldIsDropped() {
        FoodLog log = FoodLog.EMPTY.eat(1, "minecraft:apple").eat(29, "minecraft:bread");
        assertEquals(List.of(new FoodLog.Eaten("minecraft:bread", 29L)), log.eaten());
        assertEquals(List.of("minecraft:bread"), MealSlots.of(log.eaten(), 29).month());
    }

    @Test
    void aFoodEatenTodayIsInEveryWindow() {
        MealSlots.Windows windows = MealSlots.of(List.of(new FoodLog.Eaten("minecraft:apple", 10)), 10);
        assertEquals(List.of("minecraft:apple"), windows.today());
        assertEquals(List.of("minecraft:apple"), windows.week());
        assertEquals(List.of("minecraft:apple"), windows.month());
    }

    @Test
    void sevenDaysAgoHasLeftTheWeek() {
        MealSlots.Windows windows = MealSlots.of(List.of(new FoodLog.Eaten("minecraft:apple", 3)), 10);
        assertEquals(List.of(), windows.week());
        assertEquals(List.of("minecraft:apple"), windows.month());
    }

    @Test
    void todayKeepsTheThreeNewest() {
        List<FoodLog.Eaten> eaten = List.of(
                new FoodLog.Eaten("minecraft:apple", 10),
                new FoodLog.Eaten("minecraft:beetroot", 10),
                new FoodLog.Eaten("minecraft:bread", 10),
                new FoodLog.Eaten("minecraft:carrot", 10));
        assertEquals(List.of("minecraft:apple", "minecraft:beetroot", "minecraft:bread"), MealSlots.of(eaten, 10).today());

        List<FoodLog.Eaten> acrossDays = List.of(
                new FoodLog.Eaten("minecraft:apple", 10),
                new FoodLog.Eaten("minecraft:bread", 10),
                new FoodLog.Eaten("minecraft:carrot", 9),
                new FoodLog.Eaten("minecraft:beetroot", 8));
        MealSlots.Windows windows = MealSlots.of(acrossDays, 10);
        assertEquals(List.of("minecraft:apple", "minecraft:bread"), windows.today());
        assertEquals(List.of("minecraft:apple", "minecraft:bread", "minecraft:carrot", "minecraft:beetroot"), windows.month());
    }

    @Test
    void theWeekKeepsTen() {
        List<FoodLog.Eaten> eaten = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            eaten.add(new FoodLog.Eaten("minecraft:food_" + i, 10));
        }
        assertEquals(10, MealSlots.of(eaten, 10).week().size());
    }
}
