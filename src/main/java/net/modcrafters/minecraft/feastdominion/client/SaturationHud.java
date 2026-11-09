package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.client.SaturationIcons.Mark;
import net.modcrafters.minecraft.feastdominion.compat.AppleSkin;
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

    private SaturationHud() {
    }

    public static void draw(GuiGraphicsExtractor graphics, Player player, int right, int top) {
        if (AppleSkin.loaded()) {
            return;
        }
        float saturation = player.getFoodData().getSaturationLevel();
        if (saturation <= 0.0F) {
            return;
        }
        //? if >=1.21.6 {
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            Mark mark = SaturationIcons.mark(saturation, fromRight);
            if (mark == Mark.NONE) {
                break;
            }
            blit(graphics, sprite(mark), right - fromRight * 8 - 9, top);
        }
        //?} else {
        /*RenderSystem.enableBlend();
        try {
            for (int fromRight = 0; fromRight < 10; fromRight++) {
                Mark mark = SaturationIcons.mark(saturation, fromRight);
                if (mark == Mark.NONE) {
                    break;
                }
                blit(graphics, sprite(mark), right - fromRight * 8 - 9, top);
            }
        } finally {
            RenderSystem.disableBlend();
        }
        *///?}
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
