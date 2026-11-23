package net.modcrafters.minecraft.feastdominion.neoforge.network;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Recent meals for the client. 1.20.6 attachments do not sync themselves. */
public record GroupPayload(DietMemory memory) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GroupPayload> TYPE = new CustomPacketPayload.Type<>(
            FeastIds.of("groups")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, GroupPayload> STREAM_CODEC = StreamCodec.composite(
            DietMemory.STREAM_CODEC,
            GroupPayload::memory,
            GroupPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
