package net.modcrafters.minecraft.feastdominion.platform.services;

import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.minecraft.world.entity.player.Player;

/** Server lean, copied to the owning client so the wellbeing book can name it. */
public interface LeanStore {
    Taste taste(Player player);

    void set(Player player, Taste taste);
}
