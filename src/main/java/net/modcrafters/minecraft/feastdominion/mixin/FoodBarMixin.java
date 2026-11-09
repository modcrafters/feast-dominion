package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.client.SaturationHud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=26.1 {
import net.minecraft.client.gui.Hud;
//?} else {
/*import net.minecraft.client.gui.Gui;
*///?}

/** Draws after the hunger shanks, on the same pixels. The third int is their top, the fourth their right edge. */
//? if >=26.1 {
@Mixin(Hud.class)
public class FoodBarMixin {
    @Inject(method = "extractFood", at = @At("RETURN"))
    private void feast$saturation(GuiGraphicsExtractor graphics, Player player, int top, int right, CallbackInfo ci) {
        SaturationHud.draw(graphics, player, right, top);
    }
}
//?} else {
/*@Mixin(Gui.class)
public class FoodBarMixin {
    @Inject(method = "renderFood", at = @At("RETURN"))
    private void feast$saturation(GuiGraphicsExtractor graphics, Player player, int top, int right, CallbackInfo ci) {
        SaturationHud.draw(graphics, player, right, top);
    }
}
*///?}
