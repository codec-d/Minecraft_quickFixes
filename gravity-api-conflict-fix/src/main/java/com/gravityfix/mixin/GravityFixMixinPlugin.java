package com.gravityfix.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Mixin Config Plugin for the Gravity API Conflict Fix.
 *
 * This plugin tracks whether the gravity drop redirect has already been
 * registered and can be used by other parts of the fix to coordinate.
 */
public class GravityFixMixinPlugin implements IMixinConfigPlugin {

    /**
     * Tracks whether ANY gravity PlayerMixin has been applied.
     * This is checked by our coremod to determine which copy to modify.
     */
    public static final AtomicBoolean GRAVITY_REDIRECT_REGISTERED = new AtomicBoolean(false);

    @Override
    public void onLoad(String mixinPackage) {
        System.out.println("[GravityFix] Mixin plugin loaded for package: " + mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // We let all our mixins apply
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // No-op
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No-op
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No-op
    }
}
