package com.gravityfix;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * Launch plugin that removes the conflicting @Redirect method from
 * SolomonLib's PlayerMixin before Mixin processes it.
 */
public class GravityFixLaunchPlugin implements ILaunchPluginService {

    private static final String NAME = "gravityfix";
    private static final String TARGET_CLASS = "com.min01.solomonlib.mixin.gravity.PlayerMixin";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        // Handle the SolomonLib PlayerMixin class
        if (classType.getClassName().equals(TARGET_CLASS)) {
            System.out.println("[GravityFix] Will transform: " + classType.getClassName());
            return EnumSet.of(Phase.BEFORE);
        }
        return EnumSet.noneOf(Phase.class);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        if (!classType.getClassName().equals(TARGET_CLASS)) {
            return false;
        }

        System.out.println("[GravityFix] Transforming SolomonLib PlayerMixin to remove conflicting @Redirect");

        boolean modified = false;
        Iterator<MethodNode> iter = classNode.methods.iterator();

        while (iter.hasNext()) {
            MethodNode method = iter.next();

            // Remove the redirect_dropItem method that conflicts with Gravity API
            if (method.name.contains("redirect_dropItem") ||
                (method.name.contains("dropItem") && method.name.contains("redirect"))) {
                System.out.println("[GravityFix] Removing conflicting method: " + method.name);
                iter.remove();
                modified = true;
            }
        }

        if (modified) {
            System.out.println("[GravityFix] Successfully removed conflicting method(s) from SolomonLib PlayerMixin");
        } else {
            System.out.println("[GravityFix] Warning: No conflicting methods found");
        }

        return modified;
    }
}
