package net.modcrafters.minecraft.feastdominion.thirst;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class WaterSources {
    /** Biomes whose water is salt. Everything else is fresh. */
    public static final TagKey<Biome> SALT_WATER = TagKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "salt_water")
    );

    private WaterSources() {
    }

    public static boolean salty(Holder<Biome> biome) {
        return biome.is(SALT_WATER);
    }
}
