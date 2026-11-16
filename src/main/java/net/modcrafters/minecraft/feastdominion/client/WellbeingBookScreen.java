package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.feature.HealthIncreaseFeature;
import net.modcrafters.minecraft.feastdominion.feature.Variety;
import net.modcrafters.minecraft.feastdominion.feature.VarietyView;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.thirst.Thirst;
import net.modcrafters.minecraft.feastdominion.wellness.DietBand;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Locale;
//? if >=26.1
import net.minecraft.client.renderer.RenderPipelines;

/** The wellbeing book: an overview, the seven groups, and the health windows when Spice of Life is absent. */
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

    private int lastPage() {
        return HealthIncreaseFeature.instance().enabled() ? 2 : 1;
    }

    private boolean turnPage(double mouseX, double mouseY) {
        int textX = (this.width - BOOK_WIDTH) / 2 + 36;
        boolean next = mouseX >= textX + 90 && mouseX <= textX + 130 && mouseY >= 148 && mouseY <= 164;
        boolean back = mouseX >= textX && mouseX <= textX + 40 && mouseY >= 148 && mouseY <= 164;
        if (next && this.page < this.lastPage()) {
            this.page++;
            return true;
        }
        if (back && this.page > 0) {
            this.page--;
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
        if (this.page > this.lastPage()) {
            this.page = 0;
        }
        if (this.page == 2) {
            drawHealth(graphics, textX);
        } else if (this.page == 1) {
            drawGroups(graphics, textX);
        } else {
            this.page = 0;
            drawOverview(graphics, textX);
        }
        if (this.page > 0) {
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.back"), textX, 152, 0, false);
        }
        if (this.page < this.lastPage()) {
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.next"), textX + 90, 152, 0, false);
        }
    }

    private DietMemory diet() {
        if (this.minecraft != null && this.minecraft.player != null) {
            return Services.GROUPS.memory(this.minecraft.player);
        }
        return DietMemory.start();
    }

    private void drawOverview(GuiGraphicsExtractor graphics, int textX) {
        int y = 18;
        graphics.text(this.font, this.title, textX, y, 0, false);
        y += 16;
        DietMemory diet = DietMemory.start();
        if (this.minecraft != null && this.minecraft.player != null) {
            Taste lean = Services.LEAN.taste(this.minecraft.player);
            if (lean != null) {
                graphics.text(
                        this.font,
                        Component.translatable(
                                "feast_dominion.wellbeing.lean",
                                Component.translatable("feast_dominion.taste." + lean.name().toLowerCase(Locale.ROOT))
                        ),
                        textX,
                        y,
                        0,
                        false
                );
                y += 12;
            }
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
            y += 16;
        }
        boolean wellFed = true;
        for (FoodGroup group : FoodGroup.values()) {
            if (Wellness.band(diet.points(group)) != DietBand.HIGH) {
                wellFed = false;
            }
        }
        if (wellFed) {
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.well_fed"), textX, y, 0, false);
        }
    }

    private void drawGroups(GuiGraphicsExtractor graphics, int textX) {
        DietMemory diet = this.diet();
        int y = 18;
        for (FoodGroup group : FoodGroup.values()) {
            Component name = Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase());
            graphics.text(this.font, name, textX, y, 0, false);
            HungerBar.draw(graphics, textX, y + 9, diet.points(group));
            y += 18;
        }
    }

    private void drawHealth(GuiGraphicsExtractor graphics, int textX) {
        VarietyView.Snapshot variety = VarietyView.current();
        int y = 18;
        graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.variety"), textX, y, 0, false);
        y += 16;
        y = drawWindow(graphics, textX, y, "feast_dominion.wellbeing.today", variety.today());
        y = drawWindow(graphics, textX, y, "feast_dominion.wellbeing.week", variety.week());
        y = drawWindow(graphics, textX, y, "feast_dominion.wellbeing.month", variety.month());
        y += 6;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.hearts", Variety.formatHearts(variety.hearts())
        ), textX, y, 0, false);
        y += 12;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.weights",
                variety.dayWeight(),
                variety.weekWeight(),
                variety.monthWeight()
        ), textX, y, 0, false);
    }

    private int drawWindow(GuiGraphicsExtractor graphics, int textX, int y, String label, int points) {
        graphics.text(this.font, Component.translatable(label), textX, y, 0, false);
        y += 10;
        HungerBar.draw(graphics, textX, y, points);
        return y + 12;
    }
}
