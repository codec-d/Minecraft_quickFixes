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
        LOGGER.info("[GravityFix] Attempting to resolve gravityapi/solomonlib mixin conflict");

        // Create Radium config with specific mixin override
        createRadiumConfigSpecific();
    }

    private void createRadiumConfigSpecific() {
        try {
            Path configDir = FMLPaths.CONFIGDIR.get();
            Path radiumConfigPath = configDir.resolve("radium.properties");

            LOGGER.info("[GravityFix] Configuring Radium at: {}", radiumConfigPath);

            List<String> lines = new ArrayList<>();
            boolean foundGravityApiOverride = false;

            // Read existing config if it exists
            if (Files.exists(radiumConfigPath)) {
                lines = Files.readAllLines(radiumConfigPath);

                // Check if we already have a gravityapi-specific override
                for (String line : lines) {
                    if (line.trim().startsWith("mixin.gravity.PlayerMixin=") ||
                        line.trim().startsWith("gravityapi.mixin.json:PlayerMixin=")) {
                        foundGravityApiOverride = true;
                        LOGGER.info("[GravityFix] Found existing gravityapi mixin override");
                        break;
                    }
                }
            } else {
                LOGGER.info("[GravityFix] Creating new Radium config");
                lines.add("# Radium Configuration File");
                lines.add("# Modified by GravityFix to resolve mixin conflicts");
                lines.add("");
            }

            // Add specific override to disable ONLY gravityapi's PlayerMixin
            if (!foundGravityApiOverride) {
                lines.add("");
                lines.add("# GravityFix: Disable ONLY gravityapi's PlayerMixin (allow solomonlib's version)");
                lines.add("# Format attempts both possible syntaxes:");
                lines.add("mixin.gravity.PlayerMixin=false");
                lines.add("gravityapi.mixin.json:PlayerMixin=false");

                Files.write(radiumConfigPath, lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

                LOGGER.warn("[GravityFix] ===============================================");
                LOGGER.warn("[GravityFix] Radium config updated with specific overrides");
                LOGGER.warn("[GravityFix] Trying to disable only gravityapi, not solomonlib");
                LOGGER.warn("[GravityFix] RESTART Minecraft for changes to take effect");
                LOGGER.warn("[GravityFix] ===============================================");
            } else {
                LOGGER.info("[GravityFix] Gravityapi mixin override already configured");
            }

        } catch (IOException e) {
            LOGGER.error("[GravityFix] Failed to configure Radium", e);
        }
    }
}
