package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.client.SaturationIcons.Mark;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
//? if >=1.21.6 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

/** Hunger icons for the hunger restored, with a saturation outline on each icon it fills. Past one full bar, a larger outline sits on top. */
public record SaturationTooltip(int nutrition, float saturation) implements TooltipComponent, ClientTooltipComponent {
    private static final int STEP = 8;
    private static final int ICON = 9;
    private static final int RAISED = 11;
    private static final Identifier FOOD_FULL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_food_full");
    private static final Identifier FOOD_HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_food_half");
    private static final Identifier OUTLINE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_saturation");
    private static final Identifier OUTLINE_HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_saturation_half");
    private static final Identifier RAISED_FULL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_saturation_2");
    private static final Identifier RAISED_HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/tooltip_saturation_half_2");

    private int pad() {
        return this.saturation > 20.0F ? (RAISED - ICON) / 2 : 0;
    }

    private int icons() {
        return Math.max(SaturationIcons.count(this.nutrition), SaturationIcons.count(this.saturation));
    }

    private int width() {
        int marks = icons();
        return marks <= 0 ? 0 : (marks - 1) * STEP + ICON + pad() * 2;
    }

    private void paint(GuiGraphicsExtractor graphics, int x, int y) {
        //? if >=1.21.6 {
        blit(graphics, x, y);
        //?} else {
        /*RenderSystem.enableBlend();
        try {
            blit(graphics, x, y);
        } finally {
            RenderSystem.disableBlend();
        }
        *///?}
    }

    private void blit(GuiGraphicsExtractor graphics, int x, int y) {
        int pad = pad();
        int marks = icons();
        for (int i = 0; i < marks; i++) {
            int at = x + pad + i * STEP;
            Mark hunger = SaturationIcons.mark(this.nutrition, i);
            if (hunger != Mark.NONE) {
                SaturationHud.blit(graphics, hunger == Mark.FULL ? FOOD_FULL : FOOD_HALF, at, y + pad);
            }
            Mark sat = SaturationIcons.mark(this.saturation, i);
            if (sat == Mark.FULL) {
                SaturationHud.blit(graphics, OUTLINE, at, y + pad);
            } else if (sat != Mark.NONE) {
                SaturationHud.blit(graphics, OUTLINE_HALF, at, y + pad);
            }
            Mark over = SaturationIcons.extra(this.saturation, i);
            if (over == Mark.FULL) {
                SaturationHud.blit(graphics, RAISED_FULL, at - pad, y, RAISED);
            } else if (over != Mark.NONE) {
                SaturationHud.blit(graphics, RAISED_HALF, at - pad, y, RAISED);
            }
        }
    }

    //? if >=26.1 {
    @Override
    public int getHeight(Font font) {
        return ICON + pad() * 2;
    }

    @Override
    public int getWidth(Font font) {
        return width();
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        paint(graphics, x, y);
    }
    //?} else {
    /*@Override
    public int getHeight() {
        return ICON + pad() * 2;
    }

    @Override
    public int getWidth(Font font) {
        return width();
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphicsExtractor graphics) {
        paint(graphics, x, y);
    }
    *///?}
}
