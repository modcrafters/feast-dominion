package net.modcrafters.minecraft.feastdominion.fabric.platform;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.services.GroupStore;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class FabricGroupStore implements GroupStore {
    public static final AttachmentType<DietMemory> GROUPS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "groups"),
            builder -> builder.initializer(DietMemory::start)
                    .persistent(DietMemory.CODEC)
                    .syncWith(DietMemory.STREAM_CODEC, AttachmentSyncPredicate.all())
    );

    @Override
    public DietMemory memory(Player player) {
        DietMemory memory = player.getAttached(GROUPS);
        return memory == null ? DietMemory.start() : memory;
    }

    @Override
    public void setMemory(Player player, DietMemory memory) {
        player.setAttached(GROUPS, memory);
    }
}
