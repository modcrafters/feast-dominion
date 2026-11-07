package net.modcrafters.minecraft.feastdominion.wellness;

import java.util.Arrays;

public final class Wellness {
    /** Ten marks, the same width as the hearts. */
    public static final int POINTS = 20;

    private Wellness() {
    }

    /** No meals are remembered yet, so every group sits in the middle. */
    public static DietBand band(FoodGroup group) {
        return DietBand.MIDDLE;
    }

    /** Low is empty, middle is half a health bar, high is full. The bar shows the median of the seven. */
    public static int progress(FoodGroup group) {
        return switch (band(group)) {
            case LOW -> 0;
            case MIDDLE -> POINTS / 2;
            case HIGH -> POINTS;
        };
    }

    public static int median() {
        int[] values = new int[FoodGroup.values().length];
        for (int i = 0; i < values.length; i++) {
            values[i] = progress(FoodGroup.values()[i]);
        }
        return medianOf(values);
    }

    static int medianOf(int... values) {
        int[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }
}
