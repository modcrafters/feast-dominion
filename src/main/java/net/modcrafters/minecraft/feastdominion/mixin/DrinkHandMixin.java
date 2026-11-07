package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.client.DrinkPose;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.1 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class DrinkHandMixin {
    @Invoker("renderPlayerHand")
    abstract void feast$drawHand(PoseStack pose, SubmitNodeCollector collector, int light, HumanoidArm arm, PlayerRenderState playerState);

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void feast$drink(
            PlayerRenderState playerState,
            FirstPersonHandsAndItemsRenderState state,
            float partialTick,
            float pitch,
            InteractionHand hand,
            float swing,
            ItemStack stack,
            float equip,
            PoseStack pose,
            SubmitNodeCollector collector,
            int light,
            CallbackInfo ci
    ) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null
                || !stack.isEmpty()
                || hand != InteractionHand.MAIN_HAND
                || playerState.avatarRenderState.isInvisible
                || !player.isUsingItem()
                || player.getUsedItemHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        pose.pushPose();
        DrinkPose.place(pose, partialTick, player.getUseItemRemainingTicks(), player.getMainArm());
        this.feast$drawHand(pose, collector, light, player.getMainArm(), playerState);
        pose.popPose();
        ci.cancel();
    }
}
//?} else {
/*import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemInHandRenderer.class)
public abstract class DrinkHandMixin {
    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void feast$drink(
            AbstractClientPlayer player,
            float partialTick,
            float pitch,
            InteractionHand hand,
            float swing,
            ItemStack stack,
            float equip,
            PoseStack pose,
            MultiBufferSource buffer,
            int light,
            CallbackInfo ci
    ) {
        if (!stack.isEmpty()
                || hand != InteractionHand.MAIN_HAND
                || player.isInvisible()
                || !player.isUsingItem()
                || player.getUsedItemHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        HumanoidArm arm = player.getMainArm();
        pose.pushPose();
        DrinkPose.place(pose, partialTick, player.getUseItemRemainingTicks(), arm);
        PlayerRenderer renderer = (PlayerRenderer) this.entityRenderDispatcher.getRenderer(player);
        if (arm == HumanoidArm.RIGHT) {
            renderer.renderRightHand(pose, buffer, light, player);
        } else {
            renderer.renderLeftHand(pose, buffer, light, player);
        }
        pose.popPose();
        ci.cancel();
    }
}
*///?}
