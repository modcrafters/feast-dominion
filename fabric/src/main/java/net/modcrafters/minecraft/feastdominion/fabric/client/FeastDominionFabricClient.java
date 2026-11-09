package net.modcrafters.minecraft.feastdominion.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.modcrafters.minecraft.feastdominion.client.ThirstHud;
import net.modcrafters.minecraft.feastdominion.client.WellnessHud;
import net.modcrafters.minecraft.feastdominion.network.SaturationPayload;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class FeastDominionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SaturationPayload.TYPE, (payload, context) -> context.client().execute(() -> {
            if (context.client().player != null) {
                context.client().player.getFoodData().setSaturation(payload.saturation());
            }
        }));
        HudElementRegistry.attachElementAfter(VanillaHudElements.FOOD_BAR, ThirstHud.ID, FeastDominionFabricClient::renderThirst);
        HudStatusBarHeightRegistry.addRight(ThirstHud.ID, ThirstHud::barHeight);
        HudElementRegistry.attachElementAfter(VanillaHudElements.HEALTH_BAR, WellnessHud.ID, FeastDominionFabricClient::renderWellness);
        HudStatusBarHeightRegistry.addLeft(WellnessHud.ID, WellnessHud::barHeight);
    }

    private static void renderThirst(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!(Minecraft.getInstance().getCameraEntity() instanceof Player player) || !ThirstHud.shown(player)) {
            return;
        }
        int y = graphics.guiHeight() - HudStatusBarHeightRegistry.getHeight(ThirstHud.ID);
        ThirstHud.draw(graphics, graphics.guiWidth() / 2 + 91, y, player);
    }

    private static void renderWellness(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!(Minecraft.getInstance().getCameraEntity() instanceof Player player) || !WellnessHud.shown(player)) {
            return;
        }
        int y = graphics.guiHeight() - HudStatusBarHeightRegistry.getHeight(WellnessHud.ID);
        WellnessHud.draw(graphics, graphics.guiWidth() / 2 - 91, y);
    }
}
