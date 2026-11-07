package net.modcrafters.minecraft.feastdominion.thirst;

public final class ThirstIcons {
    public enum Fill {
        EMPTY,
        HALF,
        FULL
    }

    private ThirstIcons() {
    }

    /** {@code fromRight} 0 is the rightmost droplet, matching the hunger icons. */
    public static Fill fill(int points, int fromRight) {
        int mark = fromRight * 2 + 1;
        if (mark < points) {
            return Fill.FULL;
        }
        if (mark == points) {
            return Fill.HALF;
        }
        return Fill.EMPTY;
    }
}
