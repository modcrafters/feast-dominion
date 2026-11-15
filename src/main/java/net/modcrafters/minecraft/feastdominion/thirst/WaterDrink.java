package net.modcrafters.minecraft.feastdominion.thirst;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** A water bottle drinks as usual. A water bucket drinks in the air, or while sneaking, and leaves an empty bucket. */
public final class WaterDrink {
    /** One drink on the bar. A bottle and a bucket both restore this much thirst. */
    public static final int THIRST = Drink.AMOUNT;
    /** A bottle's reserve matches the thirst it restores. */
    public static final float BOTTLE_QUENCH = Drink.AMOUNT;
    /** A bucket is three bottles, so the reserve is three times a bottle's. */
    public static final float BUCKET_QUENCH = Drink.AMOUNT * 3.0F;

    private WaterDrink() {
    }

    public static boolean bucket(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET);
    }

    public static boolean bottle(ItemStack stack) {
        if (!stack.is(Items.POTION)) {
            return false;
        }
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(Potions.WATER);
    }

    public static boolean is(ItemStack stack) {
        return bucket(stack) || bottle(stack);
    }

    public static int thirst(ItemStack stack) {
        return is(stack) ? THIRST : 0;
    }

    public static float quench(ItemStack stack) {
        if (bucket(stack)) {
            return BUCKET_QUENCH;
        }
        if (bottle(stack)) {
            return BOTTLE_QUENCH;
        }
        return 0.0F;
    }

    /** Passes when this click should still place water. */
    public static InteractionResult start(Player player, InteractionHand hand) {
        if (!bucket(player.getItemInHand(hand)) || player.isCreative() || player.isSpectator()) {
            return InteractionResult.PASS;
        }
        if (!wantsToDrink(player)) {
            return InteractionResult.PASS;
        }
        if (!player.isUsingItem()) {
            player.startUsingItem(hand);
        }
        return InteractionResult.CONSUME;
    }

    public static void empty(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!bucket(held)) {
            return;
        }
        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F, 1.0F);
    }

    private static boolean wantsToDrink(Player player) {
        if (player.isShiftKeyDown()) {
            return true;
        }
        Vec3 from = player.getEyePosition();
        Vec3 to = from.add(player.getViewVector(1.0F).scale(5.0));
        BlockHitResult hit = player.level().clip(new ClipContext(
                from,
                to,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
        return hit.getType() == HitResult.Type.MISS;
    }
}
