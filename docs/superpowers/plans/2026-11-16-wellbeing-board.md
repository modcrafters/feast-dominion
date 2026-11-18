# Wellbeing Board Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Open a two-column wellbeing board from the inventory button, with the food groups on the left and food cells for today, the week, and the month on the right.

**Architecture:** The food log gains the item id and the day beside the hunger it already stores. A pure window function turns that list into three capped lists. The variety packet carries those lists, and a new screen draws them. The heart score is untouched.

**Tech Stack:** Java 21, the existing common sources, JUnit 5 via `gradlew :26.3:test`.

**Spec:** `docs/superpowers/specs/2026-11-16-wellbeing-board-design.md`

## Global Constraints

- Today holds 3 distinct items, the week 10, the month 20.
- A week is ages 0 through 6. A month is ages 0 through 27. Age 28 is gone.
- The same item id is one slot. A food eaten today is in all three windows.
- Newest day first. The same day is ordered by item id.
- The heart number and the weights are the snapshot the health feature already applies. Do not change `Variety.counts`.
- The meals column is drawn only while `HealthIncreaseFeature` is enabled.
- Thirst on the board is ten droplets. Do not draw quench.
- The screen does not pause the game, and closing it returns to the inventory.
- An old save with no item list shows every meal slot empty.

## Review Focus

- A food eaten today missing from the week and the month. A player would expect it in both longer windows.
- The same food eaten on two days filling two slots. A player would expect one slot, on the later day.
- A fourth food today still drawing a fourth slot. A player would expect the cap to hold at three.
- A food from seven days ago still sitting in the week. A player would expect it in the month only.
- The meals column still appearing while Spice of Life owns the hearts. A player would expect that column to be gone.

## File structure

- `feature/FoodLog.java` keeps `Eaten` items and `eat`.
- `feature/MealSlots.java` turns those items into three lists.
- `feature/VarietyView.java` and `network/VarietyPayload.java` carry the lists.
- `feature/HealthIncreaseFeature.java` records the item and sends the lists.
- `client/BoardLines.java` chooses the group word and whether the meals column shows.
- `client/WellbeingBoardScreen.java` replaces `client/WellbeingBookScreen.java`.
- `mixin/InventoryBookMixin.java` opens the board.

### Task 1: Remember the foods

**Files:**
- Modify: `src/main/java/net/modcrafters/minecraft/feastdominion/feature/FoodLog.java`
- Create: `src/main/java/net/modcrafters/minecraft/feastdominion/feature/MealSlots.java`
- Test: `src/test/java/net/modcrafters/minecraft/feastdominion/feature/MealSlotsTest.java`

**Interfaces:**
- Consumes: `Variety.WEEK_DAYS`, `Variety.MONTH_DAYS`
- Produces: `FoodLog.Eaten(String item, long day)`, `FoodLog.eat(long day, String item)`, `FoodLog.eaten()`, `MealSlots.TODAY = 3`, `MealSlots.WEEK = 10`, `MealSlots.MONTH = 20`, `MealSlots.Windows(List<String> today, List<String> week, List<String> month)`, `MealSlots.of(List<FoodLog.Eaten> eaten, long today)`

- [ ] **Step 1: Write the failing test**

```java
@Test
void theSameItemStaysOneSlotOnTheLaterDay() {
    FoodLog log = FoodLog.EMPTY.eat(1, "minecraft:apple").eat(2, "minecraft:apple");
    assertEquals(List.of(new FoodLog.Eaten("minecraft:apple", 2L)), log.eaten());
}

@Test
void aFoodTwentyEightDaysOldIsDropped() {
    FoodLog log = FoodLog.EMPTY.eat(1, "minecraft:apple").eat(29, "minecraft:bread");
    assertEquals(List.of("minecraft:bread"), MealSlots.of(log.eaten(), 29).month());
}

@Test
void aFoodEatenTodayIsInEveryWindow() {
    MealSlots.Windows windows = MealSlots.of(List.of(new FoodLog.Eaten("minecraft:apple", 10)), 10);
    assertEquals(List.of("minecraft:apple"), windows.today());
    assertEquals(List.of("minecraft:apple"), windows.week());
    assertEquals(List.of("minecraft:apple"), windows.month());
}

@Test
void sevenDaysAgoHasLeftTheWeek() {
    MealSlots.Windows windows = MealSlots.of(List.of(new FoodLog.Eaten("minecraft:apple", 3)), 10);
    assertEquals(List.of(), windows.week());
    assertEquals(List.of("minecraft:apple"), windows.month());
}

@Test
void todayKeepsTheThreeNewest() {
    List<FoodLog.Eaten> eaten = List.of(
            new FoodLog.Eaten("minecraft:apple", 10),
            new FoodLog.Eaten("minecraft:bread", 10),
            new FoodLog.Eaten("minecraft:carrot", 9),
            new FoodLog.Eaten("minecraft:beetroot", 8));
    assertEquals(List.of("minecraft:apple", "minecraft:bread", "minecraft:carrot"), MealSlots.of(eaten, 10).today());
}

@Test
void theWeekKeepsTen() {
    List<FoodLog.Eaten> eaten = new ArrayList<>();
    for (int i = 0; i < 11; i++) {
        eaten.add(new FoodLog.Eaten("minecraft:food_" + i, 10));
    }
    assertEquals(10, MealSlots.of(eaten, 10).week().size());
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `gradlew :26.3:test --tests net.modcrafters.minecraft.feastdominion.feature.MealSlotsTest`
Expected: FAIL, `FoodLog.eat` is missing.

- [ ] **Step 3: Implement the log and the windows**

Add `Eaten` and `eaten` to `FoodLog`. `remember` keeps the existing eaten list. `eat` ignores a blank item, replaces the day when the item is already there, and drops an item whose age is `Variety.MONTH_DAYS` or more. The codec reads `eaten` with `optionalFieldOf` defaulting to an empty list. `FoodLog.EMPTY` and every `new FoodLog(...)` pass that list through.

`MealSlots.of` keeps age 0 in today, ages 0 through 6 in the week, and ages 0 through 27 in the month. Sort by day descending, then item id ascending, and cut each list at its cap.

- [ ] **Step 4: Run the test to verify it passes**

Run: `gradlew :26.3:test --tests net.modcrafters.minecraft.feastdominion.feature.MealSlotsTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/net/modcrafters/minecraft/feastdominion/feature/FoodLog.java src/main/java/net/modcrafters/minecraft/feastdominion/feature/MealSlots.java src/test/java/net/modcrafters/minecraft/feastdominion/feature/MealSlotsTest.java
git commit -m "Remember which foods fill the recent windows."
```

### Task 2: Send the windows

**Files:**
- Modify: `src/main/java/net/modcrafters/minecraft/feastdominion/feature/VarietyView.java`
- Modify: `src/main/java/net/modcrafters/minecraft/feastdominion/network/VarietyPayload.java`
- Modify: `src/main/java/net/modcrafters/minecraft/feastdominion/feature/HealthIncreaseFeature.java`
- Test: `src/test/java/net/modcrafters/minecraft/feastdominion/feature/VarietyViewTest.java`

**Interfaces:**
- Consumes: `MealSlots.of`, `MealSlots.Windows`, `FoodLog.eat`
- Produces: `VarietyView.Snapshot` gains `List<String> todayItems`, `weekItems`, `monthItems`. `Snapshot.of(Variety.Counts counts, HealthSettings settings, MealSlots.Windows meals)`.

- [ ] **Step 1: Write the failing test**

```java
@Test
void theSnapshotKeepsTheFoods() {
    MealSlots.Windows meals = new MealSlots.Windows(List.of("minecraft:apple"), List.of(), List.of());
    VarietyView.Snapshot snapshot = VarietyView.Snapshot.of(
            new Variety.Counts(0, 0, 0), HealthSettings.DEFAULT, meals);
    assertEquals(List.of("minecraft:apple"), snapshot.todayItems());
    assertEquals(List.of(), snapshot.weekItems());
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `gradlew :26.3:test --tests net.modcrafters.minecraft.feastdominion.feature.VarietyViewTest`
Expected: FAIL, `Snapshot.of` does not take the windows.

- [ ] **Step 3: Carry the lists on the snapshot and the packet**

Add the three lists to `Snapshot`. `fresh()` uses empty lists. `VarietyPayload` writes each list as a varint length and then each item id as a UTF string, and reads them back in the same order.

In `HealthIncreaseFeature.consume`, chain `.eat(day(player), id)` onto the log that `remember` returns, before `setLog`. In `snapshot`, pass `MealSlots.of(Services.DIET.log(player).eaten(), day(player))` into `Snapshot.of`. Both client receivers already call `VarietyView.set` with the payload snapshot, so they need no new branch.

- [ ] **Step 4: Run the test to verify it passes**

Run: `gradlew :26.3:test --tests net.modcrafters.minecraft.feastdominion.feature.VarietyViewTest --tests net.modcrafters.minecraft.feastdominion.feature.VarietyTest`
Expected: PASS, including the existing heart tests.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/net/modcrafters/minecraft/feastdominion/feature/VarietyView.java src/main/java/net/modcrafters/minecraft/feastdominion/network/VarietyPayload.java src/main/java/net/modcrafters/minecraft/feastdominion/feature/HealthIncreaseFeature.java src/test/java/net/modcrafters/minecraft/feastdominion/feature/VarietyViewTest.java
git commit -m "Send the recent foods with the variety numbers."
```

### Task 3: Draw the board

**Files:**
- Create: `src/main/java/net/modcrafters/minecraft/feastdominion/client/BoardLines.java`
- Create: `src/main/java/net/modcrafters/minecraft/feastdominion/client/WellbeingBoardScreen.java`
- Delete: `src/main/java/net/modcrafters/minecraft/feastdominion/client/WellbeingBookScreen.java`
- Modify: `src/main/java/net/modcrafters/minecraft/feastdominion/mixin/InventoryBookMixin.java`
- Test: `src/test/java/net/modcrafters/minecraft/feastdominion/client/BoardLinesTest.java`

**Interfaces:**
- Consumes: `VarietyView.Snapshot.todayItems`, `weekItems`, `monthItems`, `hearts`, `dayWeight`, `weekWeight`, `monthWeight`; `MealSlots.TODAY`, `WEEK`, `MONTH`; `HealthIncreaseFeature.enabled`
- Produces: `BoardLines.mealsColumn(boolean heartsOn)`, `BoardLines.wellFed(int[] points)`, `BoardLines.groupWord(FoodGroup group, int points)`

- [ ] **Step 1: Write the failing test**

```java
@Test
void spiceOfLifeHidesTheMeals() {
    assertFalse(BoardLines.mealsColumn(false));
    assertTrue(BoardLines.mealsColumn(true));
}

@Test
void aLowGroupNamesItsTrouble() {
    assertEquals("feast_dominion.wellbeing.trouble.meat", BoardLines.groupWord(FoodGroup.MEAT, 6));
    assertEquals("feast_dominion.wellbeing.band.high", BoardLines.groupWord(FoodGroup.MEAT, 14));
}

@Test
void wellFedNeedsEveryGroupHigh() {
    int[] points = new int[FoodGroup.values().length];
    Arrays.fill(points, 14);
    assertTrue(BoardLines.wellFed(points));
    points[FoodGroup.EGGS.ordinal()] = 13;
    assertFalse(BoardLines.wellFed(points));
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `gradlew :26.3:test --tests net.modcrafters.minecraft.feastdominion.client.BoardLinesTest`
Expected: FAIL, `BoardLines` is missing.

- [ ] **Step 3: Implement the lines and the screen**

`BoardLines.mealsColumn` returns its argument. `wellFed` is true only when every point is 14 or more. `groupWord` uses the trouble key at 6 or below, `feast_dominion.wellbeing.band.high` at 14 or more, and `feast_dominion.wellbeing.band.middle` between them.

`WellbeingBoardScreen` takes the parent screen. `isPauseScreen` returns false. Close returns to that parent, using the same version split as the book screen. Draw the lean, ten thirst droplets from `ThirstIcons` with no quench, then each group name, its marks, and `BoardLines.groupWord`. When `wellFed` is true, draw one `feast_dominion.wellbeing.well_fed` line instead of those words.

When `BoardLines.mealsColumn(HealthIncreaseFeature.instance().enabled())` is true, draw the three lists from `VarietyView.current()` as item icons and empty cells out to `MealSlots.TODAY`, `WEEK`, and `MONTH`. Under them draw `Variety.formatHearts` and the three weights, day then week then month. The inventory mixin constructs this screen instead of the book screen. Delete the book screen.

- [ ] **Step 4: Run the test to verify it passes**

Run: `gradlew :26.3:test --tests net.modcrafters.minecraft.feastdominion.client.BoardLinesTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/net/modcrafters/minecraft/feastdominion/client/BoardLines.java src/main/java/net/modcrafters/minecraft/feastdominion/client/WellbeingBoardScreen.java src/main/java/net/modcrafters/minecraft/feastdominion/mixin/InventoryBookMixin.java src/test/java/net/modcrafters/minecraft/feastdominion/client/BoardLinesTest.java
git add -u src/main/java/net/modcrafters/minecraft/feastdominion/client/WellbeingBookScreen.java
git commit -m "Open the wellbeing board from the inventory."
```
