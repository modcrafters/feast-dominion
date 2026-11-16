# Changelog

Each pull request adds its own bullets at the top of **Unreleased**, written for players. When a merge lands, the Discord message shows only the bullets that pull request added.

Push a version tag to publish. Alpha and beta releases use only **Unreleased**. Stable releases include those new notes under the tagged version, followed by every alpha/beta section since the previous stable release, newest first. The first stable release includes all earlier alpha/beta sections. The same notes go to CurseForge, Modrinth, and GitHub.

After publishing, the action commits the next development version and changelog update directly to **main**. It renames **Unreleased** to the tagged version and starts a new empty **Unreleased** above it; earlier sections stay in place without duplication. If the unpublished notes on main changed during the release, they are left untouched for manual reconciliation. A stable release with no new changes can publish the accumulated alpha/beta notes; its empty version heading still marks the cutoff for the next stable release. Concurrent pushes are never overwritten; rerun a rejected update after main advances. The action needs permission to push to main.

## Unreleased

- The seven food groups start at half. A meal fills the groups in it, and older meals drop out once you have eaten eighty hunger since then. Milk, and anything else with no hunger, counts as four hunger.
- Hearts follow those groups. Three groups with four hunger each fill the day, five groups with twelve fill the week, and all seven with twenty-eight fill the month. Hunger past that adds nothing, so more berries do not count once fruit is full. Dying keeps the groups and the hearts. Set `reset_lean_on_death` to true in `config/feast_dominion.properties` and a death clears both, and rolls a different lean.
- The wellbeing book opens on the lean and thirst, then the seven food groups. Each group is its name, then a full row of hunger icons. Without Spice of Life, a third page shows the day, the week, and the month the same way.

- Quench is the reserve drawn as an outline on the thirst droplets. It spends before thirst falls. A new life starts with that reserve full, and a second outline appears once it passes a full bar. A drink's tooltip shows the quench it restores the same way.
- Drink a water bottle as usual. Use a water bucket in the air, or while sneaking, and it becomes an empty bucket. Both restore one drink of thirst. The bucket fills three times as much quench.
- An apple, a melon slice, sweet berries, and glow berries each restore one thirst droplet.
- Food and drink tooltips show thirst as droplets under the hunger icons, two points to a droplet. The number comes from the item's data. Milk restores one drink, three droplets. A thirst number also changes the bar when you finish the item.
- You lean toward one taste. A meal that matches adds that much to its hunger and saturation, so a food that is 20% sweet restores 120%. The tooltip says "Sweet, +20%". A meal with none of that taste keeps 75% of both, and the tooltip says "not Sweet, -25%". Set `lean_miss_percent` in `config/feast_dominion.properties` to change the penalty. 100 keeps the whole meal. The wellbeing book names the lean. Dying keeps it. Set `reset_lean_on_death` to true to roll a different taste on death. Eating an item in the `feast_dominion:resets_lean` tag does the same.
- Without Spice of Life: Carrot Edition, max health follows recent meals. Nothing remembered is 7.5 hearts. With Spice of Life installed, that mod keeps the health bar and the health page stays away.
- Peaceful difficulty keeps spending thirst, hunger, and saturation. Healing continues while you have food. An empty stomach damages you down to five hearts. Set `drain_in_peaceful` to false in `config/feast_dominion.properties` for vanilla peaceful, where thirst holds still, hunger and saturation fill back up, and an empty stomach does no harm.
- Food tooltips show how much hunger and saturation a food restores, when AppleSkin is not installed. Hunger uses food icons, two points each. Saturation draws an outline on those icons and continues when it fills more icons than the hunger does. A leftover that does not fill an icon is half an outline. Saturation past a full bar, 20 points, adds a larger outline on top of the icons.
- Saturation shows on the hunger shanks, and stays current as it changes, when AppleSkin is not installed.

## 0.0.1-alpha

- Thirst drains over half a Minecraft day. Wet places are slower. Deserts, badlands, savannas, and the Nether are faster. An empty bar gives Weakness.
- Hold right-click for two seconds on a block face that has water beside it, with an empty hand. The hand reaches to the water, then comes up to your mouth. Fresh water raises thirst by three droplets. Sea and shore water lowers it by the same. Letting go early stops the drink.
- A thirst bar sits just above hunger. A new life starts with it full.
- A wellness bar sits just above your hearts, the same width as health. It shows the middle of the seven food groups, and it starts half full.
