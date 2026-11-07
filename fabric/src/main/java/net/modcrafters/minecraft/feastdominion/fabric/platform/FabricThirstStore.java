package net.modcrafters.minecraft.feastdominion.fabric.platform;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.services.ThirstStore;
import net.modcrafters.minecraft.feastdominion.thirst.Thirst;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class FabricThirstStore implements ThirstStore {
    public static final AttachmentType<ThirstLevel> THIRST = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "thirst"),
            builder -> builder.initializer(() -> new ThirstLevel(Thirst.POINTS))
                    .persistent(ThirstLevel.CODEC)
                    .syncWith(ThirstLevel.STREAM_CODEC, AttachmentSyncPredicate.all())
    );

    @Override
    public ThirstLevel level(Player player) {
        ThirstLevel level = player.getAttached(THIRST);
        return level == null ? new ThirstLevel(Thirst.POINTS) : level;
    }

    @Override
    public void setLevel(Player player, ThirstLevel level) {
        player.setAttached(THIRST, level);
    }

    @Override
    public int points(Player player) {
        return level(player).points();
    }

    @Override
    public void setPoints(Player player, int points) {
        setLevel(player, new ThirstLevel(points, level(player).drain()));
    }
}
