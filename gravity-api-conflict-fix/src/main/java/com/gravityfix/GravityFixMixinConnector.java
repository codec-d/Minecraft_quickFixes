package com.gravityfix;

import org.spongepowered.asm.mixin.MixinEnvironment;
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

        // Add our mixin config with priority 0 (highest precedence)
        Mixins.addConfiguration("gravityfix.mixins.json");

        // Try to register error handler class directly
        try {
            Mixins.registerErrorHandlerClass("com.gravityfix.GravityFixErrorHandler");
            System.out.println("[GravityFix] Error handler registered successfully");
        } catch (Throwable t) {
            System.err.println("[GravityFix] Could not register error handler directly: " + t.getMessage());
            System.out.println("[GravityFix] Error handler will be registered via ServiceLoader");
        }

        System.out.println("[GravityFix] Mixin config registered via connector");
    }
}
