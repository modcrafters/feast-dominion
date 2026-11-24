package net.modcrafters.minecraft.feastdominion;

import net.minecraft.resources.Identifier;

/** Ids for this mod. Before 1.21, Identifier is built with a constructor. */
public final class FeastIds {
    private FeastIds() {
    }

    public static Identifier of(String path) {
        return of(Constants.MOD_ID, path);
    }

    public static Identifier of(String namespace, String path) {
        //? if >=1.21 {
        return Identifier.fromNamespaceAndPath(namespace, path);
        //?} else {
        /*return new Identifier(namespace, path);
        *///?}
    }
}
