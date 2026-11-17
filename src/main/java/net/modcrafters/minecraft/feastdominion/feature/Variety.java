package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;

import java.util.Arrays;

/** Recent meals, and the hearts they are worth. A group counts its hunger, and extra hunger past the quota adds nothing. */
public final class Variety {
    /** Today and the six days before it. */
    public static final int WEEK_DAYS = 7;
    /** Today and the twenty-seven days before it. */
    public static final int MONTH_DAYS = 28;
    public static final int DAY_GROUPS = 3;
    public static final int DAY_HUNGER = 4;
    public static final int WEEK_GROUPS = 5;
    public static final int WEEK_HUNGER = 12;
    /** Every group. */
    public static final int MONTH_GROUPS = FoodGroup.values().length;
    public static final int MONTH_HUNGER = 28;

    private Variety() {
    }

    /** Each value is the filled fraction of that window, from 0 to 1. */
    public record Counts(double today, double week, double month) {
    }

    public static Counts counts(FoodLog log, long today) {
        return new Counts(
                fill(sum(log, today, 1), DAY_GROUPS, DAY_HUNGER),
                fill(sum(log, today, WEEK_DAYS), WEEK_GROUPS, WEEK_HUNGER),
                fill(sum(log, today, MONTH_DAYS), MONTH_GROUPS, MONTH_HUNGER)
        );
    }

    /** Hunger-bar marks for a window. Twenty is a full bar. */
    public static int marks(double fraction) {
        double clamped = Math.min(1.0, Math.max(0.0, fraction));
        return (int) Math.round(clamped * Wellness.POINTS);
    }

    /** Weights are whole numbers. A full set of windows is their sum, whatever that sum is. */
    public static double score(Counts counts, int dayWeight, int weekWeight, int monthWeight) {
        int sum = dayWeight + weekWeight + monthWeight;
        if (sum <= 0) {
            return 0;
        }
        double filled = dayWeight * counts.today() + weekWeight * counts.week() + monthWeight * counts.month();
        return filled / sum;
    }

    public static double hearts(double score, double floor, double ceiling) {
        return floor + score * (ceiling - floor);
    }

    public static String formatHearts(double hearts) {
        double shown = Math.round(hearts * 100.0) / 100.0;
        if (shown == Math.floor(shown)) {
            return Integer.toString((int) shown);
        }
        String text = Double.toString(shown);
        if (text.endsWith(".0")) {
            return text.substring(0, text.length() - 2);
        }
        return text;
    }

    private static float[] sum(FoodLog log, long today, int days) {
        float[] total = new float[FoodGroup.values().length];
        for (FoodLog.Day entry : log.days()) {
            long age = today - entry.day();
            if (age < 0 || age >= days) {
                continue;
            }
            for (int i = 0; i < total.length && i < entry.hunger().length; i++) {
                total[i] += entry.hunger()[i];
            }
        }
        return total;
    }

    /** The fullest groups, each capped, over the hunger those groups can hold. */
    private static double fill(float[] hunger, int groups, int cap) {
        if (groups <= 0 || cap <= 0) {
            return 0;
        }
        float[] capped = hunger.clone();
        for (int i = 0; i < capped.length; i++) {
            capped[i] = Math.min(Math.max(capped[i], 0.0F), cap);
        }
        Arrays.sort(capped);
        double sum = 0;
        for (int i = capped.length - 1; i >= 0 && i >= capped.length - groups; i--) {
            sum += capped[i];
        }
        return sum / (groups * (double) cap);
    }
}
