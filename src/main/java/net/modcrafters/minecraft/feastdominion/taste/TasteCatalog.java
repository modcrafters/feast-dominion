package net.modcrafters.minecraft.feastdominion.taste;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

/** The tastes for one item, read from the singleplayer world's data packs. */
public final class TasteCatalog {
    private TasteCatalog() {
    }

    public static TasteProfile of(ItemStack stack) {
        TasteIndex.ensure(loadedData());
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return TasteIndex.profile(id);
    }

    private static ResourceManager loadedData() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return null;
        }
        try {
            return server.getResourceManager();
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
