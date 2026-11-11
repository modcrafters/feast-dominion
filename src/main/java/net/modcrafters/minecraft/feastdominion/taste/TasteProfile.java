package net.modcrafters.minecraft.feastdominion.taste;

import java.util.ArrayList;
import java.util.List;

/** How strong each taste is, from 0 to 1. */
public final class TasteProfile {
    /** A taste this weak is not named and does not count as a match. */
    public static final float SHOWN = 0.20F;

    public static final TasteProfile EMPTY = new TasteProfile(new float[Taste.values().length], 0.0F);

    private final float[] values;
    private final float poison;

    private TasteProfile(float[] values, float poison) {
        this.values = values;
        this.poison = poison;
    }

    public static TasteProfile of(Taste taste, float amount) {
        float[] values = new float[Taste.values().length];
        values[taste.ordinal()] = clamp(amount);
        return new TasteProfile(values, 0.0F);
    }

    public static TasteProfile from(float[] values) {
        return from(values, 0.0F);
    }

    public static TasteProfile from(float[] values, float poison) {
        float[] clamped = new float[Taste.values().length];
        for (int i = 0; i < clamped.length; i++) {
            clamped[i] = i < values.length ? clamp(values[i]) : 0.0F;
        }
        return new TasteProfile(clamped, clamp(poison));
    }

    public float get(Taste taste) {
        return this.values[taste.ordinal()];
    }

    public float poison() {
        return this.poison;
    }

    public boolean poisonous() {
        return this.poison > 0.0F;
    }

    public boolean any() {
        if (this.poisonous()) {
            return true;
        }
        for (float value : this.values) {
            if (value > 0.0F) {
                return true;
            }
        }
        return false;
    }

    public boolean shown(Taste taste) {
        return this.get(taste) >= SHOWN;
    }

    public List<Taste> shown() {
        List<Taste> tastes = new ArrayList<>();
        for (Taste taste : Taste.values()) {
            if (shown(taste)) {
                tastes.add(taste);
            }
        }
        return tastes;
    }

    /** Each taste covers what the ingredients have not already covered. */
    public static TasteProfile cover(List<TasteProfile> parts) {
        float[] values = new float[Taste.values().length];
        for (Taste taste : Taste.values()) {
            double open = 1.0;
            for (TasteProfile part : parts) {
                if (part == null) {
                    continue;
                }
                open *= 1.0 - clamp(part.get(taste));
            }
            values[taste.ordinal()] = (float) (1.0 - open);
        }
        double openPoison = 1.0;
        for (TasteProfile part : parts) {
            if (part == null) {
                continue;
            }
            openPoison *= 1.0 - clamp(part.poison);
        }
        return new TasteProfile(values, (float) (1.0 - openPoison));
    }

    /** Heat clears poison, adds savory, and deepens fat that was already there. */
    public TasteProfile cook() {
        float[] cooked = this.values.clone();
        cooked[Taste.SAVORY.ordinal()] = clamp(this.get(Taste.SAVORY) + 0.20F);
        if (this.get(Taste.FAT) >= SHOWN) {
            cooked[Taste.FAT.ordinal()] = clamp(this.get(Taste.FAT) + 0.20F);
        }
        return new TasteProfile(cooked, 0.0F);
    }

    private static float clamp(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        return Math.min(value, 1.0F);
    }
}
