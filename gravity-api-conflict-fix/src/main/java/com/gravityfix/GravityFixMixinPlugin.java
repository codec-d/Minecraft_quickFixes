package com.gravityfix;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin config plugin for the GravityFix mod.
 * Registers the error handler to catch redirect conflicts.
 */
public class GravityFixMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        System.out.println("[GravityFix] Mixin plugin loading, package: " + mixinPackage);

        // Try multiple methods to ensure error handler is registered
        try {
            // Method 1: Try the Mixins.registerErrorHandlerClass API
            Mixins.registerErrorHandlerClass("com.gravityfix.GravityFixErrorHandler");
            System.out.println("[GravityFix] Error handler registered via Mixins API");
        } catch (Throwable t) {
            System.out.println("[GravityFix] Mixins.registerErrorHandlerClass not available: " + t.getClass().getSimpleName());
        }

        // Method 2: Force load the error handler class to ensure it's available for ServiceLoader
        try {
            Class<?> handlerClass = Class.forName("com.gravityfix.GravityFixErrorHandler");
            System.out.println("[GravityFix] Error handler class loaded: " + handlerClass.getName());

            // Try to instantiate to trigger the constructor message
            Object instance = handlerClass.getDeclaredConstructor().newInstance();
            System.out.println("[GravityFix] Error handler instance created: " + instance);
        } catch (Throwable t) {
            System.out.println("[GravityFix] Could not force-load error handler: " + t.getMessage());
            t.printStackTrace();
        }

        System.out.println("[GravityFix] Mixin plugin loaded");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
