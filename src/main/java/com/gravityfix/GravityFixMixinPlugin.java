package com.gravityfix;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class GravityFixMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        System.out.println("[GravityFix] AGGRESSIVE FIX: Mixin plugin loading early");
        System.out.println("[GravityFix] Attempting to hijack gravityapi mixin configuration");

        // This runs during mixin loading - perfect time to modify configs
        tryModifyGravityApiConfig();
    }

    private void tryModifyGravityApiConfig() {
        try {
            System.out.println("[GravityFix] Accessing Mixin internals via reflection...");

            // Try to access the global Mixins class which has the configs
            Class<?> mixinsClass = Class.forName("org.spongepowered.asm.mixin.Mixins");
            System.out.println("[GravityFix] Found Mixins class: " + mixinsClass);

            // Try to find the configs field - it's likely a static field
            Field[] fields = mixinsClass.getDeclaredFields();
            System.out.println("[GravityFix] Scanning " + fields.length + " fields in Mixins class...");

            for (Field field : fields) {
                field.setAccessible(true);
                System.out.println("[GravityFix] Field: " + field.getName() + " type: " + field.getType());

                // Look for a Set or Collection field that might hold configs
                if (java.util.Set.class.isAssignableFrom(field.getType()) ||
                    java.util.Collection.class.isAssignableFrom(field.getType())) {

                    try {
                        Object value = field.get(null); // static field
                        if (value instanceof Set) {
                            Set<?> set = (Set<?>) value;
                            System.out.println("[GravityFix] Found Set field '" + field.getName() + "' with " + set.size() + " items");

                            // Check if it contains MixinConfig objects
                            for (Object item : set) {
                                if (item != null && item.getClass().getName().contains("MixinConfig")) {
                                    System.out.println("[GravityFix] Found MixinConfig in field '" + field.getName() + "'");
                                    scanAndModifyConfig(item);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Try next field
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("[GravityFix] ERROR during aggressive fix:");
            e.printStackTrace();
        }
    }

    private void scanAndModifyConfig(Object config) {
        try {
            Method getNameMethod = config.getClass().getMethod("getName");
            String configName = (String) getNameMethod.invoke(config);

            System.out.println("[GravityFix] Checking config: " + configName);

            if (configName != null && configName.contains("gravityapi")) {
                System.out.println("[GravityFix] *** FOUND TARGET: " + configName + " ***");
                modifyInjectorConfig(config);
            }

        } catch (Exception e) {
            // Ignore
        }
    }

    private void modifyInjectorConfig(Object config) {
        try {
            Class<?> configClass = config.getClass();

            // Try to access the injector options
            try {
                Method getInjectorOptions = configClass.getMethod("getInjectorOptions");
                Object injectorOptions = getInjectorOptions.invoke(config);
                System.out.println("[GravityFix] Got injector options: " + injectorOptions);

                if (injectorOptions != null) {
                    Class<?> optionsClass = injectorOptions.getClass();
                    Field defaultRequireField = optionsClass.getDeclaredField("defaultRequire");
                    defaultRequireField.setAccessible(true);
                    int oldValue = defaultRequireField.getInt(injectorOptions);
                    defaultRequireField.setInt(injectorOptions, 0);
                    System.out.println("[GravityFix] *** MODIFIED InjectorOptions.defaultRequire from " + oldValue + " to 0 ***");
                }
            } catch (Exception e) {
                System.err.println("[GravityFix] Failed to get injector options:");
                e.printStackTrace();
            }

            // Also try direct field access
            try {
                Field injectorOptionsField = configClass.getDeclaredField("injectorOptions");
                injectorOptionsField.setAccessible(true);
                Object injectorOptions = injectorOptionsField.get(config);

                if (injectorOptions != null) {
                    Class<?> optionsClass = injectorOptions.getClass();
                    Field defaultRequireField = optionsClass.getDeclaredField("defaultRequire");
                    defaultRequireField.setAccessible(true);
                    int oldValue = defaultRequireField.getInt(injectorOptions);
                    defaultRequireField.setInt(injectorOptions, 0);
                    System.out.println("[GravityFix] *** SUCCESSFULLY MODIFIED defaultRequire from " + oldValue + " to 0! ***");
                }
            } catch (Exception e) {
                System.err.println("[GravityFix] Failed direct field access:");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("[GravityFix] Failed to modify injector config:");
            e.printStackTrace();
        }
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
