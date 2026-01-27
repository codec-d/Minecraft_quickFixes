package com.gravityfix;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Mod("gravityfix")
public class GravityFix {
    public static final String MOD_ID = "gravityfix";
    public static final Logger LOGGER = LogManager.getLogger();

    public GravityFix() {
        LOGGER.info("[GravityFix] Initializing compatibility mod");
        LOGGER.info("[GravityFix] This mod fixes conflicts between deprecated GravityAPI and SolomonLib");

        // Create/update Radium config to disable conflicting mixin
        createRadiumConfig();
    }

    private void createRadiumConfig() {
        try {
            Path configDir = FMLPaths.CONFIGDIR.get();
            Path radiumConfigPath = configDir.resolve("radium.properties");

            LOGGER.info("[GravityFix] Checking Radium configuration at: {}", radiumConfigPath);

            List<String> lines = new ArrayList<>();
            boolean foundGravityOption = false;

            // Read existing config if it exists
            if (Files.exists(radiumConfigPath)) {
                lines = Files.readAllLines(radiumConfigPath);

                // Check if the gravity mixin option already exists
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.trim().startsWith("mixin.gravity=") ||
                        line.trim().startsWith("mixin.gravity.PlayerMixin=")) {
                        foundGravityOption = true;
                        LOGGER.info("[GravityFix] Found existing gravity mixin config option");
                        break;
                    }
                }
            } else {
                LOGGER.info("[GravityFix] Radium config doesn't exist, creating new one");
                // Add header comment
                lines.add("# Radium Configuration File");
                lines.add("# This file has been modified by GravityFix to resolve mixin conflicts");
                lines.add("");
            }

            // Add the gravity mixin disable option if not present
            if (!foundGravityOption) {
                lines.add("");
                lines.add("# GravityFix: Disable gravityapi's PlayerMixin to prevent conflict with SolomonLib");
                lines.add("# GravityAPI functionality is provided by SolomonLib instead");
                lines.add("mixin.gravity=false");

                Files.write(radiumConfigPath, lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

                LOGGER.warn("[GravityFix] ==========================================");
                LOGGER.warn("[GravityFix] IMPORTANT: Radium config has been updated!");
                LOGGER.warn("[GravityFix] Please RESTART Minecraft for changes to take effect");
                LOGGER.warn("[GravityFix] The gravity mixin conflict will be resolved after restart");
                LOGGER.warn("[GravityFix] ==========================================");
            } else {
                LOGGER.info("[GravityFix] Radium config already has gravity mixin option, no changes needed");
            }

        } catch (IOException e) {
            LOGGER.error("[GravityFix] Failed to create/update Radium configuration", e);
            LOGGER.error("[GravityFix] You may need to manually add 'mixin.gravity=false' to config/radium.properties");
        }
    }
}
