package net.modcrafters.minecraft.feastdominion.neoforge;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.FeastDominion;
import net.modcrafters.minecraft.feastdominion.neoforge.client.FeastDominionNeoForgeClient;
import net.modcrafters.minecraft.feastdominion.feature.VarietySync;
import net.modcrafters.minecraft.feastdominion.feature.VarietyView;
import net.modcrafters.minecraft.feastdominion.neoforge.platform.NeoForgeDietStore;
import net.modcrafters.minecraft.feastdominion.neoforge.platform.NeoForgeGroupStore;
import net.modcrafters.minecraft.feastdominion.neoforge.platform.NeoForgeThirstStore;
import net.modcrafters.minecraft.feastdominion.debug.DebugCommands;
import net.modcrafters.minecraft.feastdominion.wellness.DietClock;
import net.modcrafters.minecraft.feastdominion.network.SaturationPayload;
import net.modcrafters.minecraft.feastdominion.network.VarietyPayload;
import net.modcrafters.minecraft.feastdominion.network.SaturationSync;
import net.modcrafters.minecraft.feastdominion.thirst.Drinking;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstClock;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Constants.MOD_ID)
public class FeastDominionNeoForge {
    public FeastDominionNeoForge(IEventBus modBus) {
        modBus.addListener(FeastDominionNeoForge::registerPayloads);
        NeoForgeThirstStore.ATTACHMENTS.register(modBus);
        NeoForgeDietStore.ATTACHMENTS.register(modBus);
        NeoForgeGroupStore.ATTACHMENTS.register(modBus);
        NeoForge.EVENT_BUS.addListener(FeastDominionNeoForge::onUseBlock);
        NeoForge.EVENT_BUS.addListener(FeastDominionNeoForge::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(FeastDominionNeoForge::onLogout);
        NeoForge.EVENT_BUS.addListener(FeastDominionNeoForge::onCommands);
        //? if >=1.21.9 {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
        //?} else
        //if (FMLEnvironment.dist == Dist.CLIENT) {
            FeastDominionNeoForgeClient.register(modBus);
        }
        FeastDominion.init();
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(Constants.MOD_ID)
                .playToClient(
                        SaturationPayload.TYPE,
                        SaturationPayload.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() ->
                                context.player().getFoodData().setSaturation(payload.saturation()))
                )
                .playToClient(
                        VarietyPayload.TYPE,
                        VarietyPayload.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> VarietyView.set(payload.snapshot()))
                );
    }

    private static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        Direction face = event.getFace();
        if (face == null) {
            return;
        }
        InteractionResult result = Drinking.interact(
                event.getEntity(),
                event.getLevel(),
                event.getHand(),
                event.getPos(),
                face
        );
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ThirstClock.tick(player);
            DietClock.tick(player);
            SaturationSync.tick(player);
        }
    }

    private static void onCommands(RegisterCommandsEvent event) {
        DebugCommands.register(event.getDispatcher());
    }

    private static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ThirstClock.save(player);
            DietClock.save(player);
            SaturationSync.forget(player);
            VarietySync.forget(player);
        }
    }
}
