package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.food.SaturationRoom;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Eating and direct sets keep saturation at or below twice the current hunger. */
@Mixin(FoodData.class)
public abstract class SaturationCapMixin {
    @Shadow
    private int foodLevel;

    @Shadow
    private float saturationLevel;

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float feast$room(float value, float min, float max) {
        return SaturationRoom.clamp(this.foodLevel, value);
    }

    @Inject(method = "setFoodLevel", at = @At("RETURN"))
    private void feast$afterFood(int level, CallbackInfo ci) {
        this.saturationLevel = SaturationRoom.clamp(this.foodLevel, this.saturationLevel);
    }

    @Inject(method = "setSaturation", at = @At("RETURN"))
    private void feast$afterSaturation(float value, CallbackInfo ci) {
        this.saturationLevel = SaturationRoom.clamp(this.foodLevel, this.saturationLevel);
    }
}
