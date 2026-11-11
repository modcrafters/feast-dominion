package net.modcrafters.minecraft.feastdominion.platform.services;

import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.minecraft.world.entity.player.Player;

/** The seven group counts, saved with the player and copied to that player's client. */
public interface GroupStore {
    DietMemory memory(Player player);

    void setMemory(Player player, DietMemory memory);
}
