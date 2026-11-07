package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.thirst.Thirst;
import net.modcrafters.minecraft.feastdominion.wellness.DietBand;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
//? if >=26.1
import net.minecraft.client.renderer.RenderPipelines;

/** The wellbeing book: thirst and the seven bands, one page. */
public final class WellbeingBookScreen extends Screen {
    private static final Identifier BOOK = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/book.png");
    private static final int BOOK_WIDTH = 192;
    private static final int BOOK_HEIGHT = 192;
    private final Screen parent;

    public WellbeingBookScreen(Screen parent) {
        super(Component.translatable("feast_dominion.wellbeing.title"));
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            //? if >=26.1 {
            this.minecraft.setScreenAndShow(this.parent);
            //?} else
            //this.minecraft.setScreen(this.parent);
        }
    }

    //? if >=26.1 {
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        drawPage(graphics);
    }
    //?} else {
    /*@Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        drawPage(graphics);
    }
    *///?}

    private void drawPage(GuiGraphicsExtractor graphics) {
        int left = (this.width - BOOK_WIDTH) / 2;
        //? if >=26.1 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK, left, 2, 0.0F, 0.0F, BOOK_WIDTH, BOOK_HEIGHT, 256, 256);
        //?} else
        //graphics.blit(BOOK, left, 2, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);
        int textX = left + 36;
        int y = 18;
        graphics.text(this.font, this.title, textX, y, 0, false);
        y += 16;
        if (this.minecraft != null && this.minecraft.player != null) {
            int thirst = Services.THIRST.points(this.minecraft.player);
            graphics.text(
                    this.font,
                    Component.translatable("feast_dominion.wellbeing.thirst", thirst, Thirst.POINTS),
                    textX,
                    y,
                    0,
                    false
            );
        }
        y += 14;
        boolean wellFed = true;
        for (FoodGroup group : FoodGroup.values()) {
            DietBand band = Wellness.band(group);
            if (band != DietBand.HIGH) {
                wellFed = false;
            }
            Component line = band == DietBand.LOW
                    ? Component.translatable(
                            "feast_dominion.wellbeing.line.low",
                            Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase()),
                            Component.translatable("feast_dominion.wellbeing.trouble." + group.name().toLowerCase())
                    )
                    : Component.translatable(
                            "feast_dominion.wellbeing.line",
                            Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase()),
                            Component.translatable("feast_dominion.wellbeing.band." + band.name().toLowerCase())
                    );
            graphics.text(this.font, line, textX, y, 0, false);
            y += 10;
        }
        if (wellFed) {
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.well_fed"), textX, y + 4, 0, false);
        }
    }
}
