package net.modcrafters.minecraft.feastdominion.thirst;

import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Empty hand, hold right-click on the face that has a water block beside it. */
public final class Drinking {
    private static final double REACH_SQUARED = 36.0;
    private static final Map<UUID, BlockPos> CLIENT_PENDING = new HashMap<>();
    private static final Map<UUID, BlockPos> SERVER_PENDING = new HashMap<>();
    private static final Map<UUID, BlockPos> CLIENT_ACTIVE = new HashMap<>();
    private static final Map<UUID, BlockPos> SERVER_ACTIVE = new HashMap<>();

    private Drinking() {
    }

    public static InteractionResult interact(Player player, Level level, InteractionHand hand, BlockPos pos, Direction face) {
        BlockPos water = pos.relative(face);
        Drink.Decision decision = Drink.decide(
                player.getItemInHand(hand).isEmpty(),
                level.getBlockState(water).is(Blocks.WATER),
                !player.isCreative() && !player.isSpectator(),
                player.isUsingItem()
        );
        return switch (decision) {
            case PASS -> InteractionResult.PASS;
            case WAITING -> InteractionResult.CONSUME;
            case DRINK -> {
                pending(player).put(player.getUUID(), water.immutable());
                player.startUsingItem(hand);
                yield InteractionResult.CONSUME;
            }
        };
    }

    /** True when this use was armed by {@link #interact}. Moves it into the active sip. */
    public static boolean claim(Player player) {
        BlockPos water = pending(player).remove(player.getUUID());
        if (water == null) {
            return false;
        }
        active(player).put(player.getUUID(), water);
        return true;
    }

    public static BlockPos water(Player player) {
        return active(player).get(player.getUUID());
    }

    public static void drop(Player player) {
        pending(player).remove(player.getUUID());
        active(player).remove(player.getUUID());
    }

    /** A swallow along the way. The finished sip has its own sound. */
    public static void gulp(Player player) {
        if (player.level().isClientSide() || !active(player).containsKey(player.getUUID())) {
            return;
        }
        int remaining = player.getUseItemRemainingTicks();
        if (remaining > 0 && remaining % 10 == 0) {
            play(player, 0.35F, 0.9F + player.getRandom().nextFloat() * 0.15F);
        }
    }

    /**
     * The hold finished. Thirst changes when that water is still there and still in reach.
     * Always ends the use, so an empty hand cannot stay stuck drinking.
     */
    public static boolean finish(Player player) {
        BlockPos water = active(player).remove(player.getUUID());
        if (water == null) {
            return false;
        }
        if (player.level().getBlockState(water).is(Blocks.WATER)
                && player.distanceToSqr(Vec3.atCenterOf(water)) <= REACH_SQUARED) {
            boolean salty = WaterSources.salty(player.level().getBiome(water));
            Services.THIRST.setPoints(player, Drink.after(Services.THIRST.points(player), salty));
            play(player, 0.5F, 1.0F);
        }
        player.stopUsingItem();
        return true;
    }

    private static Map<UUID, BlockPos> pending(Player player) {
        return player.level().isClientSide() ? CLIENT_PENDING : SERVER_PENDING;
    }

    private static Map<UUID, BlockPos> active(Player player) {
        return player.level().isClientSide() ? CLIENT_ACTIVE : SERVER_ACTIVE;
    }

    private static void play(Player player, float volume, float pitch) {
        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.GENERIC_DRINK,
                SoundSource.PLAYERS,
                volume,
                pitch
        );
    }
}
