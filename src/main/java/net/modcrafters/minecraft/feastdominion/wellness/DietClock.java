package net.modcrafters.minecraft.feastdominion.wellness;

import net.modcrafters.minecraft.feastdominion.group.GroupIndex;
import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

/** Eating adds the meal. Older meals leave once the recent diet is full. */
public final class DietClock {
    private DietClock() {
    }

    public static void eat(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty() || player.level().getServer() == null) {
            return;
        }
        GroupIndex.ensure(player.level().getServer().getResourceManager());
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        GroupShares shares = GroupIndex.shares(id);
        if (!shares.any()) {
            return;
        }
        FoodProperties food = stack.get(DataComponents.FOOD);
        int nutrition = food == null ? 0 : food.nutrition();
        DietMemory saved = Services.GROUPS.memory(player);
        Services.GROUPS.setMemory(player, new DietMemory(DietLevels.remember(saved.meals(), shares, DietLevels.serving(nutrition))));
    }

    public static void tick(ServerPlayer player) {
    }

    public static void save(ServerPlayer player) {
    }
}
