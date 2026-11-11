package net.modcrafters.minecraft.feastdominion.group;

/** How much of a dish an ingredient is. Hunger is that number. A group with no hunger still counts as one. */
public final class GroupWeight {
    private GroupWeight() {
    }

    public static int of(int nutrition, boolean carries) {
        if (nutrition > 0) {
            return nutrition;
        }
        return carries ? 1 : 0;
    }
}
