package com.gravityfix;

import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Custom error handler that catches redirect conflicts and allows
 * the game to continue instead of crashing.
 */
public class GravityFixErrorHandler implements IMixinErrorHandler {

    public GravityFixErrorHandler() {
        System.out.println("[GravityFix] Error handler instantiated");
    }

    @Override
    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
        // Check if this is the redirect conflict we're trying to fix
        if (isRedirectConflict(th)) {
            System.out.println("[GravityFix] Caught redirect conflict in " +
                (mixin != null ? mixin.getClassName() : "unknown mixin") +
                ", allowing game to continue");
            return ErrorAction.WARN;
        }
        return action;
    }

    @Override
    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
        // Check if this is the redirect conflict we're trying to fix
        if (isRedirectConflict(th)) {
            System.out.println("[GravityFix] Caught redirect conflict during apply to " +
                targetClassName + ", allowing game to continue");
            return ErrorAction.WARN;
        }
        return action;
    }

    private boolean isRedirectConflict(Throwable th) {
        if (th == null) return false;

        String message = th.getMessage();
        if (message != null) {
            // Check for redirect conflict indicators
            if (message.contains("redirect_dropItem") ||
                message.contains("REDIRECT_TARGET_CONFLICT") ||
                message.contains("Redirect conflict") ||
                (message.contains("Redirect") && message.contains("conflict"))) {
                return true;
            }
        }

        // Check the cause as well
        Throwable cause = th.getCause();
        if (cause != null && cause != th) {
            return isRedirectConflict(cause);
        }

        return false;
    }
}
