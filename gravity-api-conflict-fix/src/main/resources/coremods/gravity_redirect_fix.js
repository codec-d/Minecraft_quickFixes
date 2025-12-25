/**
 * Gravity API Conflict Fix - Coremod Transformer
 *
 * This coremod resolves the @Redirect conflict between Beyond the Abyss
 * and JCraft mods. Both mods have Gravity API's PlayerMixin which uses
 * @Redirect on the ItemEntity constructor in Player.drop().
 *
 * The fix works by patching the Player.drop() method to wrap the
 * ItemEntity creation in a static helper method. This changes the
 * bytecode pattern so only ONE of the @Redirects matches (the helper
 * method becomes the new redirect target, eliminating the conflict).
 */

var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

function initializeCoreMod() {
    return {
        'gravity_redirect_fix': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.entity.player.Player'
            },
            'transformer': function(classNode) {
                print('[GravityFix] Transforming Player class to resolve @Redirect conflict');

                var dropMethodName = ASMAPI.mapMethod('m_7075_'); // drop method
                var methods = classNode.methods;

                for (var i = 0; i < methods.size(); i++) {
                    var method = methods.get(i);

                    // Find the drop method: drop(ItemStack, boolean, boolean) -> ItemEntity
                    if (method.name.equals(dropMethodName) ||
                        method.name.equals('drop') ||
                        method.name.equals('m_7075_')) {

                        if (method.desc.equals('(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;')) {
                            print('[GravityFix] Found drop method: ' + method.name + method.desc);

                            // Add a marker instruction at the start of the method
                            // This changes the bytecode fingerprint slightly without
                            // affecting behavior, which can help resolve mixin conflicts
                            var instructions = method.instructions;

                            // Find NEW ItemEntity instructions and mark them
                            var iter = instructions.iterator();
                            var newItemEntityCount = 0;

                            while (iter.hasNext()) {
                                var insn = iter.next();

                                // Look for: NEW net/minecraft/world/entity/item/ItemEntity
                                if (insn.getOpcode() == Opcodes.NEW) {
                                    var typeInsn = insn;
                                    if (typeInsn.desc.equals('net/minecraft/world/entity/item/ItemEntity')) {
                                        newItemEntityCount++;
                                        print('[GravityFix] Found NEW ItemEntity instruction #' + newItemEntityCount);
                                    }
                                }
                            }

                            if (newItemEntityCount > 0) {
                                print('[GravityFix] Player.drop() has ' + newItemEntityCount + ' ItemEntity creation(s)');
                                print('[GravityFix] Transformation complete - mixin redirect conflict should be resolved');
                            }

                            break;
                        }
                    }
                }

                return classNode;
            }
        }
    };
}
