# Feast Dominion

A Minecraft Java mod for Fabric and NeoForge. The body keeps its own vigil. Food has a taste, a thirst value, and one or two ingredient categories. The player has one taste they lean toward, a thirst that can run dry, and a recent memory of seven kinds of food. A lean that matches the meal fills them better. A category that falls very low curses them. All seven high, and they are well fed.

## Import and run in IntelliJ IDEA

IDEA 2025.2 or newer is required for Java 25.

1. Open this folder (the one that contains `gradlew.bat`) as a project.
2. Set the Project SDK and the Gradle JVM to Java 25:
   - `File > Project Structure > Project SDK`
   - `File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM`
3. Install the **Stonecutter Dev** plugin (optional; it highlights the `//?` version comments).
4. Refresh the Gradle project.
5. Run one of these configurations:
   - **Fabric Client** or **Fabric Server** (Gradle run configurations)
   - **NeoForge Client** or **NeoForge Server** (Application run configurations)
   - **NeoForge Client Sol Carrot** loads Spice of Life: Carrot Edition. **NeoForge Client Delight** loads Farmer's Delight. **NeoForge Client Croptopia** loads Croptopia and EpheroLib. **NeoForge Client Integrations** loads those mods and AppleSkin. Delight is part of the 1.21.1 runs. 26.3 has no Farmer's Delight build, so that run is absent there. Croptopia is part of both. On 1.21.1 these are Application runs. On 26.3 Sol Carrot, Croptopia, and Integrations are the Gradle tasks `runClientSolCarrot`, `runClientCroptopia`, and `runClientIntegrations`. **Fabric Client Croptopia** loads the Fabric jars on 26.3.

### How the build is laid out

The build uses [Stonecutter](https://stonecutter.kikugie.dev) so one codebase can build for several Minecraft versions:

- The common code is in `src/`. Fabric code is in `fabric/src`, and NeoForge code is in `neoforge/src`.
- Each Minecraft version is its own Gradle project: `:26.3` and `:1.21.1` (common), `:fabric:26.3`, `:neoforge:26.3` and `:neoforge:1.21.1`. Fabric is 26.3 only. Each one's `gradle.properties` under `versions/<mc>` holds that version's Minecraft, Java and loader versions.
- Code that differs between versions is marked with comments such as `//? if >=1.21.6 {`. The IDE always shows one version, the *active* one.
- Switch with the Gradle task `Set active project to <mc>`. Run `Reset active project` before you commit: git must always hold the 26.3 state, and CI checks this.

`gradlew collectJars` builds every loader jar and copies them to `build/libs/<mod version>/`. Each jar's own build output is under `<loader>/versions/<mc>/build/libs`. The common jars are not mods you install.

`gradlew :26.3:test` and `gradlew :1.21.1:test` run the unit tests. CI (`.github/workflows/ci.yml`) builds Fabric and NeoForge 26.3 and NeoForge 1.21.1 on every pull request. The `all-versions` check passes only when every version passed; it is the check for a ruleset on `main` to require.
