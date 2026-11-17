package net.modcrafters.minecraft.feastdominion.wellness;

/** One mark on the wellness bar. Low brings that group's penalty. High, with the others, is Well Fed. */
public enum FoodGroup {
    MEAT,
    DAIRY,
    EGGS,
    GRAIN,
    VEGETABLES,
    FRUIT;

    /**
     * Copies shares into the current groups.
     * A saved meal from when legumes were the seventh group adds that share to vegetables.
     */
    public static float[] copy(float[] incoming) {
        float[] copy = new float[values().length];
        if (incoming == null) {
            return copy;
        }
        int count = Math.min(copy.length, incoming.length);
        for (int i = 0; i < count; i++) {
            copy[i] = incoming[i] < 0.0F ? 0.0F : incoming[i];
        }
        if (incoming.length > copy.length && incoming[copy.length] > 0.0F) {
            copy[VEGETABLES.ordinal()] += incoming[copy.length];
        }
        return copy;
    }
}
