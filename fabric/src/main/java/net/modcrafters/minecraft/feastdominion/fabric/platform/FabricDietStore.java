package net.modcrafters.minecraft.feastdominion.fabric.platform;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.feature.FoodLog;
import net.modcrafters.minecraft.feastdominion.platform.services.DietStore;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class FabricDietStore implements DietStore {
    public static final AttachmentType<FoodLog> DIET = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "diet"),
            builder -> builder.initializer(() -> FoodLog.EMPTY).persistent(FoodLog.CODEC).copyOnDeath()
    );

    @Override
    public FoodLog log(Player player) {
        FoodLog log = player.getAttached(DIET);
        return log == null ? FoodLog.EMPTY : log;
    }

    @Override
    public void setLog(Player player, FoodLog log) {
        player.setAttached(DIET, log);
    }
}
