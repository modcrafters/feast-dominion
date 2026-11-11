package net.modcrafters.minecraft.feastdominion.neoforge.platform;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.services.GroupStore;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class NeoForgeGroupStore implements GroupStore {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES,
            Constants.MOD_ID
    );
    public static final Supplier<AttachmentType<DietMemory>> GROUPS = ATTACHMENTS.register(
            "groups",
            () -> AttachmentType.builder(DietMemory::start)
                    //? if >=1.21.6 {
                    .serialize(DietMemory.MAP_CODEC)
                    //?} else
                    //.serialize(DietMemory.CODEC)
                    .sync(DietMemory.STREAM_CODEC)
                    .build()
    );

    @Override
    public DietMemory memory(Player player) {
        return player.getData(GROUPS.get());
    }

    @Override
    public void setMemory(Player player, DietMemory memory) {
        player.setData(GROUPS.get(), memory);
    }
}
