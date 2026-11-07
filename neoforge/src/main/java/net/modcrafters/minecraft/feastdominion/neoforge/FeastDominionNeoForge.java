package net.modcrafters.minecraft.feastdominion.neoforge;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.FeastDominion;
import net.modcrafters.minecraft.feastdominion.neoforge.client.FeastDominionNeoForgeClient;
import net.modcrafters.minecraft.feastdominion.neoforge.platform.NeoForgeThirstStore;
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
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(Constants.MOD_ID)
public class FeastDominionNeoForge {
    public FeastDominionNeoForge(IEventBus modBus) {
        NeoForgeThirstStore.ATTACHMENTS.register(modBus);
        NeoForge.EVENT_BUS.addListener(FeastDominionNeoForge::onUseBlock);
        NeoForge.EVENT_BUS.addListener(FeastDominionNeoForge::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(FeastDominionNeoForge::onLogout);
        //? if >=1.21.9 {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
        //?} else
        //if (FMLEnvironment.dist == Dist.CLIENT) {
            FeastDominionNeoForgeClient.register(modBus);
        }
        FeastDominion.init();
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
        }
    }

    private static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ThirstClock.save(player);
        }
    }
}
