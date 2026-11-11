package net.modcrafters.minecraft.feastdominion.food;

/** Saturation may rise to twice the current hunger. */
public final class SaturationRoom {
    private SaturationRoom() {
    }

    public static float cap(int hunger) {
        return Math.max(hunger, 0) * 2.0F;
    }

    public static float clamp(int hunger, float saturation) {
        float cap = cap(hunger);
        if (saturation < 0.0F) {
            return 0.0F;
        }
        return Math.min(saturation, cap);
    }

    /** True while another bite still fits under {@link #cap}. */
    public static boolean room(int hunger, float saturation) {
        return saturation < cap(hunger);
    }

    /** A full hunger bar holds this much saturation, and eating stops there. */
    public static boolean full(float saturation) {
        return saturation >= cap(20);
    }
}
