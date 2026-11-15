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

    public record Step(int points, float quench, int drain) {
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

    /** Quench spends first. A thirst point falls only when the reserve is empty. */
    public static Step tick(int points, float quench, int drain, Climate climate) {
        if (points <= 0 && quench <= 0.0F) {
            return new Step(0, 0.0F, 0);
        }
        int next = drain + rate(climate);
        int left = points;
        float reserve = quench;
        while (next >= THRESHOLD && (reserve > 0.0F || left > 0)) {
            next -= THRESHOLD;
            if (reserve > 0.0F) {
                reserve = Math.max(0.0F, reserve - 1.0F);
            } else {
                left--;
            }
        }
        if (left == 0 && reserve <= 0.0F) {
            next = 0;
        }
        return new Step(left, Quench.clamp(left, reserve), next);
    }

    private static int rate(Climate climate) {
        return switch (climate) {
            case WET -> 3;
            case DRY -> 5;
            case NORMAL -> UNIT;
        };
    }
}
