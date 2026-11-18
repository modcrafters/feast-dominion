package net.modcrafters.minecraft.feastdominion.network;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.feature.VarietyView;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/** The three windows, the hearts they are worth, and the weights, for the local player. */
public record VarietyPayload(VarietyView.Snapshot snapshot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VarietyPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "variety")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, VarietyPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                VarietyView.Snapshot snapshot = payload.snapshot();
                buf.writeVarInt(snapshot.today());
                buf.writeVarInt(snapshot.week());
                buf.writeVarInt(snapshot.month());
                buf.writeDouble(snapshot.hearts());
                buf.writeVarInt(snapshot.dayWeight());
                buf.writeVarInt(snapshot.weekWeight());
                buf.writeVarInt(snapshot.monthWeight());
                writeItems(buf, snapshot.todayItems());
                writeItems(buf, snapshot.weekItems());
                writeItems(buf, snapshot.monthItems());
                buf.writeVarInt(snapshot.tasteMarks());
                buf.writeVarInt(snapshot.tasteMask());
            },
            buf -> new VarietyPayload(new VarietyView.Snapshot(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readDouble(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    readItems(buf),
                    readItems(buf),
                    readItems(buf),
                    buf.readVarInt(),
                    buf.readVarInt()
            ))
    );

    private static void writeItems(RegistryFriendlyByteBuf buf, List<String> items) {
        buf.writeVarInt(items.size());
        for (String item : items) {
            buf.writeUtf(item);
        }
    }

    private static List<String> readItems(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<String> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add(buf.readUtf());
        }
        return List.copyOf(items);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
