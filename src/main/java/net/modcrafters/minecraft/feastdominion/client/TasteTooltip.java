package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.taste.TasteCatalog;
import net.modcrafters.minecraft.feastdominion.taste.TasteProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

/** One line naming the tastes strong enough to notice. */
public final class TasteTooltip {
    private TasteTooltip() {
    }

    public static void add(ItemStack stack, List<Component> lines) {
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
    }
}
