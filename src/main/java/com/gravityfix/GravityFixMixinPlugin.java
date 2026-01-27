package com.gravityfix;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GravityFixMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        GravityFix.LOGGER.info("[GravityFix] Mixin plugin loading, package: {}", mixinPackage);

        // Register our error handler to suppress gravityapi mixin failures
        try {
            Mixins.addErrorHandlerClass("com.gravityfix.GravityFixErrorHandler");
            GravityFix.LOGGER.info("[GravityFix] Registered error handler to suppress gravityapi conflicts");
        } catch (Exception e) {
            GravityFix.LOGGER.error("[GravityFix] Failed to register error handler", e);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // We don't have any mixins, just error handling
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // No special target handling needed
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No mixins to apply
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No post-apply actions needed
    }
}
