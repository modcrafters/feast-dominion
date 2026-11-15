package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.feature.HealthIncreaseFeature;
import net.modcrafters.minecraft.feastdominion.taste.LeanClock;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstFood;
import net.modcrafters.minecraft.feastdominion.wellness.DietClock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class FoodMemoryMixin {
    @Shadow
    protected ItemStack useItem;

    @Unique
    private ItemStack feast$eaten = ItemStack.EMPTY;

    @Unique
    private int feast$hungerBefore;

    @Unique
    private float feast$saturationBefore;

    @Unique
    private InteractionHand feast$hand;

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void feast$rememberFood(CallbackInfo ci) {
        this.feast$eaten = ItemStack.EMPTY;
        if ((Object) this instanceof ServerPlayer player) {
            this.feast$eaten = this.useItem.copy();
            this.feast$hungerBefore = player.getFoodData().getFoodLevel();
            this.feast$saturationBefore = player.getFoodData().getSaturationLevel();
            this.feast$hand = ((LivingEntity) (Object) this).getUsedItemHand();
            HealthIncreaseFeature.instance().consume(player, this.useItem);
            DietClock.eat(player, this.useItem);
        }
    }

    @Inject(method = "completeUsingItem", at = @At("RETURN"))
    private void feast$lean(CallbackInfo ci) {
        ItemStack eaten = this.feast$eaten;
        this.feast$eaten = ItemStack.EMPTY;
        if (!eaten.isEmpty() && (Object) this instanceof ServerPlayer player) {
            LeanClock.eat(player, eaten, this.feast$hungerBefore, this.feast$saturationBefore);
            ThirstFood.eat(player, eaten, this.feast$hand);
        }
    }
}
