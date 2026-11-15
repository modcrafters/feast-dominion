package net.modcrafters.minecraft.feastdominion.taste;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeanTest {
    @Test
    void aRollIsOneOfTheSevenTastes() {
        assertEquals(Taste.SWEET, Lean.pick(0));
        assertEquals(Taste.SPICY, Lean.pick(6));
        assertEquals(Taste.SWEET, Lean.pick(7));
        assertEquals(7, Taste.values().length);
    }

    @Test
    void aNewRollSkipsTheCurrentLean() {
        for (Taste current : Taste.values()) {
            Set<Taste> rolled = new HashSet<>();
            for (int index = 0; index < Taste.values().length - 1; index++) {
                Taste next = Lean.other(current, index);
                assertNotEquals(current, next);
                assertTrue(rolled.add(next));
            }
        }
    }

    @Test
    void aMissKeepsPartOfTheMeal() {
        TasteProfile savory = TasteProfile.of(Taste.SAVORY, 0.70F);
        assertTrue(Lean.misses(Taste.SWEET, savory));
        assertTrue(Lean.misses(Taste.SAVORY, TasteProfile.of(Taste.SAVORY, 0.19F)));
        assertTrue(Lean.misses(Taste.SWEET, null));
        assertEquals(false, Lean.misses(Taste.SAVORY, savory));
        assertEquals(false, Lean.misses(null, savory));
        assertEquals(6, Lean.keptHunger(8, 75));
        assertEquals(4, Lean.keptHunger(5, 75));
        assertEquals(1, Lean.keptHunger(1, 75));
        assertEquals(0, Lean.keptHunger(8, 0));
        assertEquals(8, Lean.keptHunger(8, 100));
        assertEquals(20, Lean.hungerAfter(18, 8, 75));
        assertEquals(13.6F, Lean.saturationAfter(16, 4.0F, 12.8F, 75), 0.001F);
        assertEquals(4.0F, Lean.saturationAfter(10, 4.0F, 12.8F, 0), 0.001F);
    }

    @Test
    void aTooltipNamesTheLeanAndTheEffect() {
        TasteProfile sweet = TasteProfile.of(Taste.SWEET, 0.20F);
        Lean.Effect match = Lean.effect(Taste.SWEET, sweet, 75);
        assertEquals(true, match.matches());
        assertEquals(Taste.SWEET, match.taste());
        assertEquals(20, match.percent());
        assertEquals("+20", Lean.signed(match.percent()));
        assertEquals(120, Lean.yield(match));
        Lean.Effect miss = Lean.effect(Taste.SAVORY, sweet, 75);
        assertEquals(false, miss.matches());
        assertEquals(-25, miss.percent());
        assertEquals("-25", Lean.signed(miss.percent()));
        assertEquals(75, Lean.yield(miss));
        assertEquals(-60, Lean.effect(Taste.SWEET, TasteProfile.of(Taste.SWEET, 0.19F), 40).percent());
        assertNull(Lean.effect(null, sweet, 75));
        assertEquals(100, Lean.yield(null));
    }

    @Test
    void aMatchScalesTheMealByTheTaste() {
        assertEquals(10, Lean.keptHunger(8, 120));
        assertEquals(19.36F, Lean.saturationAfter(16, 4.0F, 12.8F, 120), 0.001F);
    }

    @Test
    void deathKeepsTheLeanUnlessAsked() {
        assertEquals(Taste.SWEET, Lean.carried(Taste.SWEET, false, true, 0));
        assertEquals(Taste.SWEET, Lean.carried(Taste.SWEET, true, false, 0));
        assertEquals(Taste.SALTY, Lean.carried(Taste.SWEET, true, true, 0));
        assertNull(Lean.carried(null, true, true, 0));
    }

    @Test
    void aResetFoodRollsADifferentLean() {
        assertEquals(Taste.SWEET, Lean.afterMeal(Taste.SWEET, false, 0));
        assertEquals(Taste.SALTY, Lean.afterMeal(Taste.SWEET, true, 0));
        assertNull(Lean.afterMeal(null, true, 0));
    }
}
