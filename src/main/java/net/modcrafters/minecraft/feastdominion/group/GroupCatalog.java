package net.modcrafters.minecraft.feastdominion.group;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

/** The groups for one item, read from the singleplayer world's data packs. */
public final class GroupCatalog {
    private GroupCatalog() {
    }

    public static GroupShares of(ItemStack stack) {
        GroupIndex.ensure(loadedData());
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return GroupIndex.shares(id);
    }

    /** Group files, recipes, and item tags are data packs. The client resource manager only has assets. */
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
