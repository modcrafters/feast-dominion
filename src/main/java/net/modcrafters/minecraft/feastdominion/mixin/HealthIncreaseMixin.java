package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.feature.HealthIncreaseFeature;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class HealthIncreaseMixin {
    /** Keeps the bonus on a living player. Once a second is enough while the amount rarely changes. */
    @Inject(method = "tick", at = @At("TAIL"))
    private void feast$healthTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.tickCount <= 20 || player.tickCount % 20 == 0) {
            HealthIncreaseFeature.instance().apply(player);
        }
    }

    /** A new body after death does not keep a transient modifier, so write it again. */
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void feast$healthRespawn(ServerPlayer oldPlayer, boolean keptInventory, CallbackInfo ci) {
        HealthIncreaseFeature.instance().apply((ServerPlayer) (Object) this);
    }
}
