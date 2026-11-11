package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.client.GroupTooltip;
import net.modcrafters.minecraft.feastdominion.client.TasteTooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/** Adds the taste line and the food-group lines to an item's tooltip. */
@Mixin(ItemStack.class)
public class GroupTooltipMixin {
    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    private void feast$groups(
            Item.TooltipContext context,
            Player player,
            TooltipFlag flag,
            CallbackInfoReturnable<List<Component>> call
    ) {
        ItemStack stack = (ItemStack) (Object) this;
        //? if >=26.1 {
        net.minecraft.world.item.component.TooltipDisplay display = stack.get(DataComponents.TOOLTIP_DISPLAY);
        if ((display != null && display.hideTooltip()) || call.getReturnValue() == null) {
            return;
        }
        //?} else {
        /*if (stack.has(DataComponents.HIDE_TOOLTIP) || call.getReturnValue() == null) {
            return;
        }
        *///?}
        List<Component> lines = new ArrayList<>(call.getReturnValue());
        TasteTooltip.add(stack, lines);
        GroupTooltip.add(stack, lines);
        call.setReturnValue(lines);
    }
}
