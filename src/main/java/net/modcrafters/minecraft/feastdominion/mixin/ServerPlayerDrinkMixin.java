package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.thirst.Drinking;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerDrinkMixin {
    /** An empty hand never reaches the normal finish, so the drink ends here. */
    @Inject(method = "completeUsingItem", at = @At("HEAD"), cancellable = true)
    private void feast$finishDrink(CallbackInfo ci) {
        if (Drinking.finish((ServerPlayer) (Object) this)) {
            ci.cancel();
        }
    }
}
