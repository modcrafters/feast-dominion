package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.taste.TasteProfile;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;

import java.util.List;
import java.util.function.Function;

/**
 * Hearts from the food groups, the tastes of the month, and recent foods.
 * Each group is 10, tastes are 20, and recent foods are 20, out of 100.
 */
public final class Constitution {
    public static final int GROUP_WEIGHT = 10;
    public static final int TASTE_WEIGHT = 20;
    public static final int RECENT_WEIGHT = 20;

    private Constitution() {
    }

    /** One bit per taste, in {@link Taste} order. Poison is never set. */
    public static int mask(List<TasteProfile> profiles) {
        int mask = 0;
        if (profiles == null) {
            return 0;
        }
        for (TasteProfile profile : profiles) {
            mask |= bits(profile);
        }
        return mask;
    }

    /** Tastes on foods eaten this month. A food 28 days old has already left. */
    public static int mask(FoodLog log, long today, Function<String, TasteProfile> tastes) {
        int mask = 0;
        if (log == null || tastes == null) {
            return 0;
        }
        for (FoodLog.Eaten eaten : log.eaten()) {
            long age = today - eaten.day();
            if (age < 0 || age >= Variety.MONTH_DAYS) {
                continue;
            }
            mask |= bits(tastes.apply(eaten.item()));
        }
        return mask;
    }

    /** How much of the seven tastes the mask holds, from 0 to 1. */
    public static double span(int mask) {
        int count = 0;
        for (Taste taste : Taste.values()) {
            if ((mask & (1 << taste.ordinal())) != 0) {
                count++;
            }
        }
        return count / (double) Taste.values().length;
    }

    /** Hunger-bar marks for a taste mask. Twenty is every taste. */
    public static int marks(int mask) {
        return (int) Math.round(span(mask) * Wellness.POINTS);
    }

    /** That weight as a percent of the whole constitution. Six groups make each one 10. */
    public static int percent(int weight) {
        int total = FoodGroup.values().length * GROUP_WEIGHT + TASTE_WEIGHT + RECENT_WEIGHT;
        if (total <= 0) {
            return 0;
        }
        return weight * 100 / total;
    }

    /**
     * From 0 to 1. Group points run from 0 to 20. Taste span and the recent-food score run from 0 to 1.
     * A missing group counts as empty.
     */
    public static double score(int[] groupPoints, double tasteSpan, double recent) {
        int total = FoodGroup.values().length * GROUP_WEIGHT + TASTE_WEIGHT + RECENT_WEIGHT;
        double filled = 0;
        for (int i = 0; i < FoodGroup.values().length; i++) {
            double points = groupPoints != null && i < groupPoints.length ? groupPoints[i] : 0;
            filled += clamp(points / Wellness.POINTS) * GROUP_WEIGHT;
        }
        filled += clamp(tasteSpan) * TASTE_WEIGHT;
        filled += clamp(recent) * RECENT_WEIGHT;
        return filled / total;
    }

    private static int bits(TasteProfile profile) {
        if (profile == null) {
            return 0;
        }
        int mask = 0;
        for (Taste taste : Taste.values()) {
            if (profile.shown(taste)) {
                mask |= 1 << taste.ordinal();
            }
        }
        return mask;
    }

    private static double clamp(double value) {
        if (value < 0) {
            return 0;
        }
        return Math.min(value, 1);
    }
}
