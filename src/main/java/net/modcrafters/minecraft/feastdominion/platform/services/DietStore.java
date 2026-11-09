package net.modcrafters.minecraft.feastdominion.platform.services;

import net.modcrafters.minecraft.feastdominion.feature.FoodLog;
import net.minecraft.world.entity.player.Player;

/** Foods and drinks the player has consumed, saved with the player. */
public interface DietStore {
    FoodLog log(Player player);

    void setLog(Player player, FoodLog log);
}
