package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardLinesTest {
    @Test
    void spiceOfLifeHidesTheMeals() {
        assertFalse(BoardLines.mealsColumn(false));
        assertTrue(BoardLines.mealsColumn(true));
    }

    @Test
    void aLowGroupNamesItsTrouble() {
        assertEquals("feast_dominion.wellbeing.trouble.meat", BoardLines.groupWord(FoodGroup.MEAT, 6));
        assertEquals("feast_dominion.wellbeing.band.high", BoardLines.groupWord(FoodGroup.MEAT, 14));
    }

    @Test
    void theMiddleNamesTheMiddleBand() {
        assertEquals("feast_dominion.wellbeing.band.middle", BoardLines.groupWord(FoodGroup.MEAT, 7));
        assertEquals("feast_dominion.wellbeing.band.middle", BoardLines.groupWord(FoodGroup.MEAT, 13));
        assertEquals("feast_dominion.wellbeing.trouble.eggs", BoardLines.groupWord(FoodGroup.EGGS, 0));
    }

    @Test
    void wellFedNeedsEveryGroupHigh() {
        int[] points = new int[FoodGroup.values().length];
        Arrays.fill(points, 14);
        assertTrue(BoardLines.wellFed(points));
        points[FoodGroup.EGGS.ordinal()] = 13;
        assertFalse(BoardLines.wellFed(points));
    }
}
