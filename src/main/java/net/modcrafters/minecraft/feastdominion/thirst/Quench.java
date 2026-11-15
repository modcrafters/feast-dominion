package net.modcrafters.minecraft.feastdominion.thirst;

/** The reserve that spends before thirst. It may rise to twice the current thirst, the same room saturation has. */
public final class Quench {
    private Quench() {
    }

    public static float cap(int points) {
        return Math.max(points, 0) * 2.0F;
    }

    public static float clamp(int points, float quench) {
        if (quench < 0.0F) {
            return 0.0F;
        }
        return Math.min(quench, cap(points));
    }
}
