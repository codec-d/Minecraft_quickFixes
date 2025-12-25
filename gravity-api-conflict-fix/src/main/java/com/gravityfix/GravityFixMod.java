package com.gravityfix;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Gravity API Conflict Fix
 *
 * This mod resolves the @Redirect mixin conflict between Beyond the Abyss
 * and JCraft mods. Both mods include Gravity API (SolomonLib) which has
 * a PlayerMixin with @Redirect on ItemEntity constructor in Player.drop().
 *
 * The fix works by using a coremod to transform the second PlayerMixin
 * class at load time, converting its @Redirect to a no-op that just
 * returns the original value.
 */
@Mod("gravityfix")
public class GravityFixMod {
    public static final String MOD_ID = "gravityfix";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public GravityFixMod() {
        LOGGER.info("Gravity API Conflict Fix loaded - resolving PlayerMixin @Redirect conflict");
    }
}
