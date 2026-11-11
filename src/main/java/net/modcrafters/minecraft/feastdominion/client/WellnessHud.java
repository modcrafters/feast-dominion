package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;
import net.modcrafters.minecraft.feastdominion.wellness.WellnessIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public final class WellnessHud {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "wellness");
    private static final Identifier EMPTY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/wellness_empty");
    private static final Identifier HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/wellness_half");
    private static final Identifier FULL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/wellness_full");
    public static final int ROW = 10;

    private WellnessHud() {
    }

    /** Shown with the hearts. A mount's hearts replace hunger, not health. */
    public static boolean shown(Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer();
    }

    public static int barHeight(Player player) {
        return shown(player) ? ROW : 0;
    }

    public static void draw(GuiGraphicsExtractor graphics, int xLeft, int y) {
        Player player = Minecraft.getInstance().player;
        int points = player == null ? Wellness.POINTS / 2 : Services.GROUPS.memory(player).median();
        for (int fromLeft = 0; fromLeft < 10; fromLeft++) {
            int x = xLeft + fromLeft * 8;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EMPTY, x, y, 9, 9);
            Identifier fill = switch (fill(points, fromLeft)) {
                case FULL -> FULL;
                case HALF -> HALF;
                case EMPTY -> null;
            };
            if (fill != null) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, fill, x, y, 9, 9);
            }
        }
    }

    /** {@code fromLeft} 0 is the leftmost mark, matching the hearts. */
    private static WellnessIcons.Fill fill(int points, int fromLeft) {
        int mark = fromLeft * 2 + 1;
        if (mark < points) {
            return WellnessIcons.Fill.FULL;
        }
        if (mark == points) {
            return WellnessIcons.Fill.HALF;
        }
        return WellnessIcons.Fill.EMPTY;
    }
}
