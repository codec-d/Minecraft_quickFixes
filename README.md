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

This mod uses **aggressive runtime reflection** to forcefully modify gravityapi's mixin configuration:

1. **Mixin Plugin loads early** during the mixin initialization phase
2. **Uses reflection** to access SpongePowered Mixin's internal configuration system
3. **Finds gravityapi.mixin.json** and modifies its `defaultRequire` setting from 1 to 0
4. **Prevents crash** by making gravityapi's redirect non-required
5. **SolomonLib continues normally** - its redirect works as intended

This is an aggressive, invasive approach that directly manipulates internal Mixin state.

## Installation

### Option 1: Download Pre-built JAR (Recommended)

1. Go to the [Actions tab](../../actions) on GitHub
2. Click on the latest successful build
3. Download the `gravityfix-mod` artifact
4. Extract the JAR file from the zip
5. Copy it to your Minecraft `mods` folder
6. Keep both `gravityapi` and `solomonlib` in your mods` folder (don't remove them)
7. Launch the game - GravityFix will modify gravityapi's config during mixin loading
8. Game should launch successfully (no restart needed)

**Note:** Check logs for `[GravityFix] *** SUCCESSFULLY MODIFIED defaultRequire! ***` to confirm the fix worked.

### Option 2: Build from Source

1. Clone this repository
2. Build using `./gradlew build`
3. Copy the JAR from `build/libs/` to your Minecraft `mods` folder
4. Keep both `gravityapi` and `solomonlib` in your mods folder (don't remove them)
5. Launch the game - GravityFix will update Radium configuration
6. **RESTART Minecraft** - the conflict will be resolved after the second launch

## Alternative Solution (Not Recommended if Other Mods Depend on GravityAPI)

If **no other mods** depend on the old GravityAPI, you can simply **remove it** from your mods folder, as its functionality is fully integrated into SolomonLib. However, if other mods have it as a dependency, you **must** use the GravityFix compatibility mod instead.

## Technical Details

- **Minecraft Version:** 1.20.1
- **Forge Version:** 47.4.13
- **Approach:** Aggressive reflection-based modification of Mixin internals
- **Technique:** IMixinConfigPlugin that runs during mixin initialization
- **Target:** Modifies gravityapi's `InjectorOptions.defaultRequire` from 1 to 0
- **Load Order:** Loads during mixin phase (before mod initialization)
- **No game mixins:** This mod doesn't inject into Minecraft code
- **Invasiveness:** HIGH - directly manipulates SpongePowered Mixin's internal state
- **Result:** gravityapi's redirect becomes non-required, allowing the conflict to be ignored

## References

- [GravityAPI (discontinued)](https://www.curseforge.com/minecraft/mc-mods/gravity-api-forge) - Merged into SolomonLib
- [SolomonLib](https://github.com/min2222/SolomonLib) - Current maintained version
- [Beyond the Abyss](https://github.com/min2222/Lovecraft-Beyond-the-Abyss) - Uses SolomonLib

## License

MIT License
