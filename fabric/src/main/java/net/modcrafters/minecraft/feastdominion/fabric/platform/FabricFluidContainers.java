package net.modcrafters.minecraft.feastdominion.fabric.platform;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers;
import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers.Drain;
import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers.FluidSip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/** A bucket and a bottle use Fabric's fluid units. Milk is a fluid whose path is milk. */
public class FabricFluidContainers implements FluidContainers {
    @Override
    public FluidSip sip(ItemStack stack) {
        return take(stack, false).sip;
    }

    @Override
    public Drain drain(ItemStack stack) {
        Taken taken = take(stack, true);
        if (!taken.sip.present()) {
            return Drain.NONE;
        }
        return new Drain(true, taken.leftover);
    }

    private static Taken take(ItemStack stack, boolean commit) {
        if (stack.isEmpty() || stack.is(Items.POTION)) {
            return Taken.NONE;
        }
        ItemStack one = stack.copy();
        one.setCount(1);
        SimpleContainer container = new SimpleContainer(one);
        ContainerItemContext context = ContainerItemContext.ofSingleSlot(ContainerStorage.of(container, null).getSlot(0));
        Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
        if (storage == null) {
            return Taken.NONE;
        }
        Taken bucket = pull(storage, container, FluidConstants.BUCKET, FluidSip.Size.BUCKET, commit);
        if (bucket != null) {
            return bucket;
        }
        Taken bottle = pull(storage, container, FluidConstants.BOTTLE, FluidSip.Size.BOTTLE, commit);
        if (bottle != null) {
            return bottle;
        }
        return Taken.NONE;
    }

    private static Taken pull(Storage<FluidVariant> storage, SimpleContainer container, long amount, FluidSip.Size size, boolean commit) {
        for (StorageView<FluidVariant> view : storage.nonEmptyViews()) {
            FluidVariant resource = view.getResource();
            if (resource.isBlank() || view.getAmount() < amount) {
                continue;
            }
            FluidSip.Kind kind = kind(resource.getFluid());
            if (kind == FluidSip.Kind.NONE) {
                continue;
            }
            try (Transaction transaction = Transaction.openOuter()) {
                long drained = storage.extract(resource, amount, transaction);
                if (drained != amount) {
                    continue;
                }
                if (commit) {
                    transaction.commit();
                }
                ItemStack leftover = commit ? container.getItem(0).copy() : ItemStack.EMPTY;
                return new Taken(new FluidSip(kind, size), leftover);
            }
        }
        return null;
    }

    private static FluidSip.Kind kind(Fluid fluid) {
        if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) {
            return FluidSip.Kind.WATER;
        }
        Identifier id = BuiltInRegistries.FLUID.getKey(fluid);
        if (id != null && (id.getPath().equals("milk") || id.getPath().equals("flowing_milk"))) {
            return FluidSip.Kind.MILK;
        }
        return FluidSip.Kind.NONE;
    }

    private record Taken(FluidSip sip, ItemStack leftover) {
        private static final Taken NONE = new Taken(FluidSip.NONE, ItemStack.EMPTY);
    }
}
