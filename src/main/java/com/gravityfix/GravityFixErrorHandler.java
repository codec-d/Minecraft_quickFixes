package com.gravityfix;

import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class GravityFixErrorHandler implements IMixinErrorHandler {

    @Override
    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
        // Check if this is the gravityapi PlayerMixin that's failing
        if (config.getName().equals("gravityapi.mixin.json") &&
            mixin.getClassName().contains("PlayerMixin")) {

            GravityFix.LOGGER.warn("[GravityFix] Suppressing gravityapi PlayerMixin preparation error (expected conflict with SolomonLib)");
            GravityFix.LOGGER.warn("[GravityFix] GravityAPI functionality is provided by SolomonLib instead");

            // Suppress the error - this is expected behavior
            return ErrorAction.NONE;
        }

        // Let other errors through with their original action
        return action;
    }

    @Override
    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
        // Check if this is the gravityapi PlayerMixin failing on Player class
        if (mixin.getConfig().getName().equals("gravityapi.mixin.json") &&
            mixin.getClassName().contains("PlayerMixin") &&
            targetClassName.contains("Player")) {

            GravityFix.LOGGER.warn("[GravityFix] Suppressing gravityapi PlayerMixin application error on {} (expected conflict with SolomonLib)", targetClassName);
            GravityFix.LOGGER.warn("[GravityFix] GravityAPI functionality is provided by SolomonLib instead");

            // Suppress the error - this is expected behavior
            return ErrorAction.NONE;
        }

        // Let other errors through with their original action
        return action;
    }
}
