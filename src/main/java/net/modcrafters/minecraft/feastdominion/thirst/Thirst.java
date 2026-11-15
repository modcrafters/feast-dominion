package net.modcrafters.minecraft.feastdominion.thirst;

public final class Thirst {
    /** Ten droplets, the same count as the hunger drumsticks. A new life starts full. */
    public static final int POINTS = 20;

    private Thirst() {
    }

    /** Adds a food's thirst number and keeps the result on the bar. */
    public static int changed(int points, int delta) {
        int next = points + delta;
        if (next < 0) {
            return 0;
        }
        if (next > POINTS) {
            return POINTS;
        }
        return next;
    }
}
