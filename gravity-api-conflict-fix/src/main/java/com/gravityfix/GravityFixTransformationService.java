package com.gravityfix;

import cpw.mods.modlauncher.api.*;

import java.util.*;
import java.util.function.BiFunction;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Transformation service that loads early and can transform mixin classes
 * before the Mixin system processes them.
 */
public class GravityFixTransformationService implements ITransformationService {

    private static final String NAME = "gravityfix";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(IEnvironment environment) {
        System.out.println("[GravityFix] Transformation service initializing");
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {
        System.out.println("[GravityFix] Transformation service loaded");
    }

    @Override
    public void beginScanning(IEnvironment environment) {
        System.out.println("[GravityFix] Beginning scan");
    }

    @Override
    public List<Resource> completeScan(IModuleLayerManager layerManager) {
        return Collections.emptyList();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        System.out.println("[GravityFix] Registering transformers");
        return Collections.singletonList(new SolomonLibMixinTransformer());
    }

    /**
     * Transformer that removes the conflicting @Redirect method from
     * SolomonLib's PlayerMixin.
     */
    private static class SolomonLibMixinTransformer implements ITransformer<ClassNode> {

        private static final String TARGET = "com.min01.solomonlib.mixin.gravity.PlayerMixin";

        @Override
        public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
            System.out.println("[GravityFix] Transforming: " + input.name);

            Iterator<MethodNode> iter = input.methods.iterator();
            while (iter.hasNext()) {
                MethodNode method = iter.next();
                if (method.name.contains("redirect_dropItem") ||
                    (method.name.contains("dropItem") && method.name.contains("redirect"))) {
                    System.out.println("[GravityFix] Removing method: " + method.name);
                    iter.remove();
                }
            }

            return input;
        }

        @Override
        public TransformerVoteResult castVote(ITransformerVotingContext context) {
            return TransformerVoteResult.YES;
        }

        @Override
        public Set<Target> targets() {
            return Collections.singleton(Target.targetClass(TARGET));
        }
    }
}
