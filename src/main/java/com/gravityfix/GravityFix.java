package com.gravityfix;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("gravityfix")
public class GravityFix {
    public static final String MOD_ID = "gravityfix";
    public static final Logger LOGGER = LogManager.getLogger();

    public GravityFix() {
        LOGGER.info("[GravityFix] Initializing compatibility mod");
        LOGGER.info("[GravityFix] This mod disables conflicting mixins from deprecated GravityAPI");
        LOGGER.info("[GravityFix] SolomonLib's gravity implementation will be used instead");
    }
}
