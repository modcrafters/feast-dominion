package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.group.GroupCatalog;
import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

/** The group lines under a food's name. */
public final class GroupTooltip {
    private GroupTooltip() {
    }

    public static void add(ItemStack stack, List<Component> lines) {
        GroupShares shares = GroupCatalog.of(stack);
        for (FoodGroup group : FoodGroup.values()) {
            int percent = shares.percent(group);
            if (percent <= 0) {
                continue;
            }
            lines.add(Component.translatable(
                    "feast_dominion.group.share",
                    Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase(Locale.ROOT)),
                    percent
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
