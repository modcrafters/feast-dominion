package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** Ten hunger shanks, filled from the left with the tooltip sprites. Two points fill one shank. */
public final class HungerBar {
    public static final int STEP = 8;
    public static final int ICON = 9;
    public static final int COUNT = 10;
    public static final int WIDTH = (COUNT - 1) * STEP + ICON;
    private static final Identifier EMPTY = FeastIds.of("hud/tooltip_food_empty");
    private static final Identifier HALF = FeastIds.of("hud/tooltip_food_half");
    private static final Identifier FULL = FeastIds.of("hud/tooltip_food_full");

    private HungerBar() {
    }

    /** {@code points} runs from 0 to 20. Every shank is drawn, so the row always shows the full bar. */
    public static void draw(GuiGraphicsExtractor graphics, int x, int y, int points) {
        draw(graphics, x, y, points, false);
    }

    /** {@code ink} draws the row black. The caller shifts {@code y} when the row should bob. */
    public static void draw(GuiGraphicsExtractor graphics, int x, int y, int points, boolean ink) {
        //? if >=1.21.6 {
        paint(graphics, x, y, points, ink);
        //?} else {
        /*if (ink) {
            graphics.setColor(0.0F, 0.0F, 0.0F, 1.0F);
        }
        paint(graphics, x, y, points, false);
        if (ink) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        *///?}
    }

    private static void paint(GuiGraphicsExtractor graphics, int x, int y, int points, boolean ink) {
        for (int fromLeft = 0; fromLeft < COUNT; fromLeft++) {
            int at = x + fromLeft * STEP;
            blit(graphics, EMPTY, at, y, ink);
            Identifier filled = fill(points, fromLeft);
            if (filled != null) {
                blit(graphics, filled, at, y, ink);
            }
        }
    }

    private static void blit(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y, boolean ink) {
        //? if >=1.21.6 {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, ICON, ICON, ink ? 0xFF000000 : -1);
        //?} else
        //graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, ICON, ICON);
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
