package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.client.DrinkDrops;
import net.modcrafters.minecraft.feastdominion.thirst.Drink;
import net.modcrafters.minecraft.feastdominion.thirst.Drinking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerDrinkMixin {
    @Shadow
    private boolean startedUsingItem;

    @Shadow
    private InteractionHand usingItemHand;

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void feast$startDrink(InteractionHand hand, CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        if (!self.getItemInHand(hand).isEmpty() || self.isUsingItem()) {
            return;
        }
        Drinking.claim(self);
        LivingEntityDrinkAccess access = (LivingEntityDrinkAccess) (Object) this;
        access.feast$setUseItem(ItemStack.EMPTY);
        access.feast$setUseItemRemaining(Drink.HOLD);
        this.startedUsingItem = true;
        this.usingItemHand = hand;
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void feast$drops(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        if (self.isUsingItem() && self.getItemInHand(self.getUsedItemHand()).isEmpty()) {
            DrinkDrops.spawn(self);
        }
    }
}
