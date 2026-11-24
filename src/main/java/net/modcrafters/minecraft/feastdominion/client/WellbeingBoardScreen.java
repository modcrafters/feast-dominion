package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.modcrafters.minecraft.feastdominion.FeastConfig;
import net.modcrafters.minecraft.feastdominion.feature.Constitution;
import net.modcrafters.minecraft.feastdominion.feature.HealthIncreaseFeature;
import net.modcrafters.minecraft.feastdominion.feature.HealthSettings;
import net.modcrafters.minecraft.feastdominion.feature.MealSlots;
import net.modcrafters.minecraft.feastdominion.feature.Variety;
import net.modcrafters.minecraft.feastdominion.feature.VarietyView;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.taste.TasteCatalog;
import net.modcrafters.minecraft.feastdominion.taste.TasteProfile;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstIcons;
import net.modcrafters.minecraft.feastdominion.wellness.DietLevels;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Lean, ten thirst droplets, the food groups, the tastes of the month, and recent meals. */
public final class WellbeingBoardScreen extends Screen {
    private static final Identifier THIRST_EMPTY = FeastIds.of("hud/thirst_empty");
    private static final Identifier THIRST_HALF = FeastIds.of("hud/thirst_half");
    private static final Identifier THIRST_FULL = FeastIds.of("hud/thirst_full");
    private static final int PAPER = 0xFFF3E6C8;
    private static final int EDGE = 0xFF6B4E32;
    private static final int RULE = 0xFFC4AE84;
    private static final int CELL = 0xFFA89474;
    private static final int CELL_EDGE = 0xFF6E5840;
    private static final int INK = 0;
    /** Dusty rose, same quiet weight as a missed lean. */
    private static final int TASTE_INK = 0xD4A3A3;
    /** Sage, so the groups read apart from the tastes. */
    private static final int GROUP_INK = 0x9BB89A;
    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 10;
    private static final int ROW = 14;
    private static final int PAD = 10;
    /** Keeps the paper off the edge after automatic GUI scale, which can be 320 by 240. */
    private static final int MARGIN = 4;

    private final Screen parent;
    private List<Component> mealHover;

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
        drawBoard(graphics, mouseX, mouseY, partialTick);
    }
    //?} else {
    /*@Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        drawBoard(graphics, mouseX, mouseY, partialTick);
    }
    *///?}

    private void drawBoard(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        boolean meals = BoardLines.mealsColumn(HealthIncreaseFeature.instance().enabled());
        DietMemory diet = this.diet();
        int[] points = new int[FoodGroup.values().length];
        for (FoodGroup group : FoodGroup.values()) {
            points[group.ordinal()] = diet.points(group);
        }
        boolean fed = BoardLines.wellFed(points);
        int nameColumn = this.nameColumn();
        int leftWidth = nameColumn + HungerBar.WIDTH;
        leftWidth = Math.max(leftWidth, this.font.width(this.title));
        if (!meals) {
            leftWidth = Math.max(leftWidth, this.leanWidth());
        }
        if (fed) {
            leftWidth = Math.max(leftWidth, this.font.width(Component.translatable("feast_dominion.wellbeing.well_fed")));
        }
        int gap = meals ? 12 : 0;
        int rightWidth = meals ? COLUMNS * SLOT_SIZE : 0;
        int innerWidth = leftWidth + gap + rightWidth;
        int leftHeight = this.leftHeight(fed, !meals);
        int rightHeight = meals ? this.rightHeight(this.leanLine() != null) : 0;
        int innerHeight = Math.max(leftHeight, rightHeight);
        int panelWidth = innerWidth + PAD * 2;
        int panelHeight = innerHeight + PAD * 2;
        // Ten meal cells plus the names and marks are wider than 320. Shrink the panel.
        float scale = fitScale(this.width, this.height, panelWidth, panelHeight);
        float originX = fitOrigin(this.width, panelWidth, scale);
        float originY = fitOrigin(this.height, panelHeight, scale);
        float localX = (mouseX - originX) / scale;
        float localY = (mouseY - originY) / scale;
        int hop = bob(partialTick);
        List<Component> hovered = null;
        pushFit(graphics, originX, originY, scale);
        try {
            graphics.fill(0, 0, panelWidth, panelHeight, EDGE);
            graphics.fill(2, 2, panelWidth - 2, panelHeight - 2, PAPER);

            int x = PAD;
            int y = PAD;
            hovered = this.drawLeft(graphics, x, y, nameColumn, points, fed, !meals, localX, localY, hop);
            if (meals) {
                int lineX = x + leftWidth + gap / 2;
                graphics.fill(lineX, 4, lineX + 1, panelHeight - 4, RULE);
                List<Component> hearts = this.drawMeals(graphics, x + leftWidth + gap, y, localX, localY);
                if (hearts != null) {
                    hovered = hearts;
                }
            }
        } finally {
            popFit(graphics);
        }
        if (hovered != null) {
            //? if >=26.1 {
            graphics.setComponentTooltipForNextFrame(this.font, hovered, mouseX, mouseY);
            //?} else
            //graphics.renderComponentTooltip(this.font, hovered, mouseX, mouseY);
        }
    }

    private List<Component> drawLeft(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int nameColumn,
            int[] points,
            boolean fed,
            boolean showLean,
            float mouseX,
            float mouseY,
            int hop
    ) {
        graphics.text(this.font, this.title, x, y, INK, false);
        y += ROW;
        Player player = this.player();
        List<Component> hovered = null;
        if (player != null) {
            if (showLean) {
                Component lean = this.leanLine();
                if (lean != null) {
                    graphics.text(this.font, lean, x, y, INK, false);
                    if (inside(mouseX, mouseY, x, y, this.font.width(lean), ROW)) {
                        hovered = leanTip();
                    }
                    y += ROW;
                }
            }
            int thirst = Services.THIRST.points(player);
            graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.thirst"), x, y + 1, INK, false);
            drawThirst(graphics, x + nameColumn, y, thirst);
            if (inside(mouseX, mouseY, x, y, nameColumn + HungerBar.WIDTH, ROW)) {
                hovered = thirstTip(thirst);
            }
            y += ROW;
        }
        for (FoodGroup group : FoodGroup.values()) {
            int at = points[group.ordinal()];
            Component name = Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase(Locale.ROOT));
            graphics.text(this.font, name, x, y + 1, INK, false);
            boolean ink = BoardLines.marked(at);
            HungerBar.draw(graphics, x + nameColumn, ink ? y + hop : y, at, ink);
            if (inside(mouseX, mouseY, x, y, nameColumn + HungerBar.WIDTH, ROW)) {
                hovered = groupTip(group, at, fed);
            }
            y += ROW;
        }
        VarietyView.Snapshot variety = VarietyView.current();
        int taste = variety.tasteMarks();
        graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.tastes"), x, y + 1, INK, false);
        boolean tasteInk = BoardLines.tasteFull(taste);
        HungerBar.draw(graphics, x + nameColumn, tasteInk ? y + hop : y, taste, tasteInk);
        if (inside(mouseX, mouseY, x, y, nameColumn + HungerBar.WIDTH, ROW)) {
            hovered = tasteTip(variety.tasteMask());
        }
        y += ROW;
        if (fed) {
            Component fedLine = Component.translatable("feast_dominion.wellbeing.well_fed");
            graphics.text(this.font, fedLine, x, y, INK, false);
            if (inside(mouseX, mouseY, x, y, this.font.width(fedLine), ROW)) {
                hovered = List.of(
                        Component.translatable("feast_dominion.wellbeing.well_fed.line"),
                        Component.translatable("feast_dominion.wellbeing.well_fed.effect")
                );
            }
        }
        return hovered;
    }

    /** One pixel up, then one pixel down, over about a second. */
    private static int bob(float partialTick) {
        float ticks = partialTick;
        if (Minecraft.getInstance().level != null) {
            ticks += Minecraft.getInstance().level.getGameTime();
        }
        return (int) Math.round(Math.sin(ticks * 0.35) * 1.0);
    }

    private static boolean inside(float mouseX, float mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
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

    private List<Component> drawMeals(GuiGraphicsExtractor graphics, int x, int y, float mouseX, float mouseY) {
        Component lean = this.leanLine();
        List<Component> leanHover = null;
        if (lean != null) {
            graphics.text(this.font, lean, x, y, INK, false);
            if (inside(mouseX, mouseY, x, y, this.font.width(lean), ROW)) {
                leanHover = leanTip();
            }
            y += ROW;
        }
        VarietyView.Snapshot variety = VarietyView.current();
        this.mealHover = null;
        graphics.text(this.font, Component.translatable("feast_dominion.wellbeing.variety"), x, y, INK, false);
        y += ROW;
        y = drawWindow(graphics, x, y, "feast_dominion.wellbeing.today", variety.todayItems(), size(variety.todayItems()), mouseX, mouseY);
        y = drawWindow(graphics, x, y, "feast_dominion.wellbeing.week", variety.weekItems(), size(variety.weekItems()), mouseX, mouseY);
        y = drawWindow(graphics, x, y, "feast_dominion.wellbeing.month", variety.monthItems(), MealSlots.MONTH, mouseX, mouseY);
        y += 6;
        Component hearts = Component.translatable(
                "feast_dominion.wellbeing.hearts", Variety.formatHearts(variety.hearts())
        );
        graphics.text(this.font, hearts, x, y, INK, false);
        if (this.mealHover != null) {
            return this.mealHover;
        }
        if (leanHover != null) {
            return leanHover;
        }
        if (!inside(mouseX, mouseY, x, y, this.font.width(hearts), this.font.lineHeight)) {
            return null;
        }
        return heartTip(variety);
    }

    /** What this row is doing, then its share of hearts. */
    private static List<Component> groupTip(FoodGroup group, int points, boolean fed) {
        BoardLines.RowTip tip = BoardLines.groupTip(group, points, fed);
        Component first = tip.argument() == null
                ? Component.translatable(tip.key())
                : Component.translatable(tip.key(), Component.translatable(tip.argument()));
        return List.of(
                first,
                Component.translatable(
                        "feast_dominion.wellbeing.group.weight",
                        Constitution.percent(Constitution.GROUP_WEIGHT)
                )
        );
    }

    /** What thirst is doing, then the fact that is not already on the first line. */
    private static List<Component> thirstTip(int points) {
        BoardLines.RowTip tip = BoardLines.thirstTip(points);
        String other = points <= 0
                ? "feast_dominion.wellbeing.thirst.held"
                : "feast_dominion.wellbeing.thirst.role";
        return List.of(Component.translatable(tip.key()), Component.translatable(other));
    }

    private static List<Component> leanTip() {
        return List.of(Component.translatable("feast_dominion.wellbeing.lean.tip"));
    }

    /** The groups, the tastes, and the recent-food windows that produced the number on the line. */
    private static List<Component> heartTip(VarietyView.Snapshot variety) {
        HealthSettings settings = FeastConfig.health();
        return List.of(
                Component.translatable(
                        "feast_dominion.wellbeing.hearts.range",
                        Variety.formatHearts(settings.floor()),
                        Variety.formatHearts(settings.ceiling())
                ),
                Component.translatable(
                        "feast_dominion.wellbeing.hearts.groups",
                        Constitution.percent(Constitution.GROUP_WEIGHT)
                ),
                Component.translatable(
                        "feast_dominion.wellbeing.hearts.tastes",
                        Constitution.percent(Constitution.TASTE_WEIGHT)
                ),
                Component.translatable(
                        "feast_dominion.wellbeing.hearts.recent",
                        Constitution.percent(Constitution.RECENT_WEIGHT),
                        variety.dayWeight(),
                        variety.weekWeight(),
                        variety.monthWeight()
                ),
                Component.translatable(
                        "feast_dominion.wellbeing.hearts.today",
                        Variety.DAY_GROUPS,
                        Variety.DAY_HUNGER
                ),
                windowTip("feast_dominion.wellbeing.week", Variety.WEEK_DAYS, Variety.WEEK_GROUPS, Variety.WEEK_HUNGER),
                windowTip("feast_dominion.wellbeing.month", Variety.MONTH_DAYS, Variety.MONTH_GROUPS, Variety.MONTH_HUNGER),
                Component.translatable("feast_dominion.wellbeing.hearts.cap")
        );
    }

    /** The tastes that counted this month. A full row still has no curse. */
    private static List<Component> tasteTip(int mask) {
        MutableComponent names = Component.empty();
        boolean any = false;
        for (Taste taste : Taste.values()) {
            if ((mask & (1 << taste.ordinal())) == 0) {
                continue;
            }
            if (any) {
                names.append(", ");
            }
            names.append(Component.translatable("feast_dominion.taste." + taste.name().toLowerCase(Locale.ROOT)));
            any = true;
        }
        Component line = any
                ? Component.translatable("feast_dominion.wellbeing.tastes.some", names)
                : Component.translatable("feast_dominion.wellbeing.tastes.none");
        return List.of(
                line,
                Component.translatable(
                        "feast_dominion.wellbeing.tastes.weight",
                        Constitution.percent(Constitution.TASTE_WEIGHT)
                )
        );
    }

    private static Component windowTip(String name, int days, int groups, int hunger) {
        return Component.translatable(
                "feast_dominion.wellbeing.hearts.window",
                Component.translatable(name),
                groups,
                hunger,
                days
        );
    }

    /** Filled cells are that list's icons. A larger cell count leaves the rest empty. Today and the week pass their own length. */
    private int drawWindow(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            String label,
            List<String> items,
            int cells,
            float mouseX,
            float mouseY
    ) {
        graphics.text(this.font, Component.translatable(label), x, y, INK, false);
        y += 12;
        List<String> shown = items == null ? List.of() : items;
        int rows = rows(cells);
        for (int i = 0; i < cells; i++) {
            int cellX = x + (i % COLUMNS) * SLOT_SIZE;
            int cellY = y + (i / COLUMNS) * SLOT_SIZE;
            graphics.fill(cellX, cellY, cellX + SLOT_SIZE, cellY + SLOT_SIZE, CELL_EDGE);
            graphics.fill(cellX + 1, cellY + 1, cellX + SLOT_SIZE - 1, cellY + SLOT_SIZE - 1, CELL);
            if (i < shown.size()) {
                ItemStack stack = icon(shown.get(i));
                if (!stack.isEmpty()) {
                    drawIcon(graphics, stack, cellX + 1, cellY + 1);
                }
                if (inside(mouseX, mouseY, cellX, cellY, SLOT_SIZE, SLOT_SIZE)) {
                    this.mealHover = mealTip(shown.get(i), stack);
                }
            }
        }
        return y + rows * SLOT_SIZE + 4;
    }

    /** The tastes on this food, and the hunger the diet counted for it. */
    private static List<Component> mealTip(String id, ItemStack stack) {
        List<Component> lines = new ArrayList<>();
        if (stack != null && !stack.isEmpty()) {
            lines.add(stack.getHoverName());
        } else if (id != null && !id.isBlank()) {
            lines.add(Component.literal(id));
        }
        TasteProfile profile = stack == null || stack.isEmpty() ? TasteProfile.EMPTY : TasteCatalog.of(stack);
        MutableComponent tastes = null;
        for (Taste taste : profile.shown()) {
            MutableComponent bit = Component.translatable(
                    "feast_dominion.wellbeing.meal.taste",
                    Component.translatable("feast_dominion.taste." + taste.name().toLowerCase(Locale.ROOT)),
                    Math.round(profile.get(taste) * 100.0F)
            );
            tastes = tastes == null ? bit : tastes.append(Component.literal(", ")).append(bit);
        }
        if (profile.poisonous()) {
            MutableComponent name = Component.translatable("feast_dominion.taste.poisonous");
            tastes = tastes == null ? name : tastes.append(Component.literal(", ")).append(name);
        }
        if (tastes != null) {
            lines.add(tastes.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(TASTE_INK))));
        }
        MutableComponent groups = GroupTooltip.line(stack);
        if (groups != null) {
            lines.add(groups.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(GROUP_INK))));
        }
        int nutrition = 0;
        if (stack != null && !stack.isEmpty()) {
            FoodProperties food = stack.get(DataComponents.FOOD);
            nutrition = food == null ? 0 : food.nutrition();
        }
        lines.add(Component.translatable("feast_dominion.wellbeing.meal.hunger", DietLevels.serving(nutrition))
                .withStyle(ChatFormatting.GRAY));
        return lines;
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

    /** Largest scale that keeps the panel inside the GUI, and never larger than one. */
    private static float fitScale(int guiWidth, int guiHeight, int panelWidth, int panelHeight) {
        float roomX = guiWidth - MARGIN * 2f;
        float roomY = guiHeight - MARGIN * 2f;
        if (panelWidth <= 0 || panelHeight <= 0 || roomX <= 0f || roomY <= 0f) {
            return 1f;
        }
        return Math.min(1f, Math.min(roomX / panelWidth, roomY / panelHeight));
    }

    /** Centers the scaled panel, and keeps at least {@link #MARGIN} pixels of the GUI around it. */
    private static float fitOrigin(int gui, int panel, float scale) {
        if (scale >= 1f) {
            return Math.max(MARGIN, (gui - panel) / 2);
        }
        return Math.max(MARGIN, (gui - panel * scale) / 2f);
    }

    private static void pushFit(GuiGraphicsExtractor graphics, float x, float y, float scale) {
        //? if >=1.21.6 {
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        //?} else {
        /*graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0);
        graphics.pose().scale(scale, scale, 1.0F);
        *///?}
    }

    private static void popFit(GuiGraphicsExtractor graphics) {
        //? if >=1.21.6 {
        graphics.pose().popMatrix();
        //?} else
        //graphics.pose().popPose();
    }

    private int leftHeight(boolean fed, boolean showLean) {
        int height = ROW;
        if (this.player() != null) {
            if (showLean && this.leanLine() != null) {
                height += ROW;
            }
            height += ROW;
        }
        height += FoodGroup.values().length * ROW;
        height += ROW;
        if (fed) {
            height += ROW;
        }
        return height;
    }

    private static int rightHeight(boolean lean) {
        VarietyView.Snapshot variety = VarietyView.current();
        int todayRows = rows(size(variety.todayItems()));
        int weekRows = rows(size(variety.weekItems()));
        int monthRows = rows(MealSlots.MONTH);
        int head = (lean ? ROW : 0) + ROW;
        return head + (12 + todayRows * SLOT_SIZE + 4) + (12 + weekRows * SLOT_SIZE + 4) + (12 + monthRows * SLOT_SIZE + 4) + 6 + ROW;
    }

    private static int size(List<String> items) {
        return items == null ? 0 : items.size();
    }

    private static int rows(int count) {
        if (count <= 0) {
            return 0;
        }
        return (count + COLUMNS - 1) / COLUMNS;
    }

    private Component leanLine() {
        Player player = this.player();
        if (player == null) {
            return null;
        }
        Taste lean = Services.LEAN.taste(player);
        if (lean == null) {
            return null;
        }
        return Component.translatable(
                "feast_dominion.wellbeing.lean",
                Component.translatable("feast_dominion.taste." + lean.name().toLowerCase(Locale.ROOT))
        );
    }

    private int nameColumn() {
        int width = this.font.width(Component.translatable("feast_dominion.wellbeing.thirst"));
        width = Math.max(width, this.font.width(Component.translatable("feast_dominion.wellbeing.tastes")));
        for (FoodGroup group : FoodGroup.values()) {
            Component name = Component.translatable("feast_dominion.wellbeing.group." + group.name().toLowerCase(Locale.ROOT));
            width = Math.max(width, this.font.width(name));
        }
        return width + 6;
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
