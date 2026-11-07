package net.modcrafters.minecraft.feastdominion.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityDrinkAccess {
    @Accessor("useItem")
    void feast$setUseItem(ItemStack stack);

    @Accessor("useItemRemaining")
    void feast$setUseItemRemaining(int ticks);
}
