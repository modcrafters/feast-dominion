package net.modcrafters.minecraft.feastdominion.thirst;

/**
 * A full bar lasts 12 in-game hours at the normal rate: 600 ticks per point.
 * Wet ground is a quarter slower. Dry ground is a quarter faster.
 */
public final class ThirstDrain {
    public static final int TICKS_PER_POINT = 600;
    private static final int UNIT = 4;
    static final int THRESHOLD = UNIT * TICKS_PER_POINT;

    private ThirstDrain() {
    }

    public enum Climate {
        WET,
        DRY,
        NORMAL
    }

    public record Step(int points, int drain) {
    }

    /** A biome on both lists is dry. */
    public static Climate climate(boolean wet, boolean dry) {
        if (dry) {
            return Climate.DRY;
        }
        if (wet) {
            return Climate.WET;
        }
        return Climate.NORMAL;
    }

    public static Step tick(int points, int drain, Climate climate) {
        if (points <= 0) {
            return new Step(0, 0);
        }
        int next = drain + rate(climate);
        int left = points;
        while (next >= THRESHOLD && left > 0) {
            next -= THRESHOLD;
            left--;
        }
        if (left == 0) {
            next = 0;
        }
        return new Step(left, next);
    }

    private static int rate(Climate climate) {
        return switch (climate) {
            case WET -> 3;
            case DRY -> 5;
            case NORMAL -> UNIT;
        };
    }
}
