# Build / verification notes

Environment: Linux x86_64, Temurin JDK 21.0.13, Gradle 8.14.2, NeoForge 21.1.267, ModDevGradle 2.0.148.

## What was verified

- `javac --release 21` compiles all five source files without errors (manual classpath: NeoForge universal, FancyModLoader loader, mergetool, Mixin 0.8.7, ASM tree, slf4j).
- `build-manual.sh` reproduces `build/libs/prccompat-1.0.0.jar` from a clean output directory.
- `prccompat.mixins.json`, `pack.mcmeta`, and `META-INF/neoforge.mods.toml` parse successfully.
- `PrcMixinPlugin.shouldApplyMixin` returns `false` without Real Camera on the classpath and `true` with `realcamera-1.21.1-neoforge-0.7.8-beta.jar` present.
- `RealCameraCore.isRendering()` exists with signature `public static boolean isRendering()` in Real Camera 0.7.8-beta, so the target descriptor `isRendering()Z` is correct.

## What was not verified here

- No Minecraft client was launched (headless build host), so the mixin has not been observed in-game.
- Real Camera's camera binding path (`computeCamera`) is independent of `isRendering()`; it renders the camera entity into a vertex catcher and still sees Punchy! Moves' `LivingEntityRendererMixin` pose.

## Environment caveat

`./gradlew build` (ModDevGradle) could not complete on this build host because
`maven.neoforged.net` is blocked by the network proxy and the NFRT artifact
`net.neoforged:neoform-runtime:2.0.31` is not present on the CERNET/BMCAPI mirror.
The fallback `build-manual.sh` was used for the shipped jar; it compiles the same
source set against the NeoForge universal jar and FancyModLoader/Mixin APIs.
