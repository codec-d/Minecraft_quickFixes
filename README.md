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

1. Build this mod using `./gradlew build`
2. Copy the JAR from `build/libs/` to your Minecraft `mods` folder
3. Keep both `gravityapi` and `solomonlib` in your mods folder (don't remove them)
4. Launch the game - the conflict will be resolved

## Alternative Solution

If you prefer a cleaner setup, you can simply **remove the old GravityAPI mod** from your mods folder, as its functionality is now fully integrated into SolomonLib.

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
