package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.client.SaturationTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Teaches the tooltip renderer the saturation row. */
@Mixin(ClientTooltipComponent.class)
public interface SaturationTooltipMixin {
    @Inject(
            method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void feast$saturation(TooltipComponent component, CallbackInfoReturnable<ClientTooltipComponent> call) {
        if (component instanceof SaturationTooltip tip) {
            call.setReturnValue(tip);
        }
    }
}
