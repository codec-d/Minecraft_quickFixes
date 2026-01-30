package com.gravityfix.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;

@Mixin(value = Player.class, priority = 10000)
public class PlayerDropMixin {

    /**
     * ULTRA HIGH-PRIORITY redirect (10000) that preempts both gravityapi (1001) and solomonlib (1001).
     * This prevents the conflict by applying first.
     *
     * We delegate to solomonlib's gravity handling if available, otherwise use vanilla behavior.
     */
    @Redirect(
        method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
        ),
        require = 0  // Don't crash if this redirect fails
    )
    private ItemEntity gravityfix_redirect_dropItem_new_0(
        Level level,
        double x,
        double y,
        double z,
        ItemStack itemStack
    ) {
        Player player = (Player) (Object) this;

        try {
            // Try to call solomonlib's gravity-aware ItemEntity creation
            Class<?> gravityAPIClass = Class.forName("com.min01.gravity.api.GravityAPI");
            Method getGravityMethod = gravityAPIClass.getMethod("getGravityDirection", net.minecraft.world.entity.Entity.class);
            Object gravityDirection = getGravityMethod.invoke(null, player);

            // If we got here, solomonlib is loaded and working
            // Create ItemEntity and let solomonlib's systems handle gravity
            ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);

            // Apply gravity direction if available
            try {
                Method applyGravityMethod = gravityAPIClass.getMethod("applyGravityToEntity", net.minecraft.world.entity.Entity.class, Object.class);
                applyGravityMethod.invoke(null, itemEntity, gravityDirection);
            } catch (Exception e) {
                // Gravity application failed, entity will use default behavior
            }

            return itemEntity;

        } catch (Exception e) {
            // SolomonLib not available or failed - use vanilla behavior
            return new ItemEntity(level, x, y, z, itemStack);
        }
    }
}
