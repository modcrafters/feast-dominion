package net.modcrafters.minecraft.feastdominion.neoforge.client;

import net.modcrafters.minecraft.feastdominion.client.ThirstHud;
import net.modcrafters.minecraft.feastdominion.client.WellnessHud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class FeastDominionNeoForgeClient {
    private FeastDominionNeoForgeClient() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(FeastDominionNeoForgeClient::registerLayers);
    }

    private static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, ThirstHud.ID, FeastDominionNeoForgeClient::renderThirst);
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, WellnessHud.ID, FeastDominionNeoForgeClient::renderWellness);
    }

    private static void renderThirst(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.1 {
        if (minecraft.gui.hud.isHidden()) {
            return;
        }
        //?} else {
        /*if (minecraft.options.hideGui) {
            return;
        }
        *///?}
        if (!(minecraft.getCameraEntity() instanceof Player player) || !ThirstHud.shown(player)) {
            return;
        }
        //? if >=26.1 {
        int y = graphics.guiHeight() - minecraft.gui.hud.rightHeight;
        minecraft.gui.hud.rightHeight += ThirstHud.ROW;
        //?} else {
        /*int y = graphics.guiHeight() - minecraft.gui.rightHeight;
        minecraft.gui.rightHeight += ThirstHud.ROW;
        *///?}
        ThirstHud.draw(graphics, graphics.guiWidth() / 2 + 91, y, player);
    }

    private static void renderWellness(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.1 {
        if (minecraft.gui.hud.isHidden()) {
            return;
        }
        //?} else {
        /*if (minecraft.options.hideGui) {
            return;
        }
        *///?}
        if (!(minecraft.getCameraEntity() instanceof Player player) || !WellnessHud.shown(player)) {
            return;
        }
        //? if >=26.1 {
        int y = graphics.guiHeight() - minecraft.gui.hud.leftHeight;
        minecraft.gui.hud.leftHeight += WellnessHud.ROW;
        //?} else {
        /*int y = graphics.guiHeight() - minecraft.gui.leftHeight;
        minecraft.gui.leftHeight += WellnessHud.ROW;
        *///?}
        WellnessHud.draw(graphics, graphics.guiWidth() / 2 - 91, y);
    }
}
