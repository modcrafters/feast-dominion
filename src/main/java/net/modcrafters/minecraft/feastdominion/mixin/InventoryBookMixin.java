package net.modcrafters.minecraft.feastdominion.mixin;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.client.WellbeingBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryBookMixin {
    /** Right end of the offhand row, under the crafting result. The recipe book button is at 104. */
    private static final int BUTTON_X = 150;

    @Unique
    private ImageButton feast$wellbeing;

    @Inject(method = "init", at = @At("RETURN"))
    private void feast$addWellbeingBook(CallbackInfo ci) {
        Minecraft minecraft = ((ScreenWidgets) (Object) this).feast$minecraft();
        if (minecraft.player != null && minecraft.player.isCreative()) {
            return;
        }
        Screen screen = (Screen) (Object) this;
        Component name = Component.translatable("feast_dominion.wellbeing.open");
        this.feast$wellbeing = new ImageButton(
                ((ContainerOrigin) (Object) this).feast$left() + BUTTON_X,
                screen.height / 2 - 22,
                20,
                18,
                new WidgetSprites(
                        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "wellbeing/book"),
                        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "wellbeing/book_highlighted")
                ),
                button -> {
                    Screen book = new WellbeingBookScreen((InventoryScreen) (Object) this);
                    //? if >=26.1 {
                    minecraft.setScreenAndShow(book);
                    //?} else
                    //minecraft.setScreen(book);
                },
                name
        );
        this.feast$wellbeing.setTooltip(Tooltip.create(name));
        ((ScreenWidgets) (Object) this).feast$addRenderableWidget(this.feast$wellbeing);
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void feast$keepWellbeingBookBesideRecipes(CallbackInfo ci) {
        if (this.feast$wellbeing != null) {
            Screen screen = (Screen) (Object) this;
            this.feast$wellbeing.setX(((ContainerOrigin) (Object) this).feast$left() + BUTTON_X);
            this.feast$wellbeing.setY(screen.height / 2 - 22);
        }
    }
}
