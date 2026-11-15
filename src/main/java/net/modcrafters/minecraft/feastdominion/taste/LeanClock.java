package net.modcrafters.minecraft.feastdominion.taste;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Rolls a lean once, adds the matching fill, and rolls again when a meal or a death asks. */
public final class LeanClock {
    public static final TagKey<Item> RESETS = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "resets_lean")
    );

    private LeanClock() {
    }

    public static void ensure(ServerPlayer player) {
        if (Services.LEAN.taste(player) != null) {
            return;
        }
        Services.LEAN.set(player, Lean.pick(player.getRandom().nextInt(Taste.values().length)));
    }

    /** The new body keeps the old lean, or rolls a different one when death is allowed to. */
    public static void respawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean died) {
        Taste current = Services.LEAN.taste(oldPlayer);
        if (current == null) {
            ensure(newPlayer);
            return;
        }
        int index = newPlayer.getRandom().nextInt(Taste.values().length - 1);
        Services.LEAN.set(newPlayer, Lean.carried(current, died, FeastConfig.resetLeanOnDeath(), index));
    }

    public static void eat(ServerPlayer player, ItemStack stack, int hungerBefore, float saturationBefore) {
        if (stack.isEmpty() || player.level().getServer() == null) {
            return;
        }
        ensure(player);
        TasteIndex.ensure(player.level().getServer().getResourceManager());
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        Taste lean = Services.LEAN.taste(player);
        TasteProfile profile = TasteIndex.profile(id);
        FoodProperties food = stack.get(DataComponents.FOOD);
        Lean.Effect effect = Lean.effect(lean, profile, FeastConfig.leanMissPercent());
        int yield = Lean.yield(effect);
        if (food != null && yield != 100) {
            FoodData data = player.getFoodData();
            int hunger = Lean.hungerAfter(hungerBefore, food.nutrition(), yield);
            data.setFoodLevel(hunger);
            data.setSaturation(Lean.saturationAfter(hunger, saturationBefore, food.saturation(), yield));
        }
        Taste next = Lean.afterMeal(lean, stack.is(RESETS), player.getRandom().nextInt(Taste.values().length - 1));
        if (next != null && next != lean) {
            Services.LEAN.set(player, next);
        }
    }
}
