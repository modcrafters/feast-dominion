package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.modcrafters.minecraft.feastdominion.client.SaturationIcons.Mark;
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
    public static final Identifier ID = FeastIds.of("thirst");
    private static final Identifier EMPTY = FeastIds.of("hud/thirst_empty");
    private static final Identifier HALF = FeastIds.of("hud/thirst_half");
    private static final Identifier FULL = FeastIds.of("hud/thirst_full");
    private static final Identifier QUENCH_FULL = FeastIds.of("hud/quench_full");
    private static final Identifier QUENCH_HALF = FeastIds.of("hud/quench_half");
    private static final Identifier QUENCH_FULL_OVER = FeastIds.of("hud/quench_full_2");
    private static final Identifier QUENCH_HALF_OVER = FeastIds.of("hud/quench_half_2");
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
        float quench = Services.THIRST.level(player).quench();
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            Mark mark = SaturationIcons.mark(quench, fromRight);
            if (mark == Mark.NONE) {
                break;
            }
            int x = xRight - fromRight * 8 - 9;
            SaturationHud.blit(graphics, mark == Mark.FULL ? QUENCH_FULL : QUENCH_HALF, x, y);
            Mark over = SaturationIcons.extra(quench, fromRight);
            if (over != Mark.NONE) {
                SaturationHud.blit(graphics, over == Mark.FULL ? QUENCH_FULL_OVER : QUENCH_HALF_OVER, x - 1, y - 1, 11);
            }
        }
    }
}
