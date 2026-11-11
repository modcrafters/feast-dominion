package net.modcrafters.minecraft.feastdominion.compat;

import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

/** Farmer's Delight paints its own hunger icons while Nourishment is active. */
public final class FarmersDelight {
    public static final String MOD_ID = "farmersdelight";
    public static final Identifier NOURISHMENT_LAYER = Identifier.fromNamespaceAndPath(MOD_ID, "nourishment");
    private static final Identifier NOURISHMENT = NOURISHMENT_LAYER;

    private FarmersDelight() {
    }

    public static boolean loaded() {
        return Services.PLATFORM.modLoaded(MOD_ID);
    }

    public static boolean nourishment(Player player) {
        //? if >=26.1 {
        return BuiltInRegistries.MOB_EFFECT.get(NOURISHMENT).filter(player::hasEffect).isPresent();
        //?} else {
        /*return BuiltInRegistries.MOB_EFFECT.getHolder(NOURISHMENT).filter(player::hasEffect).isPresent();
        *///?}
    }
}
