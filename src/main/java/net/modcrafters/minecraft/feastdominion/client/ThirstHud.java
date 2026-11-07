package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class ThirstHud {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "thirst");
    private static final Identifier EMPTY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/thirst_empty");
    private static final Identifier HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/thirst_half");
    private static final Identifier FULL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/thirst_full");
    public static final int ROW = 10;

    private ThirstHud() {
    }

    /** Hunger is hidden while a ridden mob shows its own hearts, and in creative. */
    public static boolean shown(Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()) {
            return false;
        }
        if (player.getVehicle() instanceof LivingEntity mount && mount.showVehicleHealth()) {
            int hearts = (int) (mount.getMaxHealth() + 0.5F) / 2;
            if (hearts > 30) {
                hearts = 30;
            }
            return hearts == 0;
        }
        return true;
    }

    public static int barHeight(Player player) {
        return shown(player) ? ROW : 0;
    }

    public static void draw(GuiGraphicsExtractor graphics, int xRight, int y, Player player) {
        int points = Services.THIRST.points(player);
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            int x = xRight - fromRight * 8 - 9;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EMPTY, x, y, 9, 9);
            Identifier fill = switch (ThirstIcons.fill(points, fromRight)) {
                case FULL -> FULL;
                case HALF -> HALF;
                case EMPTY -> null;
            };
            if (fill != null) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, fill, x, y, 9, 9);
            }
        }
    }
}
