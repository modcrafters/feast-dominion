package net.modcrafters.minecraft.feastdominion.taste;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** The lean saved on a player. A missing value has not been rolled yet. */
public record PlayerLean(int ordinal) {
    public static final int UNSET = -1;

    public PlayerLean {
        if (ordinal < 0 || ordinal >= Taste.values().length) {
            ordinal = UNSET;
        }
    }

    public static PlayerLean unset() {
        return new PlayerLean(UNSET);
    }

    public static PlayerLean of(Taste taste) {
        return taste == null ? unset() : new PlayerLean(taste.ordinal());
    }

    public Taste taste() {
        return this.ordinal == UNSET ? null : Taste.values()[this.ordinal];
    }

    public static final Codec<PlayerLean> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("taste", UNSET).forGetter(PlayerLean::ordinal)
    ).apply(instance, PlayerLean::new));
    public static final MapCodec<PlayerLean> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("taste", UNSET).forGetter(PlayerLean::ordinal)
    ).apply(instance, PlayerLean::new));
    public static final StreamCodec<ByteBuf, PlayerLean> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            PlayerLean::ordinal,
            PlayerLean::new
    );
}
