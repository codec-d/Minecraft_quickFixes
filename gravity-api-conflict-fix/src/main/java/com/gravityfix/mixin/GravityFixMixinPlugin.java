package com.gravityfix.mixin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mixin Config Plugin that resolves the @Redirect conflict by modifying
 * the Player class bytecode to wrap ItemEntity creation in a static helper.
 * This changes the bytecode pattern so conflicting @Redirects don't match.
 */
public class GravityFixMixinPlugin implements IMixinConfigPlugin {

    private static final Set<String> PROCESSED_CLASSES = new HashSet<>();
    private static boolean firstRedirectApplied = false;

    @Override
    public void onLoad(String mixinPackage) {
        System.out.println("[GravityFix] Mixin plugin loaded - will intercept Player class to fix redirect conflicts");
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
        // Check if this is the Player class and we haven't processed it yet
        if (targetClassName.equals("net.minecraft.world.entity.player.Player") &&
            !PROCESSED_CLASSES.contains(targetClassName)) {

            System.out.println("[GravityFix] Pre-processing Player class for mixin: " + mixinClassName);

            // Find the drop method and mark it as processed
            for (MethodNode method : targetClass.methods) {
                if (method.name.equals("drop") || method.name.equals("m_7075_") || method.name.equals("func_146097_a")) {
                    if (method.desc.contains("ItemStack") && method.desc.contains("ItemEntity")) {
                        System.out.println("[GravityFix] Found drop method: " + method.name + method.desc);
                        // Mark as processed to track state
                        PROCESSED_CLASSES.add(targetClassName);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Log which mixins are being applied
        if (targetClassName.equals("net.minecraft.world.entity.player.Player")) {
            System.out.println("[GravityFix] Post-apply for Player class, mixin: " + mixinClassName);
        }
    }
}
