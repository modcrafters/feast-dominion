package net.modcrafters.minecraft.feastdominion.thirst;

import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Applies the thirst number, and its quench, when a food or drink is finished. */
public final class ThirstFood {
    private ThirstFood() {
    }

    public static void eat(Player player, ItemStack stack, InteractionHand hand) {
        if (stack.isEmpty() || player.isCreative() || player.isSpectator() || player.level().getServer() == null) {
            return;
        }
        int thirst;
        float quench;
        if (WaterDrink.is(stack)) {
            thirst = WaterDrink.thirst(stack);
            quench = WaterDrink.quench(stack);
        } else {
            ThirstIndex.ensure(player.level().getServer().getResourceManager());
            String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            thirst = ThirstIndex.points(id);
            quench = ThirstIndex.quench(id);
        }
        if (thirst == 0 && quench <= 0.0F) {
            return;
        }
        if (WaterDrink.held(stack) && !WaterDrink.finish(player, hand, stack)) {
            return;
        }
        apply(player, thirst, quench);
    }

    public static void apply(Player player, int thirst, float quench) {
        ThirstLevel level = Services.THIRST.level(player);
        int points = Thirst.changed(level.points(), thirst);
        float reserve = Quench.clamp(points, level.quench() + quench);
        Services.THIRST.setLevel(player, new ThirstLevel(points, level.drain(), reserve));
    }
}
