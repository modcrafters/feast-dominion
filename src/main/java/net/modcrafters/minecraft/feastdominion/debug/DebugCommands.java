package net.modcrafters.minecraft.feastdominion.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.thirst.Thirst;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** {@code /feast set <hunger> <saturation> <thirst>}. Registered only when debug commands are on. */
public final class DebugCommands {
    private DebugCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!FeastConfig.debugCommands()) {
            return;
        }
        dispatcher.register(Commands.literal("feast")
                .requires(DebugCommands::allowed)
                .then(Commands.literal("set")
                        .then(Commands.argument("hunger", IntegerArgumentType.integer(0, 20))
                                .then(Commands.argument("saturation", FloatArgumentType.floatArg(0.0F, 40.0F))
                                        .then(Commands.argument("thirst", IntegerArgumentType.integer(0, Thirst.POINTS))
                                                .executes(DebugCommands::set))))));
    }

    private static boolean allowed(CommandSourceStack source) {
        //? if >=26.1 {
        return source.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
        //?} else {
        /*return source.hasPermission(2);
        *///?}
    }

    private static int set(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int hunger = IntegerArgumentType.getInteger(context, "hunger");
        float saturation = FloatArgumentType.getFloat(context, "saturation");
        int thirst = IntegerArgumentType.getInteger(context, "thirst");
        player.getFoodData().setFoodLevel(hunger);
        player.getFoodData().setSaturation(saturation);
        Services.THIRST.setPoints(player, thirst);
        int shownHunger = player.getFoodData().getFoodLevel();
        float shownSaturation = player.getFoodData().getSaturationLevel();
        int shownThirst = Services.THIRST.points(player);
        context.getSource().sendSuccess(
                () -> Component.translatable("feast_dominion.debug.set", shownHunger, shownSaturation, shownThirst),
                true
        );
        return 1;
    }
}
