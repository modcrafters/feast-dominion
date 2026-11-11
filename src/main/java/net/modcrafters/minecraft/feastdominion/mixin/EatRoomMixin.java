package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.food.SaturationRoom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hunger still gates eating. A full hunger bar stays edible while saturation is under twice that hunger.
 * Saturation at 40 stops eating, including Delight's Nourishment, which would otherwise force it.
 */
@Mixin(Player.class)
public class EatRoomMixin {
    /**
     * Nourishment also hooks the start of {@code canEat} and cancels it. Injection points are chosen before any
     * callback is inserted, so a later hook lands behind that cancel. Order 0 is applied first and can refuse first.
     */
    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true, order = 0)
    private void feast$full(boolean always, CallbackInfoReturnable<Boolean> call) {
        FoodData food = ((Player) (Object) this).getFoodData();
        if (SaturationRoom.full(food.getSaturationLevel())) {
            call.setReturnValue(false);
        }
    }

    @Inject(method = "canEat", at = @At("RETURN"), cancellable = true)
    private void feast$room(boolean always, CallbackInfoReturnable<Boolean> call) {
        if (call.getReturnValueZ()) {
            return;
        }
        FoodData food = ((Player) (Object) this).getFoodData();
        if (SaturationRoom.room(food.getFoodLevel(), food.getSaturationLevel())) {
            call.setReturnValue(true);
        }
    }
}
