package net.modcrafters.minecraft.feastdominion.neoforge.client;

import net.modcrafters.minecraft.feastdominion.client.ThirstHud;
import net.modcrafters.minecraft.feastdominion.client.WellnessHud;
//? if >=26.1 {
//?} else {
/*import net.modcrafters.minecraft.feastdominion.client.SaturationHud;
import net.modcrafters.minecraft.feastdominion.compat.FarmersDelight;
*///?}
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
//? if >=26.1 {
//?} else {
/*import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}

public final class FeastDominionNeoForgeClient {
    private FeastDominionNeoForgeClient() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(FeastDominionNeoForgeClient::registerLayers);
        //? if >=26.1 {
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(FeastDominionNeoForgeClient::afterNourishment);
        *///?}
    }

    private static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, ThirstHud.ID, FeastDominionNeoForgeClient::renderThirst);
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, WellnessHud.ID, FeastDominionNeoForgeClient::renderWellness);
    }

    //? if >=26.1 {
    //?} else {
    /*private static void afterNourishment(RenderGuiLayerEvent.Post event) {
        if (!FarmersDelight.NOURISHMENT_LAYER.equals(event.getName())) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.options.hideGui || !(minecraft.getCameraEntity() instanceof Player camera)
                ? null
                : camera;
        SaturationHud.drawOverNourishment(event.getGuiGraphics(), player);
    }
    *///?}

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
