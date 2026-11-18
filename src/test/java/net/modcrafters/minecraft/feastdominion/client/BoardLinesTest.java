package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardLinesTest {
    @Test
    void spiceOfLifeHidesTheMeals() {
        assertFalse(BoardLines.mealsColumn(false));
        assertTrue(BoardLines.mealsColumn(true));
    }

    @Test
    void aLowGroupNamesItsTrouble() {
        assertEquals("feast_dominion.wellbeing.trouble.meat", BoardLines.effect(FoodGroup.MEAT, 6, false));
        assertNull(BoardLines.effect(FoodGroup.MEAT, 14, false));
        assertEquals("feast_dominion.wellbeing.well_fed.effect", BoardLines.effect(FoodGroup.MEAT, 14, true));
    }

    @Test
    void theMiddleSaysNothing() {
        assertNull(BoardLines.effect(FoodGroup.MEAT, 7, false));
        assertNull(BoardLines.effect(FoodGroup.MEAT, 13, false));
        assertEquals("feast_dominion.wellbeing.trouble.eggs", BoardLines.effect(FoodGroup.EGGS, 0, false));
        assertTrue(BoardLines.marked(0));
        assertTrue(BoardLines.marked(14));
        assertFalse(BoardLines.marked(10));
        assertFalse(BoardLines.tasteFull(17));
        assertTrue(BoardLines.tasteFull(20));
    }

    @Test
    void everyGroupRowHasATooltip() {
        BoardLines.RowTip middle = BoardLines.groupTip(FoodGroup.MEAT, 10, false);
        assertEquals("feast_dominion.wellbeing.group.quiet", middle.key());
        assertEquals("feast_dominion.wellbeing.trouble.meat", middle.argument());
        BoardLines.RowTip low = BoardLines.groupTip(FoodGroup.DAIRY, 6, false);
        assertEquals("feast_dominion.wellbeing.trouble.dairy", low.key());
        assertNull(low.argument());
        assertEquals("feast_dominion.wellbeing.group.full", BoardLines.groupTip(FoodGroup.FRUIT, 14, false).key());
        assertEquals("feast_dominion.wellbeing.well_fed.effect", BoardLines.groupTip(FoodGroup.FRUIT, 14, true).key());
    }

    @Test
    void thirstNamesEmptyAndHeld() {
        assertEquals("feast_dominion.wellbeing.thirst.empty", BoardLines.thirstTip(0).key());
        assertEquals("feast_dominion.wellbeing.thirst.held", BoardLines.thirstTip(1).key());
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
