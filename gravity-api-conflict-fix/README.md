# Gravity API Conflict Fix

A hotfix mod that resolves the `@Redirect` mixin conflict between **Beyond the Abyss** and **JCraft** mods for Minecraft 1.20.1 (Forge 47.4.x).

## The Problem

Both Beyond the Abyss and JCraft include the Gravity API (via SolomonLib), which has a `PlayerMixin` that uses `@Redirect` on the `ItemEntity` constructor in the `Player.drop()` method. When both mods are loaded, Mixin fails with:

```
@Redirect::redirect_dropItem_new_0(...) with priority 1000, already redirected by
gravityapi.mixin.json:PlayerMixin from mod gravityapi->@Redirect::redirect_dropItem_new_0(...)
with priority 1001
```

This happens because Mixin doesn't allow two `@Redirect` annotations on the same target bytecode.

## The Solution

This hotfix mod provides a **higher-priority mixin (priority 2000)** that supersedes both conflicting redirects. Our mixin replicates the gravity-aware item drop logic from Gravity API, ensuring proper behavior when players drop items under non-standard gravity.

## Installation

1. Download the latest release JAR
2. Place it in your `mods` folder alongside Beyond the Abyss and JCraft
3. Launch the game

## Building from Source

```bash
# Clone or download this repository
cd gravity-api-conflict-fix

# Build the mod
./gradlew build

# The JAR will be in build/libs/
```

## Compatibility

- **Minecraft**: 1.20.1
- **Forge**: 47.4.x
- **Java**: 17+
- **Beyond the Abyss**: 0.0.2
- **JCraft**: 0.17.4

## Technical Details

The fix works by:

1. Registering a `@Redirect` mixin with priority 2000 (higher than both 1000 and 1001)
2. Mixin selects the highest priority redirect, so only our version applies
3. Our redirect uses reflection to call the appropriate Gravity API methods, supporting both SolomonLib and original FusionFlux implementations

## License

MIT License
