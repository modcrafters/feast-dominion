package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.feature.HealthIncreaseFeature;
import net.modcrafters.minecraft.feastdominion.feature.MealSlots;
import net.modcrafters.minecraft.feastdominion.feature.Variety;
import net.modcrafters.minecraft.feastdominion.feature.VarietyView;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstIcons;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;

import java.util.List;
import java.util.Locale;

/** Lean, ten thirst droplets, the seven groups, and recent meals while the variety hearts are on. */
public final class WellbeingBoardScreen extends Screen {
    private static final Identifier SLOT = Identifier.withDefaultNamespace("container/slot");
    private static final Identifier THIRST_EMPTY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/thirst_empty");
    private static final Identifier THIRST_HALF = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/thirst_half");
    private static final Identifier THIRST_FULL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hud/thirst_full");
    private static final int PAPER = 0xFFF3E6C8;
    private static final int EDGE = 0xFF6B4E32;
    private static final int INK = 0;
    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 10;
    private static final int ROW = 14;
    private static final int PAD = 10;

    private final Screen parent;

    public WellbeingBoardScreen(Screen parent) {
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
        drawBoard(graphics);
    }
    //?} else {
    /*@Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        drawBoard(graphics);
    }
    *///?}

    private void drawBoard(GuiGraphicsExtractor graphics) {
        boolean meals = BoardLines.mealsColumn(HealthIncreaseFeature.instance().enabled());
        DietMemory diet = this.diet();
        int[] points = new int[FoodGroup.values().length];
        for (FoodGroup group : FoodGroup.values()) {
            points[group.ordinal()] = diet.points(group);
        }
        boolean fed = BoardLines.wellFed(points);
        int nameColumn = this.nameColumn();
        int leftWidth = nameColumn + HungerBar.WIDTH;
        if (!fed) {
            leftWidth += 8 + this.wordWidth();
        }
        leftWidth = Math.max(leftWidth, this.font.width(this.title));
        leftWidth = Math.max(leftWidth, this.leanWidth());
        if (fed) {
            leftWidth = Math.max(leftWidth, this.font.width(Component.translatable("feast_dominion.wellbeing.well_fed")));
        }
        int gap = meals ? 16 : 0;
        int rightWidth = meals ? COLUMNS * SLOT_SIZE : 0;
        int innerWidth = leftWidth + gap + rightWidth;
        int leftHeight = this.leftHeight(fed);
        int rightHeight = meals ? this.rightHeight() : 0;
        int innerHeight = Math.max(leftHeight, rightHeight);
        int panelWidth = innerWidth + PAD * 2;
        int panelHeight = innerHeight + PAD * 2;
        int panelX = Math.max(4, (this.width - panelWidth) / 2);
        int panelY = Math.max(4, (this.height - panelHeight) / 2);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, EDGE);
        graphics.fill(panelX + 2, panelY + 2, panelX + panelWidth - 2, panelY + panelHeight - 2, PAPER);

        int x = panelX + PAD;
        int y = panelY + PAD;
        this.drawLeft(graphics, x, y, nameColumn, points, fed);
        if (meals) {
            this.drawMeals(graphics, x + leftWidth + gap, y);
        }
    }

    private void drawLeft(GuiGraphicsExtractor graphics, int x, int y, int nameColumn, int[] points, boolean fed) {
        graphics.text(this.font, this.title, x, y, INK, false);
        y += ROW;
        Player player = this.player();
        if (player != null) {
            Taste lean = Services.LEAN.taste(player);
            if (lean != null) {
                graphics.text(
                        this.font,
                        Component.translatable(
                                "feast_dominion.wellbeing.lean",
                                Component.translatable("feast_dominion.taste." + lean.name().toLowerCase(Locale.ROOT))
                        ),
                        x,
                        y,
                        INK,
                        false
                );
                y += ROW;
            }
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.thirst"), x, y + 1, INK, false);
            drawThirst(graphics, x + nameColumn, y, Services.THIRST.points(player));
            y += ROW;
        }
        for (FoodGroup group : FoodGroup.values()) {
            int at = points[group.ordinal()];
            Component name = Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase(Locale.ROOT));
            graphics.text(this.font, name, x, y + 1, INK, false);
            HungerBar.draw(graphics, x + nameColumn, y, at);
            if (!fed) {
                graphics.text(
                        this.font,
                        Component.translatable(BoardLines.groupWord(group, at)),
                        x + nameColumn + HungerBar.WIDTH + 8,
                        y + 1,
                        INK,
                        false
                );
            }
            y += ROW;
        }
        if (fed) {
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.well_fed"), x, y, INK, false);
        }
    }

    /** Ten droplets, filled the same way as the thirst bar. Quench stays on the food tooltip. */
    private static void drawThirst(GuiGraphicsExtractor graphics, int x, int y, int points) {
        for (int fromLeft = 0; fromLeft < 10; fromLeft++) {
            int at = x + fromLeft * HungerBar.STEP;
            SaturationHud.blit(graphics, THIRST_EMPTY, at, y);
            Identifier fill = switch (ThirstIcons.fill(points, 9 - fromLeft)) {
                case FULL -> THIRST_FULL;
                case HALF -> THIRST_HALF;
                case EMPTY -> null;
            };
            if (fill != null) {
                SaturationHud.blit(graphics, fill, at, y);
            }
        }
    }

    private void drawMeals(GuiGraphicsExtractor graphics, int x, int y) {
        VarietyView.Snapshot variety = VarietyView.current();
        graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.variety"), x, y, INK, false);
        y += ROW;
        y = drawWindow(graphics, x, y, "feast_dominion.wellbeing.today", variety.todayItems(), MealSlots.TODAY);
        y = drawWindow(graphics, x, y, "feast_dominion.wellbeing.week", variety.weekItems(), MealSlots.WEEK);
        y = drawWindow(graphics, x, y, "feast_dominion.wellbeing.month", variety.monthItems(), MealSlots.MONTH);
        y += 6;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.hearts", Variety.formatHearts(variety.hearts())
        ), x, y, INK, false);
        y += ROW;
        graphics.text(this.font, Component.translatable(
                "feast_dominion.wellbeing.weights",
                variety.dayWeight(),
                variety.weekWeight(),
                variety.monthWeight()
        ), x, y, INK, false);
    }

    /** Filled cells are that list's icons. Empty cells make up the rest of the cap. Today is only today's list. */
    private int drawWindow(GuiGraphicsExtractor graphics, int x, int y, String label, List<String> items, int cap) {
        graphics.text(this.font, Component.translatable(label), x, y, INK, false);
        y += 12;
        List<String> shown = items == null ? List.of() : items;
        int rows = (cap + COLUMNS - 1) / COLUMNS;
        for (int i = 0; i < cap; i++) {
            int cellX = x + (i % COLUMNS) * SLOT_SIZE;
            int cellY = y + (i / COLUMNS) * SLOT_SIZE;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT, cellX, cellY, SLOT_SIZE, SLOT_SIZE);
            if (i < shown.size()) {
                ItemStack stack = icon(shown.get(i));
                if (!stack.isEmpty()) {
                    drawIcon(graphics, stack, cellX + 1, cellY + 1);
                }
            }
        }
        return y + rows * SLOT_SIZE + 4;
    }

    private static void drawIcon(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        //? if >=26.1 {
        graphics.item(stack, x, y);
        //?} else
        //graphics.renderItem(stack, x, y);
    }

    /** An unknown id leaves the cell empty. */
    private static ItemStack icon(String id) {
        if (id == null || id.isBlank()) {
            return ItemStack.EMPTY;
        }
        Identifier parsed = Identifier.tryParse(id);
        if (parsed == null) {
            return ItemStack.EMPTY;
        }
        //? if >=26.1 {
        Item item = BuiltInRegistries.ITEM.getValue(parsed);
        //?} else
        //Item item = BuiltInRegistries.ITEM.get(parsed);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item);
    }

    private int leftHeight(boolean fed) {
        int height = ROW;
        if (this.player() != null) {
            if (Services.LEAN.taste(this.player()) != null) {
                height += ROW;
            }
            height += ROW;
        }
        height += FoodGroup.values().length * ROW;
        if (fed) {
            height += ROW;
        }
        return height;
    }

    private static int rightHeight() {
        int todayRows = 1;
        int weekRows = (MealSlots.WEEK + COLUMNS - 1) / COLUMNS;
        int monthRows = (MealSlots.MONTH + COLUMNS - 1) / COLUMNS;
        return ROW + (12 + todayRows * SLOT_SIZE + 4) + (12 + weekRows * SLOT_SIZE + 4) + (12 + monthRows * SLOT_SIZE + 4) + 6 + ROW + ROW;
    }

    private int nameColumn() {
        int width = this.font.width(Component.translatable("feast_dominion.wellbeing.thirst"));
        for (FoodGroup group : FoodGroup.values()) {
            Component name = Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase(Locale.ROOT));
            width = Math.max(width, this.font.width(name));
        }
        return width + 6;
    }

    private int wordWidth() {
        int width = 0;
        for (FoodGroup group : FoodGroup.values()) {
            width = Math.max(width, this.font.width(Component.translatable(BoardLines.groupWord(group, 0))));
            width = Math.max(width, this.font.width(Component.translatable(BoardLines.groupWord(group, 10))));
            width = Math.max(width, this.font.width(Component.translatable(BoardLines.groupWord(group, 20))));
        }
        return width;
    }

    private int leanWidth() {
        int width = 0;
        for (Taste taste : Taste.values()) {
            width = Math.max(width, this.font.width(Component.translatable(
                    "feast_dominion.wellbeing.lean",
                    Component.translatable("feast_dominion.taste." + taste.name().toLowerCase(Locale.ROOT))
            )));
        }
        return width;
    }

    private Player player() {
        if (this.minecraft == null) {
            return null;
        }
        return this.minecraft.player;
    }

    private DietMemory diet() {
        Player player = this.player();
        if (player == null) {
            return DietMemory.start();
        }
        return Services.GROUPS.memory(player);
    }
}
