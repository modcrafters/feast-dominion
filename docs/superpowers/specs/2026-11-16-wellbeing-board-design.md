# Wellbeing board

Agreed with Face on 2026-11-16. The inventory button opens this board instead of the written book.

## Why

The book hid the groups, the troubles, and the meals behind page turns, and its column could not hold them together. Face wants all of it in one look.

## What the player sees

The button stays where it is. The board replaces the inventory. Closing it returns to the inventory. The game keeps running.

```
Wellbeing
Lean: Sweet                 Recent meals
Thirst  ●●●●●●●○○○          Today   [apple] [bread] [ ]
Meat    ██  Weakness        Week    [apple] [bread] [steak] [carrot] [ ]×6
...                         Month   [six foods] [ ]×14
                            13.9 hearts
                            50 · 30 · 20
```

Left column, always:

- The lean.
- Thirst as ten droplets. Quench stays on the food tooltip.
- The seven groups, each with its marks. A low group names its trouble. Middle and high name that band. When all seven are high, those words become one line: Well fed. The marks stay.

Right column, only while the variety hearts are on:

- Today is 3 slots, the week is 10, the month is 20.
- A filled slot is that item's icon. The same item is one slot. A food eaten today sits in all three windows.
- Empty slots make up the rest of the cap.
- Under them, the hearts the feature is already applying, then the weights, day then week then month.

With Spice of Life installed, the right column is absent and the groups use the width.

## Approaches

- **Two-column board.** Chosen. Everything is on the screen. The month's twenty slots make that column the tall one.
- **Beside the inventory.** The inventory stays, and the column is too narrow for a mark row. Twenty slots make the panel taller than the inventory.
- **One long sheet.** The groups fit, and the meals fall below a normal window.

## Non-goals

Changing how hearts are scored. The number under the slots is the number already applied. Quench on this board. A lifetime list of foods. Foods older than 28 days. Pausing the game.

## Left to the builder

Draw each filled slot with the item renderer. Empty slots are the same cell with no item. Within a window, the newest day comes first, and the same day is ordered by item id. Store the item id and the day on the existing food log, so a death that already clears that log clears the slots too. An old save with no item list shows every slot empty and leaves the hearts as they are.
