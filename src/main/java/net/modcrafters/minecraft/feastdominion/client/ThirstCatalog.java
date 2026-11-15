package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.thirst.ThirstIndex;
import net.modcrafters.minecraft.feastdominion.thirst.WaterDrink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

/** The thirst number for one item, read from the singleplayer world's data packs. */
public final class ThirstCatalog {
    private ThirstCatalog() {
    }

    public static int points(ItemStack stack) {
        if (WaterDrink.is(stack)) {
            return WaterDrink.thirst(stack);
        }
        ThirstIndex.ensure(loadedData());
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return ThirstIndex.points(id);
    }

    public static float quench(ItemStack stack) {
        if (WaterDrink.is(stack)) {
            return WaterDrink.quench(stack);
        }
        ThirstIndex.ensure(loadedData());
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return ThirstIndex.quench(id);
    }

    /** Item files are data packs. The client resource manager only has assets. */
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
