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

            // Access MixinEnvironment to get all configs
            Class<?> mixinEnvClass = Class.forName("org.spongepowered.asm.mixin.MixinEnvironment");
            Method getCurrentEnv = mixinEnvClass.getMethod("getCurrentEnvironment");
            Object environment = getCurrentEnv.invoke(null);

            System.out.println("[GravityFix] Got MixinEnvironment: " + environment);

            // Access the configs
            Method getConfigsMethod = mixinEnvClass.getMethod("getConfigs");
            Object configsObj = getConfigsMethod.invoke(environment);

            if (configsObj instanceof Set) {
                Set<?> configs = (Set<?>) configsObj;
                System.out.println("[GravityFix] Scanning " + configs.size() + " mixin configs...");

                for (Object config : configs) {
                    try {
                        Method getNameMethod = config.getClass().getMethod("getName");
                        String configName = (String) getNameMethod.invoke(config);

                        if (configName != null && configName.contains("gravityapi")) {
                            System.out.println("[GravityFix] *** FOUND TARGET: " + configName + " ***");

                            // Modify the injector configs
                            modifyInjectorConfig(config);
                        }
                    } catch (Exception e) {
                        // Continue to next config
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("[GravityFix] ERROR during aggressive fix:");
            e.printStackTrace();
        }
    }

    private void modifyInjectorConfig(Object config) {
        try {
            // Try to access and modify the injector config
            Class<?> configClass = config.getClass();

            // Try multiple possible field names for the injector options
            String[] possibleFields = {"injectorOptions", "injectors", "defaultRequire"};

            for (String fieldName : possibleFields) {
                try {
                    Field field = configClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(config);
                    System.out.println("[GravityFix] Found field '" + fieldName + "': " + value);

                    if (fieldName.equals("defaultRequire") && value instanceof Integer) {
                        System.out.println("[GravityFix] Changing defaultRequire from " + value + " to 0");
                        field.setInt(config, 0);
                        System.out.println("[GravityFix] *** SUCCESSFULLY MODIFIED defaultRequire! ***");
                    }

                } catch (NoSuchFieldException e) {
                    // Try next field
                }
            }

            // Also try to access via the Config object's methods
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
                // Not available
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
