/**
 * Gravity API Conflict Fix - Coremod Transformer
 *
 * Replaces NEW ItemEntity(...) in Player.drop() with a call to
 * ItemEntityHelper.createSimple(). This eliminates the bytecode pattern
 * that the conflicting @Redirect mixins target.
 */

var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');

function initializeCoreMod() {
    return {
        'gravity_redirect_fix': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.entity.player.Player'
            },
            'transformer': function(classNode) {
                print('[GravityFix] Transforming Player class to resolve @Redirect conflict');

                var dropMethodName = ASMAPI.mapMethod('m_7075_');
                var methods = classNode.methods;

                for (var i = 0; i < methods.size(); i++) {
                    var method = methods.get(i);

                    var isDropMethod = method.name.equals(dropMethodName) ||
                                       method.name.equals('drop') ||
                                       method.name.equals('m_7075_');

                    if (isDropMethod && method.desc.equals('(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;')) {
                        print('[GravityFix] Found drop method: ' + method.name);
                        transformDropMethod(method);
                        break;
                    }
                }

                return classNode;
            }
        }
    };
}

function transformDropMethod(method) {
    var instructions = method.instructions;
    var toProcess = [];

    // First pass: find all NEW ItemEntity instructions
    var iter = instructions.iterator();
    while (iter.hasNext()) {
        var insn = iter.next();
        if (insn.getOpcode() == Opcodes.NEW && insn.desc.equals('net/minecraft/world/entity/item/ItemEntity')) {
            toProcess.push(insn);
        }
    }

    print('[GravityFix] Found ' + toProcess.length + ' ItemEntity creations to transform');

    // Second pass: transform each NEW+DUP+INVOKESPECIAL sequence
    for (var i = 0; i < toProcess.length; i++) {
        var newInsn = toProcess[i];
        transformItemEntityCreation(instructions, newInsn);
    }

    print('[GravityFix] Transformation complete');
}

function transformItemEntityCreation(instructions, newInsn) {
    // Pattern: NEW ItemEntity, DUP, [args], INVOKESPECIAL <init>
    // Replace with: [args], INVOKESTATIC ItemEntityHelper.createSimple

    var dupInsn = newInsn.getNext();
    if (dupInsn == null || dupInsn.getOpcode() != Opcodes.DUP) {
        print('[GravityFix] Warning: DUP not found after NEW');
        return;
    }

    // Find the INVOKESPECIAL <init>
    var current = dupInsn.getNext();
    var initInsn = null;
    var argCount = 0;

    while (current != null) {
        if (current.getOpcode() == Opcodes.INVOKESPECIAL) {
            if (current.owner.equals('net/minecraft/world/entity/item/ItemEntity') &&
                current.name.equals('<init>')) {
                initInsn = current;
                break;
            }
        }
        current = current.getNext();
        argCount++;
        if (argCount > 20) break; // Safety limit
    }

    if (initInsn == null) {
        print('[GravityFix] Warning: INVOKESPECIAL <init> not found');
        return;
    }

    print('[GravityFix] Transforming ItemEntity creation');

    // Get the constructor descriptor to determine which helper method to call
    var initDesc = initInsn.desc;
    print('[GravityFix] Constructor descriptor: ' + initDesc);

    // Create the static method call
    // Original: (Level, double, double, double, ItemStack)V
    // Static:   (Level, double, double, double, ItemStack)ItemEntity
    var staticCall = new MethodInsnNode(
        Opcodes.INVOKESTATIC,
        'com/gravityfix/ItemEntityHelper',
        'createSimple',
        '(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;',
        false
    );

    // Remove NEW and DUP, replace INVOKESPECIAL with INVOKESTATIC
    instructions.remove(newInsn);
    instructions.remove(dupInsn);
    instructions.set(initInsn, staticCall);

    print('[GravityFix] Replaced constructor call with static factory method');
}
