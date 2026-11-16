package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/** Ten hunger shanks, filled from the left with the tooltip sprites. Two points fill one shank. */
public final class HungerBar {
    public static final int STEP = 8;
    public static final int ICON = 9;
    public static final int COUNT = 10;
    public static final int WIDTH = (COUNT - 1) * STEP + ICON;
    private static final Identifier EMPTY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_food_empty");
    private static final Identifier HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_food_half");
    private static final Identifier FULL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_food_full");

    private HungerBar() {
    }

    /** {@code points} runs from 0 to 20. Every shank is drawn, so the row always shows the full bar. */
    public static void draw(GuiGraphicsExtractor graphics, int x, int y, int points) {
        for (int fromLeft = 0; fromLeft < COUNT; fromLeft++) {
            int at = x + fromLeft * STEP;
            SaturationHud.blit(graphics, EMPTY, at, y);
            Identifier fill = fill(points, fromLeft);
            if (fill != null) {
                SaturationHud.blit(graphics, fill, at, y);
            }
        }
    }

    private static Identifier fill(int points, int fromLeft) {
        int mark = fromLeft * 2 + 1;
        if (mark < points) {
            return FULL;
        }
        if (mark == points) {
            return HALF;
        }
        return null;
    }
}
