package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.client.SaturationIcons.Mark;
import net.modcrafters.minecraft.feastdominion.compat.AppleSkin;
import net.modcrafters.minecraft.feastdominion.compat.FarmersDelight;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?} else {
/*import com.mojang.blaze3d.systems.RenderSystem;
*///?}
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

/** Saturation marks on the hunger shanks. The art lives next to the other hud sprites. */
public final class SaturationHud {
    private static final Identifier FULL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/saturation_full");
    private static final Identifier HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/saturation_half");
    private static final Identifier FULL_OVER = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/saturation_full_2");
    private static final Identifier HALF_OVER = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/saturation_half_2");
    private static final int OVER = 11;
    private static int foodRight;
    private static int foodTop;
    private static boolean placed;

    private SaturationHud() {
    }

    public static void draw(GuiGraphicsExtractor graphics, Player player, int right, int top) {
        if (AppleSkin.loaded()) {
            return;
        }
        foodRight = right;
        foodTop = top;
        placed = true;
        // Delight paints over these marks. The layer above that overlay draws them instead.
        if (FarmersDelight.nourishment(player)) {
            return;
        }
        paint(graphics, player, right, top);
    }

    /** Draws on the hunger icons after Delight's nourishment overlay. A null player only clears the saved spot. */
    public static void drawOverNourishment(GuiGraphicsExtractor graphics, Player player) {
        if (!placed) {
            return;
        }
        placed = false;
        if (player == null || !FarmersDelight.nourishment(player)) {
            return;
        }
        paint(graphics, player, foodRight, foodTop);
    }

    private static void paint(GuiGraphicsExtractor graphics, Player player, int right, int top) {
        float saturation = player.getFoodData().getSaturationLevel();
        if (saturation <= 0.0F) {
            return;
        }
        //? if >=1.21.6 {
        paintMarks(graphics, saturation, right, top);
        //?} else {
        /*RenderSystem.enableBlend();
        try {
            paintMarks(graphics, saturation, right, top);
        } finally {
            RenderSystem.disableBlend();
        }
        *///?}
    }

    private static void paintMarks(GuiGraphicsExtractor graphics, float saturation, int right, int top) {
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            Mark mark = SaturationIcons.mark(saturation, fromRight);
            if (mark == Mark.NONE) {
                break;
            }
            int x = right - fromRight * 8 - 9;
            blit(graphics, sprite(mark), x, top);
            Mark over = SaturationIcons.extra(saturation, fromRight);
            if (over != Mark.NONE) {
                blit(graphics, over == Mark.FULL ? FULL_OVER : HALF_OVER, x - 1, top - 1, OVER);
            }
        }
    }

    static Identifier sprite(Mark mark) {
        return mark == Mark.FULL ? FULL : HALF;
    }

    static void blit(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y) {
        blit(graphics, sprite, x, y, 9);
    }

    static void blit(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y, int size) {
        //? if >=1.21.6 {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, size, size);
        //?} else {
        /*graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, size, size);
        *///?}
    }
}
