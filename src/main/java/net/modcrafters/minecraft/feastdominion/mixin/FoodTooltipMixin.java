package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.client.SaturationTooltip;
import net.modcrafters.minecraft.feastdominion.client.ThirstCatalog;
import net.modcrafters.minecraft.feastdominion.compat.AppleSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/** Puts hunger icons, the saturation outlines on them, and thirst droplets on food that has no other tooltip image. */
@Mixin(ItemStack.class)
public class FoodTooltipMixin {
    @Inject(method = "getTooltipImage", at = @At("RETURN"), cancellable = true)
    private void feast$saturation(CallbackInfoReturnable<Optional<TooltipComponent>> call) {
        if (AppleSkin.loaded() || call.getReturnValue().isPresent()) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        FoodProperties food = stack.get(DataComponents.FOOD);
        int thirst = ThirstCatalog.points(stack);
        float quench = ThirstCatalog.quench(stack);
        boolean feeds = food != null && (food.nutrition() > 0 || food.saturation() > 0.0F);
        if (!feeds && thirst <= 0 && quench <= 0.0F) {
            return;
        }
        int nutrition = food == null ? 0 : food.nutrition();
        float saturation = food == null ? 0.0F : food.saturation();
        call.setReturnValue(Optional.of(new SaturationTooltip(nutrition, saturation, thirst, quench)));
    }
}
