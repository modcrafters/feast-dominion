package net.modcrafters.minecraft.feastdominion.thirst;

import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers.Drain;
import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers.FluidSip;
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

/** A water bottle drinks as usual. A fluid container drinks in the air, or while sneaking. */
public final class WaterDrink {
    /** One drink on the bar. A bottle and a bucket both restore this much thirst. */
    public static final int THIRST = Drink.AMOUNT;
    /** A bottle's reserve matches the thirst it restores. */
    public static final float BOTTLE_QUENCH = Drink.AMOUNT;
    /** A bucket is three bottles, so the reserve is three times a bottle's. */
    public static final float BUCKET_QUENCH = Drink.AMOUNT * 3.0F;

    private WaterDrink() {
    }

    /** A container this mod drinks by holding it. A water potion is not one of these. */
    public static boolean held(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET) || milkBucket(stack) || Services.FLUIDS.sip(stack).present();
    }

    /** Vanilla milk, and on 1.21.1 a modded bucket that extends it, such as Meadow's wooden one. */
    public static boolean milkBucket(ItemStack stack) {
        //? if >=26.1 {
        return stack.is(Items.MILK_BUCKET);
        //?} else {
        /*return stack.getItem() instanceof net.minecraft.world.item.MilkBucketItem;
        *///?}
    }

    public static boolean bottle(ItemStack stack) {
        if (!stack.is(Items.POTION)) {
            return false;
        }
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(Potions.WATER);
    }

    public static boolean is(ItemStack stack) {
        return held(stack) || bottle(stack);
    }

    public static int thirst(ItemStack stack) {
        return is(stack) ? THIRST : 0;
    }

    public static float quench(ItemStack stack) {
        FluidSip sip = Services.FLUIDS.sip(stack);
        if (sip.size() == FluidSip.Size.BUCKET || stack.is(Items.WATER_BUCKET) || milkBucket(stack)) {
            return BUCKET_QUENCH;
        }
        if (sip.size() == FluidSip.Size.BOTTLE || bottle(stack)) {
            return BOTTLE_QUENCH;
        }
        return 0.0F;
    }

    /** Passes when this click should still place the fluid. */
    public static InteractionResult start(Player player, InteractionHand hand) {
        if (!held(player.getItemInHand(hand)) || player.isCreative() || player.isSpectator()) {
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

    /** Empties one container. A vanilla milk bucket has already done this itself. */
    public static boolean finish(Player player, InteractionHand hand, ItemStack eaten) {
        ItemStack held = player.getItemInHand(hand);
        FluidSip live = Services.FLUIDS.sip(held);
        boolean emptied = false;
        if (live.present()) {
            Drain drain = Services.FLUIDS.drain(held);
            if (drain.done()) {
                replaceOne(player, hand, drain.leftover());
                emptied = true;
            }
        } else if (held.is(Items.WATER_BUCKET) || held.is(Items.MILK_BUCKET)) {
            replaceOne(player, hand, new ItemStack(Items.BUCKET));
            emptied = true;
        }
        if (!emptied && !vanillaConsumed(eaten, held)) {
            return false;
        }
        if (emptied && (milkBucket(eaten) || live.milk() || Services.FLUIDS.sip(eaten).milk())) {
            player.removeAllEffects();
        }
        if (emptied && !milkBucket(eaten)) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
        return true;
    }

    private static boolean vanillaConsumed(ItemStack eaten, ItemStack held) {
        return (eaten.is(Items.WATER_BUCKET) || milkBucket(eaten)) && !held.is(eaten.getItem());
    }

    private static void replaceOne(Player player, InteractionHand hand, ItemStack leftover) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getCount() <= 1) {
            player.setItemInHand(hand, leftover);
            return;
        }
        held.shrink(1);
        if (!leftover.isEmpty() && !player.getInventory().add(leftover)) {
            //? if >=26.1 {
            player.drop(leftover, false, net.minecraft.util.Prediction.SERVER_ONLY);
            //?} else {
            /*player.drop(leftover, false);
            *///?}
        }
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
