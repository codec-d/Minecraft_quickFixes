# GravityFix - Compatibility Mod

A compatibility mod that fixes mixin conflicts between the deprecated GravityAPI mod and SolomonLib.

## The Problem

The old standalone [GravityAPI (Forge) mod](https://www.curseforge.com/minecraft/mc-mods/gravity-api-forge) was discontinued and its functionality was merged into [SolomonLib](https://github.com/min2222/SolomonLib). However, if both mods are loaded together, they create a mixin conflict:

```
@Redirect conflict. Skipping gravityapi.mixin.json:PlayerMixin from mod gravityapi
already redirected by solomonlib.mixin.json:gravity.PlayerMixin from mod solomonlib
```

Both mixins try to modify the same `Player.drop()` method with the same priority (1001), causing the game to crash on startup.

## The Solution

This mod provides a **high-priority mixin wrapper** (priority 2000) that loads before both conflicting mixins:

1. **Prevents the conflict** by being the first to redirect the `ItemEntity` creation
2. **Delegates to SolomonLib** for gravity handling (the newer, maintained implementation)
3. **Blocks the old gravityapi** mixin from applying

## Installation

### Option 1: Download Pre-built JAR (Recommended)

1. Go to the [Actions tab](../../actions) on GitHub
2. Click on the latest successful build
3. Download the `gravityfix-mod` artifact
4. Extract the JAR file from the zip
5. Copy it to your Minecraft `mods` folder
6. Keep both `gravityapi` and `solomonlib` in your mods folder (don't remove them)
7. Launch the game - the conflict will be resolved

### Option 2: Build from Source

1. Clone this repository
2. Build using `./gradlew build`
3. Copy the JAR from `build/libs/` to your Minecraft `mods` folder
4. Keep both `gravityapi` and `solomonlib` in your mods folder (don't remove them)
5. Launch the game - the conflict will be resolved

## Alternative Solution (Not Recommended if Other Mods Depend on GravityAPI)

If **no other mods** depend on the old GravityAPI, you can simply **remove it** from your mods folder, as its functionality is fully integrated into SolomonLib. However, if other mods have it as a dependency, you **must** use the GravityFix compatibility mod instead.

## Technical Details

- **Minecraft Version:** 1.20.1
- **Forge Version:** 47.4.13
- **Mixin Priority:** 2000 (higher than both gravityapi and solomonlib at 1001)
- **Load Order:** AFTER gravityapi and solomonlib

## References

- [GravityAPI (discontinued)](https://www.curseforge.com/minecraft/mc-mods/gravity-api-forge) - Merged into SolomonLib
- [SolomonLib](https://github.com/min2222/SolomonLib) - Current maintained version
- [Beyond the Abyss](https://github.com/min2222/Lovecraft-Beyond-the-Abyss) - Uses SolomonLib

## License

MIT License
