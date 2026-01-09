package com.gravityfix.mixin;

import com.gravityfix.GravityFix;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin has a very high priority (2000) to load before both
 * gravityapi (priority 1001) and solomonlib (priority 1001).
 *
 * It redirects the ItemEntity creation to allow gravity modifications
 * from SolomonLib while preventing the conflict with old gravityapi.
 */
@Mixin(value = Player.class, priority = 2000)
public class PlayerMixinFix {

    /**
     * Redirect ItemEntity creation when dropping items.
     * This has the highest priority, so it will be applied first,
     * preventing both gravityapi and solomonlib from conflicting.
     *
     * The actual gravity modification will happen through SolomonLib's
     * systems that hook into entity creation.
     */
    @Redirect(
        method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
        )
    )
    private ItemEntity redirect_dropItem_new_0(Level level, double x, double y, double z, ItemStack stack) {
        // Create the ItemEntity normally - SolomonLib's gravity system will handle
        // gravity modifications through its own entity initialization hooks
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);

        GravityFix.LOGGER.debug("[GravityFix] ItemEntity created at ({}, {}, {}) - gravity handling delegated to SolomonLib", x, y, z);

        return itemEntity;
    }
}
