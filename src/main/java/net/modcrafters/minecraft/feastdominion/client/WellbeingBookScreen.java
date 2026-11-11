package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.feature.HealthIncreaseFeature;
import net.modcrafters.minecraft.feastdominion.feature.Variety;
import net.modcrafters.minecraft.feastdominion.feature.VarietyView;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.thirst.Thirst;
import net.modcrafters.minecraft.feastdominion.wellness.DietBand;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
//? if >=26.1
import net.minecraft.client.renderer.RenderPipelines;

/** The wellbeing book: thirst and the seven bands, and variety when Spice of Life is absent. */
public final class WellbeingBookScreen extends Screen {
    private static final Identifier BOOK = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/book.png");
    private static final int BOOK_WIDTH = 192;
    private static final int BOOK_HEIGHT = 192;
    private final Screen parent;
    private int page;

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

    private boolean turnPage(double mouseX, double mouseY) {
        if (!HealthIncreaseFeature.instance().enabled()) {
            return false;
        }
        int textX = (this.width - BOOK_WIDTH) / 2 + 36;
        if (this.page == 0 && mouseX >= textX + 90 && mouseX <= textX + 130 && mouseY >= 148 && mouseY <= 164) {
            this.page = 1;
            return true;
        }
        if (this.page == 1 && mouseX >= textX && mouseX <= textX + 40 && mouseY >= 148 && mouseY <= 164) {
            this.page = 0;
            return true;
        }
        return false;
    }

    //? if >=26.1 {
    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubled) {
        if (this.turnPage(event.x(), event.y())) {
            return true;
        }
        return super.mouseClicked(event, doubled);
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.turnPage(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    *///?}

    private void drawPage(GuiGraphicsExtractor graphics) {
        int left = (this.width - BOOK_WIDTH) / 2;
        //? if >=26.1 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK, left, 2, 0.0F, 0.0F, BOOK_WIDTH, BOOK_HEIGHT, 256, 256);
        //?} else
        //graphics.blit(BOOK, left, 2, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);
        int textX = left + 36;
        if (this.page == 1 && HealthIncreaseFeature.instance().enabled()) {
            drawVariety(graphics, textX);
            return;
        }
        this.page = 0;
        int y = 18;
        graphics.text(this.font, this.title, textX, y, 0, false);
        y += 16;
        DietMemory diet = DietMemory.start();
        if (this.minecraft != null && this.minecraft.player != null) {
            int thirst = Services.THIRST.points(this.minecraft.player);
            diet = Services.GROUPS.memory(this.minecraft.player);
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
            int points = diet.points(group);
            DietBand band = Wellness.band(points);
            if (band != DietBand.HIGH) {
                wellFed = false;
            }
            Component name = Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase());
            Component line = band == DietBand.LOW
                    ? Component.translatable(
                            "feast_dominion.wellbeing.line.low",
                            name,
                            points,
                            Wellness.POINTS,
                            Component.translatable("feast_dominion.wellbeing.trouble." + group.name().toLowerCase())
                    )
                    : Component.translatable("feast_dominion.wellbeing.line", name, points, Wellness.POINTS);
            graphics.text(this.font, line, textX, y, 0, false);
            y += 10;
        }
        if (wellFed) {
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.well_fed"), textX, y + 4, 0, false);
        }
        if (HealthIncreaseFeature.instance().enabled()) {
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.next"), textX + 90, 152, 0, false);
        }
    }

    private void drawVariety(GuiGraphicsExtractor graphics, int textX) {
        VarietyView.Snapshot variety = VarietyView.current();
        int y = 18;
        graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.variety"), textX, y, 0, false);
        y += 20;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.today", variety.today(), Variety.DAY_CAP
        ), textX, y, 0, false);
        y += 14;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.week", variety.week(), Variety.WEEK_CAP
        ), textX, y, 0, false);
        y += 14;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.month", variety.month(), Variety.MONTH_CAP
        ), textX, y, 0, false);
        y += 18;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.hearts", Variety.formatHearts(variety.hearts())
        ), textX, y, 0, false);
        graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.back"), textX, 152, 0, false);
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.weights",
                variety.dayWeight(),
                variety.weekWeight(),
                variety.monthWeight()
        ), textX, 168, 0, false);
    }
}
