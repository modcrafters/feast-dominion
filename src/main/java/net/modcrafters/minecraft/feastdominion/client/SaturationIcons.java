package net.modcrafters.minecraft.feastdominion.client;

/**
 * How full the saturation mark on one hunger shank is.
 * The steps match AppleSkin: two points fill a shank, and smaller amounts still show.
 */
public final class SaturationIcons {
    public enum Mark {
        NONE,
        TRACE,
        QUARTER,
        HALF,
        FULL
    }

    private SaturationIcons() {
    }

    /** {@code fromRight} 0 is the rightmost shank, matching hunger. */
    public static Mark mark(float saturation, int fromRight) {
        if (saturation <= 0.0F || fromRight < 0) {
            return Mark.NONE;
        }
        float effective = Math.min(saturation, 20.0F) / 2.0F - fromRight;
        if (effective >= 1.0F) {
            return Mark.FULL;
        }
        if (effective > 0.5F) {
            return Mark.HALF;
        }
        if (effective > 0.25F) {
            return Mark.QUARTER;
        }
        if (effective > 0.0F) {
            return Mark.TRACE;
        }
        return Mark.NONE;
    }

    /** Points past one full bar. The first bar stays in {@link #mark}. */
    public static Mark extra(float saturation, int index) {
        if (saturation <= 20.0F) {
            return Mark.NONE;
        }
        return mark(saturation - 20.0F, index);
    }

    /** How many marks this saturation paints. Ten is the most the hunger bar holds. */
    public static int count(float saturation) {
        int marks = 0;
        while (marks < 10 && mark(saturation, marks) != Mark.NONE) {
            marks++;
        }
        return marks;
    }
}
