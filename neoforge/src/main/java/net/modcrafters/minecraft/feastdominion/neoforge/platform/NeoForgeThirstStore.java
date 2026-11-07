package net.modcrafters.minecraft.feastdominion.neoforge.platform;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.services.ThirstStore;
import net.modcrafters.minecraft.feastdominion.thirst.Thirst;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class NeoForgeThirstStore implements ThirstStore {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES,
            Constants.MOD_ID
    );
    public static final Supplier<AttachmentType<ThirstLevel>> THIRST = ATTACHMENTS.register(
            "thirst",
            () -> AttachmentType.builder(() -> new ThirstLevel(Thirst.POINTS))
                    //? if >=1.21.6 {
                    .serialize(ThirstLevel.MAP_CODEC)
                    //?} else
                    //.serialize(ThirstLevel.CODEC)
                    .sync(ThirstLevel.STREAM_CODEC)
                    .build()
    );

    @Override
    public ThirstLevel level(Player player) {
        return player.getData(THIRST.get());
    }

    @Override
    public void setLevel(Player player, ThirstLevel level) {
        player.setData(THIRST.get(), level);
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
