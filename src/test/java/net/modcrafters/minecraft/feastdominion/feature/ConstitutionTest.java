package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.taste.TasteProfile;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstitutionTest {
    @Test
    void aTasteBelowTheLineDoesNotCount() {
        int mask = Constitution.mask(List.of(TasteProfile.of(Taste.SWEET, 0.19F)));
        assertEquals(0, mask);
        assertEquals(0.0, Constitution.span(mask), 0.001);
    }

    @Test
    void oneShownTasteIsOneSeventh() {
        int mask = Constitution.mask(List.of(TasteProfile.of(Taste.SWEET, TasteProfile.SHOWN)));
        assertEquals(1 << Taste.SWEET.ordinal(), mask);
        assertEquals(1.0 / 7.0, Constitution.span(mask), 0.001);
    }

    @Test
    void theSameTasteOnTwoFoodsCountsOnce() {
        int mask = Constitution.mask(List.of(
                TasteProfile.of(Taste.SALTY, 0.5F),
                TasteProfile.of(Taste.SALTY, 0.8F)
        ));
        assertEquals(1 << Taste.SALTY.ordinal(), mask);
    }

    @Test
    void twoTastesOnOneFoodBothCount() {
        float[] values = new float[Taste.values().length];
        values[Taste.SWEET.ordinal()] = 0.4F;
        values[Taste.SOUR.ordinal()] = 0.4F;
        int mask = Constitution.mask(List.of(TasteProfile.from(values)));
        assertEquals((1 << Taste.SWEET.ordinal()) | (1 << Taste.SOUR.ordinal()), mask);
        assertEquals(2.0 / 7.0, Constitution.span(mask), 0.001);
    }

    @Test
    void poisonDoesNotFillTheSpan() {
        int mask = Constitution.mask(List.of(TasteProfile.from(new float[Taste.values().length], 1.0F)));
        assertEquals(0, mask);
    }

    @Test
    void everyTasteFillsTheBar() {
        List<TasteProfile> foods = new ArrayList<>();
        int expected = 0;
        for (Taste taste : Taste.values()) {
            foods.add(TasteProfile.of(taste, 0.4F));
            expected |= 1 << taste.ordinal();
        }
        int mask = Constitution.mask(foods);
        assertEquals(expected, mask);
        assertEquals(1.0, Constitution.span(mask), 0.001);
        assertEquals(20, Constitution.marks(mask));
    }

    @Test
    void aFoodThisMonthCountsAndAMonthOldFoodDoesNot() {
        FoodLog recent = FoodLog.EMPTY.eat(101, "minecraft:apple");
        FoodLog gone = FoodLog.EMPTY.eat(100, "minecraft:apple");
        assertEquals(
                1 << Taste.SWEET.ordinal(),
                Constitution.mask(recent, 128, id -> TasteProfile.of(Taste.SWEET, 0.5F))
        );
        assertEquals(0, Constitution.mask(gone, 128, id -> TasteProfile.of(Taste.SWEET, 0.5F)));
    }

    @Test
    void halfTheGroupsAndNothingEatenAreThirtyPercent() {
        int[] groups = new int[FoodGroup.values().length];
        Arrays.fill(groups, 10);
        assertEquals(0.30, Constitution.score(groups, 0, 0), 0.001);
        assertEquals(11.25, Variety.hearts(Constitution.score(groups, 0, 0), 7.5, 20), 0.001);
    }

    @Test
    void anEmptyConstitutionIsTheFloor() {
        int[] groups = new int[FoodGroup.values().length];
        assertEquals(0.0, Constitution.score(groups, 0, 0), 0.001);
        assertEquals(7.5, Variety.hearts(Constitution.score(groups, 0, 0), 7.5, 20), 0.001);
    }

    @Test
    void tastesOutweighAnyOneGroupAndRecentFoodsMatchTastes() {
        int[] empty = new int[FoodGroup.values().length];
        assertEquals(0.20, Constitution.score(empty, 1, 0), 0.001);
        assertEquals(0.20, Constitution.score(empty, 0, 1), 0.001);
        int[] one = new int[FoodGroup.values().length];
        one[FoodGroup.MEAT.ordinal()] = 20;
        assertEquals(0.10, Constitution.score(one, 0, 0), 0.001);
    }

    @Test
    void fullGroupsTastesAndRecentFoodsAreTheCeiling() {
        int[] groups = new int[FoodGroup.values().length];
        Arrays.fill(groups, 20);
        assertEquals(1.0, Constitution.score(groups, 1, 1), 0.001);
        assertEquals(20.0, Variety.hearts(Constitution.score(groups, 1, 1), 7.5, 20), 0.001);
    }
}
