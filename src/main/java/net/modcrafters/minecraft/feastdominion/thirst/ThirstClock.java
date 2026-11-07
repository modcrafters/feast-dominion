package net.modcrafters.minecraft.feastdominion.thirst;

import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server tick. The partial point stays on the server until a droplet is lost or the player leaves. */
public final class ThirstClock {
    private static final Map<UUID, Integer> DRAIN = new HashMap<>();
    private static final int WEAKNESS_TICKS = 80;

    private ThirstClock() {
    }

    public static void tick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        ThirstLevel saved = Services.THIRST.level(player);
        int drain = DRAIN.computeIfAbsent(player.getUUID(), id -> saved.drain());
        ThirstDrain.Climate climate = ThirstClimates.of(player.level().getBiome(player.blockPosition()));
        ThirstDrain.Step step = ThirstDrain.tick(saved.points(), drain, climate);
        DRAIN.put(player.getUUID(), step.drain());
        if (step.points() != saved.points()) {
            Services.THIRST.setLevel(player, new ThirstLevel(step.points(), step.drain()));
        }
        if (step.points() == 0) {
            weaken(player);
        }
    }

    public static void save(ServerPlayer player) {
        Integer drain = DRAIN.remove(player.getUUID());
        if (drain == null) {
            return;
        }
        ThirstLevel saved = Services.THIRST.level(player);
        if (saved.drain() != drain) {
            Services.THIRST.setLevel(player, new ThirstLevel(saved.points(), drain));
        }
    }

    private static void weaken(ServerPlayer player) {
        MobEffectInstance current = player.getEffect(MobEffects.WEAKNESS);
        if (current != null && current.getAmplifier() > 0) {
            return;
        }
        if (current != null && current.getDuration() > 20) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_TICKS, 0, false, true, true));
    }
}
