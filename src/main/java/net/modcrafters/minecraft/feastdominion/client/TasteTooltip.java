package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.taste.Lean;
import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.taste.TasteCatalog;
import net.modcrafters.minecraft.feastdominion.taste.TasteProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

/** Taste names, then whether this meal meets the player's lean. */
public final class TasteTooltip {
    /** Sage, quiet on the dark tooltip. */
    private static final int GOOD = 0x9BB89A;
    /** Dusty rose, the same weight as the sage. */
    private static final int BAD = 0xD4A3A3;

    private TasteTooltip() {
    }

    public static void add(ItemStack stack, Player player, List<Component> lines) {
        TasteProfile profile = TasteCatalog.of(stack);
        MutableComponent line = null;
        for (Taste taste : profile.shown()) {
            MutableComponent name = Component.translatable("feast_dominion.taste." + taste.name().toLowerCase(Locale.ROOT));
            line = line == null ? name : line.append(Component.literal(", ")).append(name);
        }
        if (profile.poisonous()) {
            MutableComponent name = Component.translatable("feast_dominion.taste.poisonous");
            line = line == null ? name : line.append(Component.literal(", ")).append(name);
        }
        if (line != null) {
            lines.add(line.withStyle(ChatFormatting.GRAY));
        }
        if (player == null || stack.get(DataComponents.FOOD) == null) {
            return;
        }
        Lean.Effect effect = Lean.effect(Services.LEAN.taste(player), profile, FeastConfig.leanMissPercent());
        if (effect == null) {
            return;
        }
        String key = effect.matches() ? "feast_dominion.taste.effect" : "feast_dominion.taste.effect.miss";
        lines.add(Component.translatable(
                key,
                Component.translatable("feast_dominion.taste." + effect.taste().name().toLowerCase(Locale.ROOT)),
                Lean.signed(effect.percent())
        ).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(effect.matches() ? GOOD : BAD))));
    }
}
