package net.modcrafters.minecraft.feastdominion.wellness;

import net.modcrafters.minecraft.feastdominion.group.GroupShares;

import java.util.ArrayList;
import java.util.List;

/** The group counts. A new life is half full, and a meal replaces that much of the recent diet. */
public final class DietLevels {
    /** Hunger in the recent diet. Older meals leave once newer ones fill this. */
    public static final int WINDOW = 80;
    /** A food with no hunger, such as a bucket of milk, counts as one meal. */
    public static final int SERVING = 4;

    private DietLevels() {
    }

    /** One meal. {@code shares} is how much of it sits in each group. Newest meals are kept first. */
    public record Meal(int hunger, float[] shares) {
        public Meal {
            float[] copy = FoodGroup.copy(shares);
            for (int i = 0; i < copy.length; i++) {
                if (copy[i] < 0.0F) {
                    copy[i] = 0.0F;
                }
            }
            shares = copy;
            if (hunger < 0) {
                hunger = 0;
            }
        }
    }

    public static int serving(int nutrition) {
        if (nutrition > 0) {
            return nutrition;
        }
        return SERVING;
    }

    public static int clamp(int points) {
        if (points < 0) {
            return 0;
        }
        return Math.min(points, Wellness.POINTS);
    }

    /** Half on every group. No meals have replaced the default yet. */
    public static int[] starting() {
        return points(List.of());
    }

    /** Puts the meal at the front and drops meals that have fallen out of the window. */
    public static List<Meal> remember(List<Meal> meals, GroupShares shares, int hunger) {
        if (hunger <= 0 || shares == null || !shares.any()) {
            return meals == null ? List.of() : meals;
        }
        float[] copy = new float[FoodGroup.values().length];
        for (FoodGroup group : FoodGroup.values()) {
            copy[group.ordinal()] = shares.get(group);
        }
        List<Meal> next = new ArrayList<>();
        next.add(new Meal(hunger, copy));
        if (meals != null) {
            next.addAll(meals);
        }
        int filled = 0;
        List<Meal> kept = new ArrayList<>();
        for (Meal meal : next) {
            if (filled >= WINDOW) {
                break;
            }
            kept.add(meal);
            filled += meal.hunger();
        }
        return List.copyOf(kept);
    }

    /** Each group is its share of the last {@link #WINDOW} hunger. The rest of the window stays at half. */
    public static int[] points(List<Meal> meals) {
        float[] sum = new float[FoodGroup.values().length];
        int filled = 0;
        if (meals != null) {
            for (Meal meal : meals) {
                if (filled >= WINDOW) {
                    break;
                }
                int take = Math.min(meal.hunger(), WINDOW - filled);
                for (int i = 0; i < sum.length; i++) {
                    sum[i] += meal.shares()[i] * take;
                }
                filled += take;
            }
        }
        int gap = WINDOW - filled;
        int[] points = new int[sum.length];
        for (int i = 0; i < points.length; i++) {
            double value = (sum[i] + 0.5 * gap) / WINDOW;
            points[i] = clamp((int) Math.round(value * Wellness.POINTS));
        }
        return points;
    }
}
