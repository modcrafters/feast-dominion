package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.modcrafters.minecraft.feastdominion.client.SaturationIcons.Mark;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstIcons;
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

/** Hunger icons for the hunger restored, with a saturation outline on each icon it fills, then thirst droplets with quench outlines. Past one full bar, a larger outline sits on top. */
public record SaturationTooltip(int nutrition, float saturation, int thirst, float quench) implements TooltipComponent, ClientTooltipComponent {
    private static final int STEP = 8;
    private static final int ICON = 9;
    private static final int RAISED = 11;
    private static final Identifier FOOD_FULL = FeastIds.of("hud/tooltip_food_full");
    private static final Identifier FOOD_HALF = FeastIds.of("hud/tooltip_food_half");
    private static final Identifier OUTLINE = FeastIds.of("hud/tooltip_saturation");
    private static final Identifier OUTLINE_HALF = FeastIds.of("hud/tooltip_saturation_half");
    private static final Identifier RAISED_FULL = FeastIds.of("hud/tooltip_saturation_2");
    private static final Identifier RAISED_HALF = FeastIds.of("hud/tooltip_saturation_half_2");
    private static final Identifier THIRST_FULL = FeastIds.of("hud/thirst_full");
    private static final Identifier THIRST_HALF = FeastIds.of("hud/thirst_half");
    private static final Identifier QUENCH = FeastIds.of("hud/tooltip_quench");
    private static final Identifier QUENCH_HALF = FeastIds.of("hud/tooltip_quench_half");
    private static final Identifier QUENCH_RAISED = FeastIds.of("hud/tooltip_quench_2");
    private static final Identifier QUENCH_RAISED_HALF = FeastIds.of("hud/tooltip_quench_half_2");

    private int hungerPad() {
        return this.saturation > 20.0F ? (RAISED - ICON) / 2 : 0;
    }

    private int thirstPad() {
        return this.quench > 20.0F ? (RAISED - ICON) / 2 : 0;
    }

    private int icons() {
        return Math.max(SaturationIcons.count(this.nutrition), SaturationIcons.count(this.saturation));
    }

    private int thirstMarks() {
        return Math.max(ThirstIcons.count(this.thirst), SaturationIcons.count(this.quench));
    }

    private int hungerHeight() {
        return icons() <= 0 ? 0 : ICON + hungerPad() * 2;
    }

    private int thirstHeight() {
        return thirstMarks() <= 0 ? 0 : ICON + thirstPad() * 2;
    }

    private int rowGap() {
        return hungerHeight() > 0 && thirstHeight() > 0 ? 1 : 0;
    }

    private int height() {
        return hungerHeight() + rowGap() + thirstHeight();
    }

    private int width() {
        int marks = icons();
        int hunger = marks <= 0 ? 0 : (marks - 1) * STEP + ICON + hungerPad() * 2;
        int droplets = thirstMarks();
        int thirst = droplets <= 0 ? 0 : thirstPad() + (droplets - 1) * STEP + ICON;
        return Math.max(hunger, thirst);
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
        int pad = hungerPad();
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
        int droplets = thirstMarks();
        int thirstPad = thirstPad();
        int top = y + hungerHeight() + rowGap();
        for (int i = 0; i < droplets; i++) {
            int at = x + thirstPad + i * STEP;
            ThirstIcons.Fill fill = ThirstIcons.fill(this.thirst, i);
            if (fill == ThirstIcons.Fill.FULL) {
                SaturationHud.blit(graphics, THIRST_FULL, at, top + thirstPad);
            } else if (fill == ThirstIcons.Fill.HALF) {
                SaturationHud.blit(graphics, THIRST_HALF, at, top + thirstPad);
            }
            Mark reserve = SaturationIcons.mark(this.quench, i);
            if (reserve == Mark.FULL) {
                SaturationHud.blit(graphics, QUENCH, at, top + thirstPad);
            } else if (reserve != Mark.NONE) {
                SaturationHud.blit(graphics, QUENCH_HALF, at, top + thirstPad);
            }
            Mark over = SaturationIcons.extra(this.quench, i);
            if (over == Mark.FULL) {
                SaturationHud.blit(graphics, QUENCH_RAISED, at - thirstPad, top, RAISED);
            } else if (over != Mark.NONE) {
                SaturationHud.blit(graphics, QUENCH_RAISED_HALF, at - thirstPad, top, RAISED);
            }
        }
    }

    //? if >=26.1 {
    @Override
    public int getHeight(Font font) {
        return height();
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
        return height();
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
