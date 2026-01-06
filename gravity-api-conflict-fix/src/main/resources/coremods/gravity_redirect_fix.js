/**
 * Gravity API Conflict Fix - Coremod Transformer
 *
 * Removes the conflicting @Redirect method from SolomonLib's PlayerMixin
 * BEFORE mixin processes it. This allows Gravity API's version to work
 * without conflict while keeping all other SolomonLib functionality.
 */

var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
        'solomonlib_playermixin_fix': {
            'target': {
                'type': 'CLASS',
                'name': 'com.min01.solomonlib.mixin.gravity.PlayerMixin'
            },
            'transformer': function(classNode) {
                print('[GravityFix] Transforming SolomonLib PlayerMixin to remove conflicting @Redirect');

                var methods = classNode.methods;
                var toRemove = [];

                // Find the redirect_dropItem_new_0 method(s)
                for (var i = 0; i < methods.size(); i++) {
                    var method = methods.get(i);

                    // Match the conflicting redirect method by name pattern
                    if (method.name.indexOf('redirect_dropItem') >= 0 ||
                        method.name.indexOf('dropItem') >= 0 && method.name.indexOf('redirect') >= 0) {
                        print('[GravityFix] Found conflicting method: ' + method.name + method.desc);
                        toRemove.push(method);
                    }
                }

                // Remove the conflicting methods
                for (var i = 0; i < toRemove.length; i++) {
                    methods.remove(toRemove[i]);
                    print('[GravityFix] Removed method: ' + toRemove[i].name);
                }

                if (toRemove.length > 0) {
                    print('[GravityFix] Successfully removed ' + toRemove.length + ' conflicting method(s) from SolomonLib PlayerMixin');
                } else {
                    print('[GravityFix] Warning: No conflicting methods found in SolomonLib PlayerMixin');
                }

                return classNode;
            }
        }
    };
}
