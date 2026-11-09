package net.modcrafters.minecraft.feastdominion;

/**
 * Peaceful normally holds thirst and fills hunger and saturation back up.
 * The config can spend them the same way other difficulties do.
 */
public final class PeacefulRules {
    private PeacefulRules() {
    }

    public static boolean thirstDrains(boolean drainInPeaceful, boolean peaceful) {
        return !peaceful || drainInPeaceful;
    }

    /** The peaceful fill runs only for that difficulty. Skipping it lets exhaustion spend the bars. */
    public static boolean fillHunger(boolean drainInPeaceful) {
        return !drainInPeaceful;
    }

    /** An empty stomach keeps its damage. Any food left still gets the peaceful heal. */
    public static boolean heal(boolean drainInPeaceful, int foodLevel) {
        return !drainInPeaceful || foodLevel > 0;
    }

    /**
     * Vanilla already took four exhaustion and left hunger where it was.
     * That skipped point comes off now.
     */
    public static boolean hungerDropped(
            boolean drainInPeaceful,
            boolean peaceful,
            float exhaustionBefore,
            float saturationBefore,
            int foodBefore,
            int foodAfter
    ) {
        return drainInPeaceful
                && peaceful
                && exhaustionBefore > 4.0F
                && saturationBefore <= 0.0F
                && foodAfter == foodBefore
                && foodBefore > 0;
    }
}
