package net.modcrafters.minecraft.feastdominion.wellness;

import net.modcrafters.minecraft.feastdominion.group.GroupShares;

import java.util.Arrays;

/** The seven group counts. A new life starts at half, eating adds, and time takes one point back. */
public final class DietLevels {
    /** One point fades after 30 seconds, the same pace as one thirst droplet. */
    public static final int TICKS_PER_POINT = 600;

    private DietLevels() {
    }

    public record Step(int[] points, int drain) {
    }

    public static int[] starting() {
        int[] points = new int[FoodGroup.values().length];
        Arrays.fill(points, Wellness.POINTS / 2);
        return points;
    }

    public static int clamp(int points) {
        if (points < 0) {
            return 0;
        }
        return Math.min(points, Wellness.POINTS);
    }

    /** Adds each group's share of the meal's weight, and stops at a full bar. */
    public static int[] eat(int[] points, GroupShares shares, int weight) {
        int[] next = points.clone();
        if (weight <= 0 || shares == null) {
            return next;
        }
        for (FoodGroup group : FoodGroup.values()) {
            int gain = Math.round(shares.get(group) * weight);
            next[group.ordinal()] = clamp(next[group.ordinal()] + gain);
        }
        return next;
    }

    /** Counts ticks until every group that still has a point loses one. An empty diet holds still. */
    public static Step tick(int[] points, int drain) {
        boolean any = false;
        for (int point : points) {
            if (point > 0) {
                any = true;
                break;
            }
        }
        if (!any) {
            return new Step(points.clone(), 0);
        }
        if (drain + 1 < TICKS_PER_POINT) {
            return new Step(points.clone(), drain + 1);
        }
        int[] next = points.clone();
        for (int i = 0; i < next.length; i++) {
            if (next[i] > 0) {
                next[i]--;
            }
        }
        return new Step(next, 0);
    }
}
