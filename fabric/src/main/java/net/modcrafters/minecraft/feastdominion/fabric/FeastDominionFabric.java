package net.modcrafters.minecraft.feastdominion.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.modcrafters.minecraft.feastdominion.FeastDominion;
import net.modcrafters.minecraft.feastdominion.feature.VarietySync;
import net.modcrafters.minecraft.feastdominion.network.SaturationPayload;
import net.modcrafters.minecraft.feastdominion.network.SaturationSync;
import net.modcrafters.minecraft.feastdominion.network.VarietyPayload;
import net.modcrafters.minecraft.feastdominion.thirst.Drinking;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstClock;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class FeastDominionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(SaturationPayload.TYPE, SaturationPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VarietyPayload.TYPE, VarietyPayload.STREAM_CODEC);
        UseBlockCallback.EVENT.register(FeastDominionFabric::onUseBlock);
        ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(player -> {
            ThirstClock.tick(player);
            SaturationSync.tick(player);
        }));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ThirstClock.save(handler.getPlayer());
            SaturationSync.forget(handler.getPlayer());
            VarietySync.forget(handler.getPlayer());
        });
        FeastDominion.init();
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        return Drinking.interact(player, level, hand, hit.getBlockPos(), hit.getDirection());
    }
}
