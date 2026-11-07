package net.modcrafters.minecraft.feastdominion.thirst;

/** How one drink changes thirst, and how long the hand stays at the water. */
public final class Drink {
    /** Three droplets. Fresh water adds one drink. Salt water takes one away. */
    public static final int AMOUNT = 6;
    /** Two seconds. Releasing early does not drink. */
    public static final int HOLD = 40;

    private Drink() {
    }

    public enum Decision {
        PASS,
        WAITING,
        DRINK
    }

    public static int after(int points, boolean salty) {
        int next = points + (salty ? -AMOUNT : AMOUNT);
        if (next < 0) {
            return 0;
        }
        if (next > Thirst.POINTS) {
            return Thirst.POINTS;
        }
        return next;
    }

    /** 0 at the start of the hold, 1 when the two seconds are up. */
    public static float held(float partialTick, int remainingTicks) {
        float left = remainingTicks - partialTick;
        if (left < 0.0F) {
            left = 0.0F;
        }
        float done = (HOLD - left) / HOLD;
        if (done < 0.0F) {
            return 0.0F;
        }
        if (done > 1.0F) {
            return 1.0F;
        }
        return done;
    }

    /**
     * @param thirstApplies survival and adventure. Creative and spectator pass through.
     * @param alreadyDrinking the hand is already at the water, so this click must not start another sip.
     */
    public static Decision decide(boolean emptyHand, boolean waterBeside, boolean thirstApplies, boolean alreadyDrinking) {
        if (!emptyHand || !waterBeside || !thirstApplies) {
            return Decision.PASS;
        }
        if (alreadyDrinking) {
            return Decision.WAITING;
        }
        return Decision.DRINK;
    }
}
