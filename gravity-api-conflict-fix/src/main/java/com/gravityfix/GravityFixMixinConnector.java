package com.gravityfix;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

/**
 * Mixin connector that runs early during Forge startup.
 * This is specified in the JAR manifest and runs before regular mod loading.
 */
public class GravityFixMixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        System.out.println("[GravityFix] Mixin connector initializing");

        // Add our mixin config
        Mixins.addConfiguration("gravityfix.mixins.json");

        System.out.println("[GravityFix] Mixin config registered via connector");
    }
}
