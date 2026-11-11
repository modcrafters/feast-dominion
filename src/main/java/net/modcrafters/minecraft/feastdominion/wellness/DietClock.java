package net.modcrafters.minecraft.feastdominion.wellness;

import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.PeacefulRules;
import net.modcrafters.minecraft.feastdominion.group.GroupIndex;
import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.group.GroupWeight;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server tick. Eating adds the meal. Time takes the counts back down. */
public final class DietClock {
    private static final Map<UUID, Integer> DRAIN = new HashMap<>();

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
        int weight = GroupWeight.of(nutrition, true);
        DietMemory saved = Services.GROUPS.memory(player);
        int drain = DRAIN.getOrDefault(player.getUUID(), saved.drain());
        Services.GROUPS.setMemory(player, new DietMemory(DietLevels.eat(saved.points(), shares, weight), drain));
    }

    public static void tick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (!PeacefulRules.thirstDrains(
                FeastConfig.drainInPeaceful(),
                player.level().getDifficulty() == Difficulty.PEACEFUL
        )) {
            return;
        }
        DietMemory saved = Services.GROUPS.memory(player);
        int drain = DRAIN.computeIfAbsent(player.getUUID(), id -> saved.drain());
        DietLevels.Step step = DietLevels.tick(saved.points(), drain);
        DRAIN.put(player.getUUID(), step.drain());
        if (changed(saved.points(), step.points())) {
            Services.GROUPS.setMemory(player, new DietMemory(step.points(), step.drain()));
        }
    }

    public static void save(ServerPlayer player) {
        Integer drain = DRAIN.remove(player.getUUID());
        if (drain == null) {
            return;
        }
        DietMemory saved = Services.GROUPS.memory(player);
        if (saved.drain() != drain) {
            Services.GROUPS.setMemory(player, new DietMemory(saved.points(), drain));
        }
    }

    private static boolean changed(int[] before, int[] after) {
        if (before.length != after.length) {
            return true;
        }
        for (int i = 0; i < before.length; i++) {
            if (before[i] != after[i]) {
                return true;
            }
        }
        return false;
    }
}
