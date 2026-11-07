package net.modcrafters.minecraft.feastdominion.platform.services;

import net.modcrafters.minecraft.feastdominion.thirst.ThirstLevel;
import net.minecraft.world.entity.player.Player;

/** Server thirst, copied to the owning client so the bar can draw it. */
public interface ThirstStore {
    ThirstLevel level(Player player);

    void setLevel(Player player, ThirstLevel level);

    int points(Player player);

    void setPoints(Player player, int points);
}
