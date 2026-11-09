package net.modcrafters.minecraft.feastdominion.neoforge.platform;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.feature.FoodLog;
import net.modcrafters.minecraft.feastdominion.platform.services.DietStore;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class NeoForgeDietStore implements DietStore {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES,
            Constants.MOD_ID
    );
    public static final Supplier<AttachmentType<FoodLog>> DIET = ATTACHMENTS.register(
            "diet",
            () -> AttachmentType.builder(() -> FoodLog.EMPTY)
                    //? if >=1.21.6 {
                    .serialize(FoodLog.MAP_CODEC)
                    //?} else
                    //.serialize(FoodLog.CODEC)
                    .copyOnDeath()
                    .build()
    );

    @Override
    public FoodLog log(Player player) {
        return player.getData(DIET.get());
    }

    @Override
    public void setLog(Player player, FoodLog log) {
        player.setData(DIET.get(), log);
    }
}
