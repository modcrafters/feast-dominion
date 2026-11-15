package net.modcrafters.minecraft.feastdominion.fabric.platform;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.services.LeanStore;
import net.modcrafters.minecraft.feastdominion.taste.PlayerLean;
import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class FabricLeanStore implements LeanStore {
    public static final AttachmentType<PlayerLean> LEAN = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "lean"),
            builder -> builder.initializer(PlayerLean::unset)
                    .persistent(PlayerLean.CODEC)
                    .copyOnDeath()
                    .syncWith(PlayerLean.STREAM_CODEC, AttachmentSyncPredicate.all())
    );

    @Override
    public Taste taste(Player player) {
        PlayerLean lean = player.getAttached(LEAN);
        return lean == null ? null : lean.taste();
    }

    @Override
    public void set(Player player, Taste taste) {
        player.setAttached(LEAN, PlayerLean.of(taste));
    }
}
