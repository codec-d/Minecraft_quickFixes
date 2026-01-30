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
        LOGGER.info("[GravityFix] Using ultra high-priority mixin (10000) to preempt both gravityapi and solomonlib");
        LOGGER.info("[GravityFix] Delegates to solomonlib's gravity handling when available");
        LOGGER.info("[GravityFix] Both mod's redirects will be skipped - no conflict, no crash");
    }
}
