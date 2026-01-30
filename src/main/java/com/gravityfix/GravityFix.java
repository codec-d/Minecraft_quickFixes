package com.gravityfix;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("gravityfix")
public class GravityFix {
    public static final String MOD_ID = "gravityfix";
    public static final Logger LOGGER = LogManager.getLogger();

    public GravityFix() {
        LOGGER.info("[GravityFix] Compatibility mod loaded");
        LOGGER.info("[GravityFix] Used aggressive reflection to modify gravityapi's mixin config");
        LOGGER.info("[GravityFix] Check earlier logs for '[GravityFix] *** SUCCESSFULLY MODIFIED defaultRequire! ***'");
        LOGGER.info("[GravityFix] SolomonLib provides gravity functionality, gravityapi conflict prevented");
    }
}
