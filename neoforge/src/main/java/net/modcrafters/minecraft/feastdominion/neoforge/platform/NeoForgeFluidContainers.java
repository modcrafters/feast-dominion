package net.modcrafters.minecraft.feastdominion.neoforge.platform;

import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers;
import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers.Drain;
import net.modcrafters.minecraft.feastdominion.platform.services.FluidContainers.FluidSip;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
//? if >=26.1 {
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
//?} else {
/*import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
*///?}

/** A bucket is 1000 mB. A bottle is a quarter of that. Milk is NeoForge's milk fluid. */
public class NeoForgeFluidContainers implements FluidContainers {
    private static final int BUCKET = FluidType.BUCKET_VOLUME;
    private static final int BOTTLE = BUCKET / 4;

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
        //? if >=26.1 {
        return takeTransfer(one, commit);
        //?} else {
        /*return takeHandler(one, commit);
        *///?}
    }

    //? if >=26.1 {
    private static Taken takeTransfer(ItemStack one, boolean commit) {
        SimpleContainer container = new SimpleContainer(one);
        ItemAccess access = ItemAccess.forHandlerIndex(VanillaContainerWrapper.of(container), 0).oneByOne();
        ResourceHandler<FluidResource> handler = access.getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) {
            return Taken.NONE;
        }
        Taken bucket = pull(handler, container, BUCKET, FluidSip.Size.BUCKET, commit);
        if (bucket != null) {
            return bucket;
        }
        Taken bottle = pull(handler, container, BOTTLE, FluidSip.Size.BOTTLE, commit);
        if (bottle != null) {
            return bottle;
        }
        return Taken.NONE;
    }

    private static Taken pull(ResourceHandler<FluidResource> handler, SimpleContainer container, int amount, FluidSip.Size size, boolean commit) {
        int slots = handler.size();
        for (int index = 0; index < slots; index++) {
            FluidResource resource = handler.getResource(index);
            if (resource.isEmpty() || handler.getAmountAsInt(index) < amount) {
                continue;
            }
            FluidSip.Kind kind = kind(resource.getFluid());
            if (kind == FluidSip.Kind.NONE) {
                continue;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                int drained = handler.extract(index, resource, amount, transaction);
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
    //?} else {
    /*private static Taken takeHandler(ItemStack one, boolean commit) {
        IFluidHandlerItem handler = one.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null) {
            return Taken.NONE;
        }
        Taken bucket = pull(handler, BUCKET, FluidSip.Size.BUCKET, commit);
        if (bucket != null) {
            return bucket;
        }
        Taken bottle = pull(handler, BOTTLE, FluidSip.Size.BOTTLE, commit);
        if (bottle != null) {
            return bottle;
        }
        return Taken.NONE;
    }

    private static Taken pull(IFluidHandlerItem handler, int amount, FluidSip.Size size, boolean commit) {
        int tanks = handler.getTanks();
        for (int index = 0; index < tanks; index++) {
            FluidStack contents = handler.getFluidInTank(index);
            if (contents.isEmpty() || contents.getAmount() < amount) {
                continue;
            }
            FluidSip.Kind kind = kind(contents.getFluid());
            if (kind == FluidSip.Kind.NONE) {
                continue;
            }
            FluidStack ask = new FluidStack(contents.getFluid(), amount);
            FluidStack simulated = handler.drain(ask, IFluidHandler.FluidAction.SIMULATE);
            if (simulated.isEmpty() || simulated.getAmount() != amount) {
                continue;
            }
            if (commit) {
                handler.drain(ask, IFluidHandler.FluidAction.EXECUTE);
            }
            ItemStack leftover = commit ? handler.getContainer().copy() : ItemStack.EMPTY;
            return new Taken(new FluidSip(kind, size), leftover);
        }
        return null;
    }
    *///?}

    private static FluidSip.Kind kind(Fluid fluid) {
        if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) {
            return FluidSip.Kind.WATER;
        }
        if (NeoForgeMod.MILK.isBound() && (fluid == NeoForgeMod.MILK.get() || fluid == NeoForgeMod.FLOWING_MILK.get())) {
            return FluidSip.Kind.MILK;
        }
        return FluidSip.Kind.NONE;
    }

    private record Taken(FluidSip sip, ItemStack leftover) {
        private static final Taken NONE = new Taken(FluidSip.NONE, ItemStack.EMPTY);
    }
}
