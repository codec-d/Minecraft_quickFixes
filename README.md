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

This mod automatically configures Radium to disable the conflicting mixin:

1. **Disables GravityAPI's PlayerMixin** via Radium configuration
2. **Allows SolomonLib** to handle gravity functionality normally
3. **Automatic setup** - creates/updates `config/radium.properties`
4. **Requires one restart** - configuration takes effect after relaunch

## Installation

### Option 1: Download Pre-built JAR (Recommended)

1. Go to the [Actions tab](../../actions) on GitHub
2. Click on the latest successful build
3. Download the `gravityfix-mod` artifact
4. Extract the JAR file from the zip
5. Copy it to your Minecraft `mods` folder
6. Keep both `gravityapi` and `solomonlib` in your mods folder (don't remove them)
7. Launch the game - GravityFix will update Radium configuration
8. **RESTART Minecraft** - the conflict will be resolved after the second launch

**Note:** The first launch will still crash, but GravityFix will create the configuration. The second launch will work!

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
- **Dependencies:** Requires Radium (usually already present in modpacks)
- **Approach:** Automatically configures Radium to disable gravityapi's conflicting mixin
- **Configuration:** Attempts specific overrides: `mixin.gravity.PlayerMixin=false` and `gravityapi.mixin.json:PlayerMixin=false`
- **Load Order:** AFTER gravityapi and solomonlib
- **No mixins:** This mod doesn't add any redirects or injections
- **Result:** SolomonLib's gravity system functions normally, gravityapi remains installed for dependency purposes

## References

- [GravityAPI (discontinued)](https://www.curseforge.com/minecraft/mc-mods/gravity-api-forge) - Merged into SolomonLib
- [SolomonLib](https://github.com/min2222/SolomonLib) - Current maintained version
- [Beyond the Abyss](https://github.com/min2222/Lovecraft-Beyond-the-Abyss) - Uses SolomonLib

## License

MIT License
