package net.modcrafters.minecraft.feastdominion.taste;

import net.modcrafters.minecraft.feastdominion.food.SaturationRoom;

/** One taste a player leans toward. A match adds a percent. A miss takes one away. */
public final class Lean {
    private Lean() {
    }

    public static Taste pick(int index) {
        Taste[] tastes = Taste.values();
        return tastes[Math.floorMod(index, tastes.length)];
    }

    /** A different taste. The index walks the six tastes that are not the current one. */
    public static Taste other(Taste current, int index) {
        Taste[] tastes = Taste.values();
        int slot = Math.floorMod(index, tastes.length - 1);
        int ordinal = slot >= current.ordinal() ? slot + 1 : slot;
        return tastes[ordinal];
    }

    /**
     * The lean after a respawn. Dying can roll a different taste.
     * Returning from the End keeps it, and so does a death when the config is off.
     */
    public static Taste carried(Taste current, boolean died, boolean resetOnDeath, int index) {
        if (current == null) {
            return null;
        }
        if (died && resetOnDeath) {
            return other(current, index);
        }
        return current;
    }

    /** Eating a reset food rolls a different taste. The meal itself still uses the old one. */
    public static Taste afterMeal(Taste current, boolean resets, int index) {
        if (current == null || !resets) {
            return current;
        }
        return other(current, index);
    }

    /** A match is a bonus, so sweet 0.20 is +20. A miss is the kept share below 100, so 75 is -25. */
    public static Effect effect(Taste lean, TasteProfile profile, int missPercent) {
        if (lean == null) {
            return null;
        }
        float strength = profile == null ? 0.0F : profile.get(lean);
        if (strength >= TasteProfile.SHOWN) {
            return new Effect(true, lean, Math.round(strength * 100.0F));
        }
        return new Effect(false, lean, missPercent - 100);
    }

    /** How much of the meal this bite is worth. +20 is 120. -25 is 75. */
    public static int yield(Effect effect) {
        return effect == null ? 100 : 100 + effect.percent();
    }

    public static String signed(int percent) {
        if (percent > 0) {
            return "+" + percent;
        }
        return Integer.toString(percent);
    }

    public record Effect(boolean matches, Taste taste, int percent) {
    }

    /** The leaned taste is missing, or too faint to count. A player with no lean yet is not punished. */
    public static boolean misses(Taste lean, TasteProfile profile) {
        if (lean == null) {
            return false;
        }
        if (profile == null) {
            return true;
        }
        return profile.get(lean) < TasteProfile.SHOWN;
    }

    /** Hunger kept from one bite. A half point or more rounds up. Percents over 100 add hunger. */
    public static int keptHunger(int nutrition, int percent) {
        if (nutrition <= 0 || percent <= 0) {
            return 0;
        }
        return Math.round(nutrition * (percent / 100.0F));
    }

    public static int hungerAfter(int before, int nutrition, int percent) {
        int sum = Math.max(0, before) + keptHunger(nutrition, percent);
        return Math.min(20, sum);
    }

    /** Saturation kept from one bite, capped at twice the hunger the bite leaves. */
    public static float saturationAfter(int hunger, float before, float foodSaturation, int percent) {
        float kept = percent <= 0 ? 0.0F : foodSaturation * (percent / 100.0F);
        float value = Math.max(0.0F, before) + Math.max(0.0F, kept);
        return SaturationRoom.clamp(hunger, value);
    }
}
