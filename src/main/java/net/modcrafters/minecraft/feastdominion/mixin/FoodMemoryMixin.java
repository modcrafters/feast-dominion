package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.feature.HealthIncreaseFeature;
import net.modcrafters.minecraft.feastdominion.wellness.DietClock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class FoodMemoryMixin {
    @Shadow
    protected ItemStack useItem;

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void feast$rememberFood(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            HealthIncreaseFeature.instance().consume(player, this.useItem);
            DietClock.eat(player, this.useItem);
        }
    }
}
