package com.gravityfix;

import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Custom error handler that catches redirect conflicts and injection failures,
 * allowing the game to continue instead of crashing.
 */
public class GravityFixErrorHandler implements IMixinErrorHandler {

    static {
        System.out.println("[GravityFix] Error handler class loaded");
    }

    public GravityFixErrorHandler() {
        System.out.println("[GravityFix] Error handler instantiated");
    }

    @Override
    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
        System.out.println("[GravityFix] onPrepareError called for: " +
            (mixin != null ? mixin.getClassName() : "unknown") +
            ", error: " + (th != null ? th.getClass().getSimpleName() : "null"));

        if (shouldSuppress(th, mixin)) {
            System.out.println("[GravityFix] Suppressing prepare error, returning WARN");
            return ErrorAction.WARN;
        }
        return action;
    }

    @Override
    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
        System.out.println("[GravityFix] onApplyError called for target: " + targetClassName +
            ", mixin: " + (mixin != null ? mixin.getClassName() : "unknown") +
            ", error: " + (th != null ? th.getClass().getSimpleName() : "null"));

        if (shouldSuppress(th, mixin)) {
            System.out.println("[GravityFix] Suppressing apply error, returning WARN");
            return ErrorAction.WARN;
        }
        return action;
    }

    private boolean shouldSuppress(Throwable th, IMixinInfo mixin) {
        // Check if this is from gravityapi
        if (mixin != null) {
            String mixinName = mixin.getClassName();
            String configName = mixin.getConfig() != null ? mixin.getConfig().getName() : "";

            if (configName.contains("gravityapi") || mixinName.contains("gravity")) {
                System.out.println("[GravityFix] Detected gravityapi mixin, checking error type");
                if (isInjectionFailure(th)) {
                    return true;
                }
            }
        }

        // Also check error message for any gravity-related redirect conflicts
        return isGravityRedirectConflict(th);
    }

    private boolean isInjectionFailure(Throwable th) {
        if (th == null) return false;

        // Check exception type
        String className = th.getClass().getName();
        if (className.contains("InjectionError") || className.contains("InjectionException")) {
            return true;
        }

        // Check message
        String message = th.getMessage();
        if (message != null) {
            if (message.contains("injection failure") ||
                message.contains("failed injection check") ||
                message.contains("redirect_dropItem") ||
                message.contains("Redirector") ||
                message.contains("0/1") ||
                message.contains("succeeded")) {
                return true;
            }
        }

        // Check cause
        Throwable cause = th.getCause();
        if (cause != null && cause != th) {
            return isInjectionFailure(cause);
        }

        return false;
    }

    private boolean isGravityRedirectConflict(Throwable th) {
        if (th == null) return false;

        String message = th.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            if ((lowerMessage.contains("redirect") && lowerMessage.contains("conflict")) ||
                lowerMessage.contains("redirect_dropitem") ||
                (lowerMessage.contains("gravity") && lowerMessage.contains("redirect"))) {
                return true;
            }
        }

        Throwable cause = th.getCause();
        if (cause != null && cause != th) {
            return isGravityRedirectConflict(cause);
        }

        return false;
    }
}
