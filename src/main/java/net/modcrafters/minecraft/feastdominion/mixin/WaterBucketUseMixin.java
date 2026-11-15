package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.thirst.Drink;
import net.modcrafters.minecraft.feastdominion.thirst.WaterDrink;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=26.1 {
import net.minecraft.world.item.ItemUseAnimation;
//?} else {
/*import net.minecraft.world.item.UseAnim;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** A water bucket can be held to the mouth. Placing water does not start this use. */
@Mixin(Item.class)
public class WaterBucketUseMixin {
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void feast$bucketDrinkTime(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> call) {
        if (WaterDrink.bucket(stack)) {
            call.setReturnValue(Drink.HOLD);
        }
    }

    //? if >=26.1 {
    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void feast$bucketDrinkAnim(ItemStack stack, CallbackInfoReturnable<ItemUseAnimation> call) {
        if (WaterDrink.bucket(stack)) {
            call.setReturnValue(ItemUseAnimation.DRINK);
        }
    }
    //?} else {
    /*@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void feast$bucketDrinkAnim(ItemStack stack, CallbackInfoReturnable<UseAnim> call) {
        if (WaterDrink.bucket(stack)) {
            call.setReturnValue(UseAnim.DRINK);
        }
    }
    *///?}
}
