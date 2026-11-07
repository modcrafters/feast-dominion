package net.modcrafters.minecraft.feastdominion.thirst;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/** Editable lists. A pack adds or removes biomes. A biome on both lists is dry. */
public final class ThirstClimates {
    public static final TagKey<Biome> WET = TagKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "wet")
    );
    public static final TagKey<Biome> DRY = TagKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "dry")
    );

    private ThirstClimates() {
    }

    public static ThirstDrain.Climate of(Holder<Biome> biome) {
        return ThirstDrain.climate(biome.is(WET), biome.is(DRY));
    }
}
