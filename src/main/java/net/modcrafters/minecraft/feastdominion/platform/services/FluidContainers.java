package net.modcrafters.minecraft.feastdominion.platform.services;

import net.minecraft.world.item.ItemStack;

/** Water or milk held in an item's fluid container. Potions are not part of this. */
public interface FluidContainers {
    FluidSip sip(ItemStack stack);

    /** Drains one sip from a single item. The leftover replaces that one item. */
    Drain drain(ItemStack stack);

    record FluidSip(Kind kind, Size size) {
        public enum Kind {
            NONE,
            WATER,
            MILK
        }

        public enum Size {
            NONE,
            BOTTLE,
            BUCKET
        }

        public static final FluidSip NONE = new FluidSip(Kind.NONE, Size.NONE);

        public boolean present() {
            return this.size != Size.NONE;
        }

        public boolean milk() {
            return this.kind == Kind.MILK;
        }
    }

    record Drain(boolean done, ItemStack leftover) {
        public static final Drain NONE = new Drain(false, ItemStack.EMPTY);
    }
}
