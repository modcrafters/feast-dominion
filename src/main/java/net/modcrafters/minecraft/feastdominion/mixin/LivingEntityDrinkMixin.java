package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.thirst.Drink;
import net.modcrafters.minecraft.feastdominion.thirst.Drinking;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDrinkMixin {
    @Shadow
    protected ItemStack useItem;

    @Shadow
    protected int useItemRemaining;

    @Shadow
    protected abstract void setLivingEntityFlag(int flag, boolean value);

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void feast$startDrink(InteractionHand hand, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide() || !(self instanceof Player player) || !Drinking.claim(player)) {
            return;
        }
        this.useItem = ItemStack.EMPTY;
        this.useItemRemaining = Drink.HOLD;
        this.setLivingEntityFlag(1, true);
        this.setLivingEntityFlag(2, hand == InteractionHand.OFF_HAND);
        ci.cancel();
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    private void feast$gulp(ItemStack stack, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player) {
            Drinking.gulp(player);
        }
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"), cancellable = true)
    private void feast$finishDrink(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && Drinking.finish(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void feast$dropDrink(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player) {
            Drinking.drop(player);
        }
    }
}
