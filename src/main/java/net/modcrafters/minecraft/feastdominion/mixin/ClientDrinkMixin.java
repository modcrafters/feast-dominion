package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.thirst.Drink;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Supplies the empty-hand duration that vanilla normally reads from the used item. */
@Mixin(LivingEntity.class)
public abstract class ClientDrinkMixin {
    @Shadow @Final protected static EntityDataAccessor<Byte> DATA_LIVING_ENTITY_FLAGS;
    @Shadow protected int useItemRemaining;
    @Unique private boolean feast$remoteDrinking;

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void feast$syncDrink(EntityDataAccessor<?> accessor, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof RemotePlayer) || !DATA_LIVING_ENTITY_FLAGS.equals(accessor)) {
            return;
        }
        boolean drinking = self.isUsingItem()
                && self.getUsedItemHand() == InteractionHand.MAIN_HAND
                && self.getMainHandItem().isEmpty();
        if (drinking && !this.feast$remoteDrinking) {
            this.useItemRemaining = Drink.HOLD;
        } else if (!drinking && this.feast$remoteDrinking && !self.isUsingItem()) {
            this.useItemRemaining = 0;
        }
        this.feast$remoteDrinking = drinking;
    }

    @Inject(method = "getTicksUsingItem()I", at = @At("HEAD"), cancellable = true)
    private void feast$drinkAge(CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player && self.level().isClientSide() && self.isUsingItem()
                && self.getUsedItemHand() == InteractionHand.MAIN_HAND
                && self.getMainHandItem().isEmpty()) {
            cir.setReturnValue(Mth.clamp(Drink.HOLD - this.useItemRemaining, 0, Drink.HOLD));
        }
    }
}
