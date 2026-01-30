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

This mod creates an **ultra high-priority redirect** that preempts both conflicting mods:

1. **Priority 10000** - Way higher than both gravityapi (1001) and solomonlib (1001)
2. **Applies first** - Our redirect wins, both other redirects get skipped
3. **Delegates to solomonlib** - Calls solomonlib's gravity handling via reflection
4. **Falls back gracefully** - Uses vanilla behavior if solomonlib isn't available
5. **No crash** - Since our redirect applies first, no conflict occurs

This is the "preemptive interception" approach - we solve the problem by winning the priority race.

## Installation

### Option 1: Download Pre-built JAR (Recommended)

1. Go to the [Actions tab](../../actions) on GitHub
2. Click on the latest successful build
3. Download the `gravityfix-mod` artifact
4. Extract the JAR file from the zip
5. Copy it to your Minecraft `mods` folder
6. Keep both `gravityapi` and `solomonlib` in your mods folder (don't remove them)
7. Launch the game - GravityFix's high-priority mixin will preempt the conflict
8. Game should launch successfully (no restart needed)

**Note:** Check logs for `[GravityFix] Using ultra high-priority mixin (10000)` to confirm it loaded.

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
- **Approach:** Ultra high-priority mixin redirect
- **Priority:** 10000 (vs. 1001 for both gravityapi and solomonlib)
- **Target:** `Player.drop()` method - redirects `new ItemEntity()` call
- **Delegation:** Calls solomonlib's gravity API via reflection when available
- **Fallback:** Uses vanilla ItemEntity creation if solomonlib not found
- **Invasiveness:** MEDIUM - standard mixin technique, just very high priority
- **Result:** Our redirect applies first, both conflicting redirects get skipped, no crash occurs

## References

- [GravityAPI (discontinued)](https://www.curseforge.com/minecraft/mc-mods/gravity-api-forge) - Merged into SolomonLib
- [SolomonLib](https://github.com/min2222/SolomonLib) - Current maintained version
- [Beyond the Abyss](https://github.com/min2222/Lovecraft-Beyond-the-Abyss) - Uses SolomonLib

## License

MIT License
