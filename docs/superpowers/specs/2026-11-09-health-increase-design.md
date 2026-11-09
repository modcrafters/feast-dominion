# Recent variety and max health

Design agreed with Face on 2026-11-09. This replaces the "no health bonus" line in the private design for the case where Spice of Life: Carrot Edition is absent. Taste, thirst, and the seven categories stay as they are.

## Why

The extra hearts should make you cook and grow many things, and keep doing it. A lifetime list of foods gets finished, and then the same few meals are fine. Here the hearts follow the last day, the last week, and the last month. Spice of Life: Carrot Edition already grants permanent hearts for a lifetime list. When that mod is installed, it owns the health bar and this feature stays off. (Face: "this feature should only run when SoL IS NOT INSTALLED... if SoL is installed it completely replaces this feature".)

## What the player feels

Hearts slide in a straight line from a floor to a ceiling.

- The floor is an empty record. Nothing eaten in the three windows, including a brand new life, and including a player whose meals are all older than 28 days. Default **7.5 hearts**. (Face: "the not eaten ever value should be like 7.5 hearts".)
- The ceiling is every window full. Default **20 hearts**, twice vanilla. (Face: "have like double vanilla hearts?".)
- One day of 3 different foods or drinks, with only those same 3 inside the week and the month, is about **15 hearts** with the default weights.
- A full week and a full month, before anything is eaten today, is **13.75 hearts** with the default weights.

Foods and drinks both count. (Face: "or could be foods/drinks".)

## When Spice of Life is installed

On startup, if `solcarrot` is loaded, log one warning that Spice of Life: Carrot Edition is installed and Feast Dominion is leaving max health alone. Do not apply a modifier. The floor, the weights, and the ceiling do nothing while it is loaded. (Face: "when our mod load we emit a warning in logs" and "only make them matter if SoL is not installed".)

The current `HealthIncreaseFeature` turns on when that mod is present. This design reverses that guard.

## The three windows

A day is a Minecraft day. **(my call.** Played back and kept.) The clock is the overworld's day count, so the Nether and the End share it. **(my call)**

Each food or drink remembers the day it was last consumed, and that day stays on record even when it was a thousand days ago. It no longer affects hearts once it is too old. There is no year score. (Face: "drop year, could just record it as ate this 1000 day ago".)

Age is whole days since that saved day. Age 0 is today.

- **Today:** age 0. Full at 3 different foods or drinks.
- **Week:** age 0 through 6, seven days. Full at 10.
- **Month:** age 0 through 27, twenty-eight days, four weeks. Full at 20. (Face: "4 weeks = 28 days".)

A thing consumed today sits in all three windows. The same three meals every day keep today full and leave the week and the month at 3, so the bar never reaches the ceiling. Something last consumed 7 days ago has left the week and still counts for the month. Something last consumed 28 days ago has left the month. The saved day remains.

The caps 3, 10, and 20, and the lengths 1, 7, and 28, stay fixed. (Played back. Face: "looks good".)

## The score

Each window is its count divided by its cap, stopped at 1. The weights are whole numbers. The default is **50** for the day, **30** for the week, and **20** for the month. (Face: "20-30-50 (month-week-day)".)

```
today  = min(1, unique today  / 3)
week   = min(1, unique week   / 10)
month  = min(1, unique month  / 20)
sum    = dayWeight + weekWeight + monthWeight
score  = (dayWeight * today + weekWeight * week + monthWeight * month) / sum
```

The sum is the "1". Defaults add to 100, so a full day alone is 50/100 of the way up. Weights of 50, 30, and 30 add to 110, and a full day alone is 50/110 of the way up. All three windows full is still the ceiling, because the top of the sum is the sum itself. (Face: "if someone says 30-30-50 then the weights are still 0..1 but 1 is 110 not 100".)

A sum of 0 leaves the score at 0, so the bar stays on the floor. **(my call)**

```
hearts = floor + score * (ceiling - floor)
```

## Config

`config/feast_dominion.properties`, beside `drain_in_peaceful`. The keys are written with the file as today. They are read only when Spice of Life is absent.

| Key | Default |
|---|---|
| `health_floor_hearts` | 7.5 |
| `health_ceiling_hearts` | 20 |
| `health_weight_day` | 50 |
| `health_weight_week` | 30 |
| `health_weight_month` | 20 |

A missing key uses its default. A weight has to be a whole number, zero or more. Anything else, including a fraction or a minus, keeps that key's default and is logged. The floor and the ceiling still accept halves. **(my call)**

## What counts as one food or drink

The player finishes consuming an item. It counts when that item has food data, or when its id is in the item tag `feast_dominion:health_drinks`. The tag starts with the vanilla drinks that are not foods: milk bucket, honey bottle, and potion. **(my call)**

Two stacks of the same item are one food, whatever their components, count, or custom name. An aged cheese and a fresh cheese of one item id are one food. **(my call)** Drinking from a water block with an empty hand does not count. That is thirst, and it consumes no item. **(my call)**

## Saved on the player

A map of item id to the overworld day it was last consumed. It survives death, because the diet attachment is copied onto the new body. **(my call.** The habit is the player's, and the "1000 days ago" record is the point of keeping the day. Face: "death should not forget things".) An empty map is the floor. Thirst still starts full on a new life.

## The modifier

While the feature is on, an additive `MAX_HEALTH` modifier `feast_dominion:health_increase` makes the bar equal the computed hearts, assuming the vanilla base of 10 hearts. 7.5 hearts is a modifier of −5 health points. 20 hearts is +20 health points. The modifier is transient, reapplied about once a second and after respawn, and removed whenever the feature is off, so adding Spice of Life later does not leave our bonus behind.

Hearts use halves. 7.5 is 15 health points. The bar shows that.

## The book

The wellbeing book, the one beside the recipe book, gains a second page while this feature is on. The first page stays thirst and the seven bands. The second page shows the three counts and the hearts they are worth right now:

- Today, as `2 / 3`
- This week, as `4 / 10`
- This month, as `6 / 20`
- Hearts, as the number the score currently gives, halves included

A note along the bottom gives the weights, day then week then month, as `50 · 30 · 20`. It uses the configured numbers, so `50 · 30 · 30` shows when that is what the file says. (Face: "sure... as a bottom note or something".)

Corner buttons turn the page, the way a written book does. **(my call)** With Spice of Life installed there is no second page and no buttons. (Face: "need a page in the book to show these numbers... only if SoL not installed".) The page does not list which foods were eaten.

## Out of scope

Taste, thirst, the seven categories, and Well Fed. Changing the lean. A year window. Configurable caps or window lengths. Running this beside Spice of Life. A list of every remembered food.
