package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.client.DrinkPose;
import net.modcrafters.minecraft.feastdominion.thirst.Drink;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=26.1 {
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
*///?}

/** Poses the body arm with its sleeve and matching humanoid armor. */
//? if >=26.1 {
@Mixin(HumanoidModel.class)
public abstract class DrinkBodyMixin {
    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void feast$drink(HumanoidRenderState state, CallbackInfo ci) {
        if (!(state instanceof AvatarRenderState)
                || !state.isUsingItem
                || state.useItemHand != InteractionHand.MAIN_HAND
                || !state.getMainHandItemStack().isEmpty()) {
            return;
        }
        HumanoidArm hand = state.mainArm;
        DrinkPose.placeThirdPerson(hand == HumanoidArm.RIGHT ? this.rightArm : this.leftArm,
                this.head, Mth.clamp(state.ticksUsingItem / Drink.HOLD, 0.0F, 1.0F), hand);
    }
}
//?} else {
/*@Mixin(LivingEntityRenderer.class)
public abstract class DrinkBodyMixin {
    @Shadow protected EntityModel<?> model;

    // Hook the body render, not HumanoidModel.setupAnim: 1.21.1 also calls setupAnim
    // when drawing the first-person hand, whose approved pose must remain independent.
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void feast$drink(LivingEntity entity, float yaw, float partialTick, PoseStack pose,
                            MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!(entity instanceof Player player)
                || !(this.model instanceof PlayerModel<?> model)
                || !player.isUsingItem()
                || player.getUsedItemHand() != InteractionHand.MAIN_HAND
                || !player.getMainHandItem().isEmpty()) {
            return;
        }
        HumanoidArm hand = player.getMainArm();
        ModelPart arm = hand == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        DrinkPose.placeThirdPerson(arm, model.head,
                Drink.held(partialTick, player.getUseItemRemainingTicks()), hand);
        (hand == HumanoidArm.RIGHT ? model.rightSleeve : model.leftSleeve).copyFrom(arm);
    }
}
*///?}
