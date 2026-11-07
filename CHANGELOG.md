# Changelog

Each pull request adds its own bullets at the top of **Unreleased**, written for players. When a merge lands, the Discord message shows only the bullets that pull request added.

Push a version tag to publish. Alpha and beta releases use only **Unreleased**. Stable releases include those new notes under the tagged version, followed by every alpha/beta section since the previous stable release, newest first. The first stable release includes all earlier alpha/beta sections. The same notes go to CurseForge, Modrinth, and GitHub.

After publishing, the action commits the next development version and changelog update directly to **main**. It renames **Unreleased** to the tagged version and starts a new empty **Unreleased** above it; earlier sections stay in place without duplication. If the unpublished notes on main changed during the release, they are left untouched for manual reconciliation. A stable release with no new changes can publish the accumulated alpha/beta notes; its empty version heading still marks the cutoff for the next stable release. Concurrent pushes are never overwritten; rerun a rejected update after main advances. The action needs permission to push to main.

## Unreleased

- Thirst drains over half a Minecraft day. Wet places are slower. Deserts, badlands, savannas, and the Nether are faster. An empty bar gives Weakness.
- Hold right-click for two seconds on a block face that has water beside it, with an empty hand. The hand reaches to the water, then comes up to your mouth. Fresh water raises thirst by three droplets. Sea and shore water lowers it by the same. Letting go early stops the drink.
- A thirst bar sits just above hunger. A new life starts with it full.
- A wellness bar sits just above your hearts, the same width as health. It shows the middle of the seven food groups, and it starts half full.
