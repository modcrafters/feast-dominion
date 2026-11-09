package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.PeacefulRules;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if >=26.1 {
import net.minecraft.server.level.ServerPlayer;
//?} else {
/*import net.minecraft.world.entity.player.Player;
*///?}

/** Peaceful fills hunger and saturation back up. The config can leave them to fall. */
//? if >=26.1 {
@Mixin(ServerPlayer.class)
public class PeacefulFillMixin {
    @Redirect(method = "tickRegeneration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;setFoodLevel(I)V"))
    private void feast$food(FoodData food, int level) {
        if (PeacefulRules.fillHunger(FeastConfig.drainInPeaceful())) {
            food.setFoodLevel(level);
        }
    }

    @Redirect(method = "tickRegeneration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;setSaturation(F)V"))
    private void feast$saturation(FoodData food, float saturation) {
        if (PeacefulRules.fillHunger(FeastConfig.drainInPeaceful())) {
            food.setSaturation(saturation);
        }
    }

    @Redirect(method = "tickRegeneration", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V"))
    private void feast$heal(ServerPlayer player, float amount) {
        if (PeacefulRules.heal(FeastConfig.drainInPeaceful(), player.getFoodData().getFoodLevel())) {
            player.heal(amount);
        }
    }
}
//?} else {
/*@Mixin(Player.class)
public class PeacefulFillMixin {
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;setFoodLevel(I)V"))
    private void feast$food(FoodData food, int level) {
        if (PeacefulRules.fillHunger(FeastConfig.drainInPeaceful())) {
            food.setFoodLevel(level);
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;setSaturation(F)V"))
    private void feast$saturation(FoodData food, float saturation) {
        if (PeacefulRules.fillHunger(FeastConfig.drainInPeaceful())) {
            food.setSaturation(saturation);
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    private void feast$heal(Player player, float amount) {
        if (PeacefulRules.heal(FeastConfig.drainInPeaceful(), player.getFoodData().getFoodLevel())) {
            player.heal(amount);
        }
    }
}
*///?}
