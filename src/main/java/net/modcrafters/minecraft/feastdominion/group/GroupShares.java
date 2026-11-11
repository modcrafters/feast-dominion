package net.modcrafters.minecraft.feastdominion.group;

import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;

/** How much of a food sits in each of the seven groups, from 0 to 1. */
public final class GroupShares {
    public static final GroupShares EMPTY = new GroupShares(new float[FoodGroup.values().length]);

    private final float[] values;

    private GroupShares(float[] values) {
        this.values = values;
    }

    public static GroupShares of(FoodGroup group, float amount) {
        float[] values = new float[FoodGroup.values().length];
        values[group.ordinal()] = amount;
        return new GroupShares(values);
    }

    public static GroupShares from(float[] values) {
        return new GroupShares(values.clone());
    }

    public float get(FoodGroup group) {
        return this.values[group.ordinal()];
    }

    /** Nearest whole percent. A trace under half a percent shows as 0. */
    public int percent(FoodGroup group) {
        return Math.round(this.get(group) * 100.0F);
    }

    public boolean any() {
        for (float value : this.values) {
            if (value > 0.0F) {
                return true;
            }
        }
        return false;
    }
}
