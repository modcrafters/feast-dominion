package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

/**
 * Max health from recent foods and drinks. Spice of Life: Carrot Edition replaces this entirely.
 */
public final class HealthIncreaseFeature extends ModDependantFeature {
    public static final String MOD_ID = "solcarrot";
    public static final Identifier MODIFIER_ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "health_increase");
    public static final TagKey<Item> DRINKS = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "health_drinks")
    );
    /** Vanilla max health, in health points. Two points are one heart. */
    private static final double VANILLA_HEALTH = 20;

    public static HealthIncreaseFeature instance() {
        return Holder.INSTANCE;
    }

    private HealthIncreaseFeature() {
        super(MOD_ID, false);
    }

    HealthIncreaseFeature(BooleanSupplier solLoaded) {
        super(MOD_ID, solLoaded, false);
    }

    @Override
    protected void onEnable() {
        Constants.LOG.info("Health increase is on.");
    }

    @Override
    protected void onDisable() {
        Constants.LOG.warn(
                "Spice of Life: Carrot Edition is installed, so Feast Dominion leaves max health alone."
        );
    }

    public static boolean counts(ItemStack stack) {
        return !stack.isEmpty() && (stack.has(DataComponents.FOOD) || stack.is(DRINKS));
    }

    public void consume(ServerPlayer player, ItemStack stack) {
        if (!enabled() || !counts(stack)) {
            return;
        }
        String item = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        FoodLog log = Services.DIET.log(player).remember(item, day(player));
        Services.DIET.setLog(player, log);
        apply(player);
    }

    /** Writes the hearts onto the player, or takes the modifier off when Spice of Life owns the bar. */
    public void apply(Player player) {
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health == null) {
            return;
        }
        if (!enabled() || !(player instanceof ServerPlayer server)) {
            health.removeModifier(MODIFIER_ID);
            return;
        }
        VarietyView.Snapshot snapshot = snapshot(server);
        double points = snapshot.hearts() * 2.0 - VANILLA_HEALTH;
        if (points == 0.0) {
            health.removeModifier(MODIFIER_ID);
        } else {
            health.addOrUpdateTransientModifier(new AttributeModifier(
                    MODIFIER_ID,
                    points,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
        player.setHealth(player.getHealth());
        VarietySync.send(server, snapshot);
    }

    public VarietyView.Snapshot snapshot(ServerPlayer player) {
        HealthSettings settings = FeastConfig.health();
        Variety.Counts counts = Variety.counts(Services.DIET.log(player), day(player));
        return VarietyView.Snapshot.of(counts, settings);
    }

    static long day(ServerPlayer player) {
        //? if >=26.1 {
        return player.level().getOverworldClockTime() / 24000L;
        //?} else {
        /*return ((ServerLevel) player.level()).getServer().overworld().getDayTime() / 24000L;
        *///?}
    }

    private static final class Holder {
        static final HealthIncreaseFeature INSTANCE = new HealthIncreaseFeature();
    }
}
