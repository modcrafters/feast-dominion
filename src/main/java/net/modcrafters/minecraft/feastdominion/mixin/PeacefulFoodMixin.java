package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.PeacefulRules;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=26.1 {
import net.minecraft.server.level.ServerPlayer;
//?}

/** Hunger can fall in peaceful when the config says the bars keep spending. */
@Mixin(FoodData.class)
public abstract class PeacefulFoodMixin {
    @Unique
    private boolean feast$peaceful;
    @Unique
    private float feast$exhaustionBefore;
    @Unique
    private float feast$saturationBefore;
    @Unique
    private int feast$foodBefore;

    @Accessor("exhaustionLevel")
    abstract float feast$exhaustionLevel();

    //? if >=26.1 {
    @Inject(method = "tick", at = @At("HEAD"))
    private void feast$before(ServerPlayer player, CallbackInfo ci) {
        this.feast$remember(player);
    }
    //?} else {
    /*@Inject(method = "tick", at = @At("HEAD"))
    private void feast$before(Player player, CallbackInfo ci) {
        this.feast$remember(player);
    }
    *///?}

    @Inject(method = "tick", at = @At("RETURN"))
    private void feast$hunger(CallbackInfo ci) {
        FoodData food = (FoodData) (Object) this;
        if (PeacefulRules.hungerDropped(
                FeastConfig.drainInPeaceful(),
                this.feast$peaceful,
                this.feast$exhaustionBefore,
                this.feast$saturationBefore,
                this.feast$foodBefore,
                food.getFoodLevel()
        )) {
            food.setFoodLevel(this.feast$foodBefore - 1);
        }
    }

    @Unique
    private void feast$remember(Player player) {
        FoodData food = (FoodData) (Object) this;
        this.feast$peaceful = player.level().getDifficulty() == Difficulty.PEACEFUL;
        this.feast$exhaustionBefore = this.feast$exhaustionLevel();
        this.feast$saturationBefore = food.getSaturationLevel();
        this.feast$foodBefore = food.getFoodLevel();
    }
}
