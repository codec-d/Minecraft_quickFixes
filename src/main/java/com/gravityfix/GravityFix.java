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
        LOGGER.info("[GravityFix] Attempting to override gravityapi's mixin configuration");
        LOGGER.info("[GravityFix] Providing empty gravityapi.mixin.json to prevent conflicts");
        LOGGER.info("[GravityFix] SolomonLib will provide gravity functionality");
    }
}
