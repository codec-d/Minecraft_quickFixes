# Minecraft_quickFixes

Collection of bespoke mods that fix simple compatibility issues between Minecraft mods.

## Available Fixes

### [Gravity API Conflict Fix](./gravity-api-conflict-fix/)

Resolves the `@Redirect` mixin conflict between **Beyond the Abyss** and **JCraft** mods for Minecraft 1.20.1 (Forge 47.4.x).

Both mods include Gravity API which causes a duplicate mixin error:
```
@Redirect::redirect_dropItem_new_0 with priority 1000, already redirected by
gravityapi.mixin.json:PlayerMixin from mod gravityapi with priority 1001
```

Install this hotfix mod alongside both mods to resolve the conflict.
