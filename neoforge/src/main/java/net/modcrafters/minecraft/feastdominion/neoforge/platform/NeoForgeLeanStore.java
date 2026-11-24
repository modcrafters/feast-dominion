package net.modcrafters.minecraft.feastdominion.neoforge.platform;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.services.LeanStore;
import net.modcrafters.minecraft.feastdominion.neoforge.network.LeanPayload;
import net.modcrafters.minecraft.feastdominion.taste.PlayerLean;
import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class NeoForgeLeanStore implements LeanStore {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES,
            Constants.MOD_ID
    );
    public static final Supplier<AttachmentType<PlayerLean>> LEAN = ATTACHMENTS.register(
            "lean",
            () -> AttachmentType.builder(PlayerLean::unset)
                    //? if >=1.21.6 {
                    .serialize(PlayerLean.MAP_CODEC)
                    //?} else
                    //.serialize(PlayerLean.CODEC)
                    //? if >=1.21 {
                    .sync(PlayerLean.STREAM_CODEC)
                    //?}
                    .copyOnDeath()
                    .build()
    );

    @Override
    public Taste taste(Player player) {
        return player.getData(LEAN.get()).taste();
    }

    @Override
    public void set(Player player, Taste taste) {
        PlayerLean lean = PlayerLean.of(taste);
        if (lean.equals(player.getData(LEAN.get()))) {
            return;
        }
        player.setData(LEAN.get(), lean);
        //? if <1.21 {
        /*if (player instanceof ServerPlayer server) {
            PacketDistributor.sendToPlayer(server, new LeanPayload(lean));
        }
        *///?}
    }
}
