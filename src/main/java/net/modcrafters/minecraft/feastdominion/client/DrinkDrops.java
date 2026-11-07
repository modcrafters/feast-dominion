package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.thirst.Drink;
import net.modcrafters.minecraft.feastdominion.thirst.Drinking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Splashes where the hand meets the water, then a few drops at the mouth. */
public final class DrinkDrops {
    private DrinkDrops() {
    }

    public static void spawn(LocalPlayer player) {
        int remaining = player.getUseItemRemainingTicks();
        if (remaining <= 0) {
            return;
        }
        float progress = Drink.held(0.0F, remaining);
        Level level = player.level();
        RandomSource random = player.getRandom();
        if (progress < 0.62F) {
            if (player.tickCount % 2 != 0) {
                return;
            }
            BlockPos water = Drinking.water(player);
            double x;
            double y;
            double z;
            if (water != null) {
                x = water.getX() + 0.5;
                y = water.getY() + 0.95;
                z = water.getZ() + 0.5;
            } else {
                Vec3 at = player.getEyePosition().add(player.getLookAngle().scale(0.9)).add(0.0, -0.45, 0.0);
                x = at.x;
                y = at.y;
                z = at.z;
            }
            splash(level, random, x, y, z, 2);
            return;
        }
        if (player.tickCount % 2 != 0) {
            return;
        }
        Vec3 mouth = player.getEyePosition().add(player.getLookAngle().scale(0.35)).add(0.0, -0.18, 0.0);
        level.addParticle(ParticleTypes.SPLASH, mouth.x, mouth.y, mouth.z, 0.0, 0.02, 0.0);
        level.addParticle(
                ParticleTypes.DRIPPING_WATER,
                mouth.x + (random.nextDouble() - 0.5) * 0.1,
                mouth.y,
                mouth.z + (random.nextDouble() - 0.5) * 0.1,
                0.0,
                0.0,
                0.0
        );
    }

    private static void splash(Level level, RandomSource random, double x, double y, double z, int count) {
        for (int i = 0; i < count; i++) {
            level.addParticle(
                    ParticleTypes.SPLASH,
                    x + (random.nextDouble() - 0.5) * 0.4,
                    y,
                    z + (random.nextDouble() - 0.5) * 0.4,
                    (random.nextDouble() - 0.5) * 0.08,
                    random.nextDouble() * 0.12,
                    (random.nextDouble() - 0.5) * 0.08
            );
        }
    }
}
