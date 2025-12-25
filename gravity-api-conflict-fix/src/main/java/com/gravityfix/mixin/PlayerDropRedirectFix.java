package com.gravityfix.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;

/**
 * High-priority mixin that resolves the @Redirect conflict between
 * Beyond the Abyss and JCraft mods.
 *
 * Both mods have Gravity API's PlayerMixin which @Redirects the
 * ItemEntity constructor in Player.drop(). This mixin has a higher
 * priority (2000 > 1001 > 1000) and supersedes both conflicting redirects.
 *
 * The implementation replicates the gravity-aware logic from Gravity API.
 */
@Mixin(value = Player.class, priority = 2000)
public abstract class PlayerDropRedirectFix {

    /**
     * Redirect the ItemEntity constructor call in Player.drop() method.
     * This has priority 2000 which supersedes the conflicting redirects
     * at priority 1000 and 1001.
     *
     * We replicate the gravity-aware ItemEntity creation logic here.
     */
    @Redirect(
        method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;")
    )
    private ItemEntity gravityfix$redirectDropItemNew(
            Level level, double x, double y, double z, ItemStack stack) {

        Player self = (Player) (Object) this;

        // Try to get gravity direction using reflection to support both
        // Gravity API implementations (SolomonLib and original)
        Direction gravityDirection = getGravityDirection(self);

        if (gravityDirection == null || gravityDirection == Direction.DOWN) {
            // Normal gravity - use original position
            return new ItemEntity(level, x, y, z, stack);
        }

        // Non-standard gravity - adjust item spawn position
        Vec3 eyePos = self.getEyePosition();
        Vec3 offset = rotateVectorByGravity(0.0D, 0.3D, 0.0D, gravityDirection);
        Vec3 adjustedPos = eyePos.subtract(offset);

        return new ItemEntity(level, adjustedPos.x, adjustedPos.y, adjustedPos.z, stack);
    }

    /**
     * Get the gravity direction for an entity using reflection.
     * This supports both SolomonLib's and original Gravity API's methods.
     */
    private static Direction getGravityDirection(Player player) {
        try {
            // Try SolomonLib's GravityAPI class
            Class<?> gravityApiClass = Class.forName("com.min01.solomonlib.gravity.api.GravityAPI");
            Method getDirection = gravityApiClass.getMethod("getGravityDirection",
                Class.forName("net.minecraft.world.entity.Entity"));
            return (Direction) getDirection.invoke(null, player);
        } catch (Exception e1) {
            try {
                // Try original Gravity API class (FusionFlux)
                Class<?> gravityApiClass = Class.forName("com.fusionflux.gravity_api.api.GravityChangerAPI");
                Method getDirection = gravityApiClass.getMethod("getGravityDirection",
                    Class.forName("net.minecraft.world.entity.Entity"));
                return (Direction) getDirection.invoke(null, player);
            } catch (Exception e2) {
                // Neither API found - fall back to normal gravity
                return Direction.DOWN;
            }
        }
    }

    /**
     * Rotate a vector from player space to world space based on gravity direction.
     * This replicates RotationUtil.vecPlayerToWorld from Gravity API.
     */
    private static Vec3 rotateVectorByGravity(double x, double y, double z, Direction gravityDirection) {
        try {
            // Try SolomonLib's RotationUtil
            Class<?> rotationUtilClass = Class.forName("com.min01.solomonlib.gravity.util.RotationUtil");
            Method vecPlayerToWorld = rotationUtilClass.getMethod("vecPlayerToWorld",
                double.class, double.class, double.class, Direction.class);
            return (Vec3) vecPlayerToWorld.invoke(null, x, y, z, gravityDirection);
        } catch (Exception e1) {
            try {
                // Try original Gravity API's RotationUtil
                Class<?> rotationUtilClass = Class.forName("com.fusionflux.gravity_api.util.RotationUtil");
                Method vecPlayerToWorld = rotationUtilClass.getMethod("vecPlayerToWorld",
                    double.class, double.class, double.class, Direction.class);
                return (Vec3) vecPlayerToWorld.invoke(null, x, y, z, gravityDirection);
            } catch (Exception e2) {
                // Fallback - return original offset
                return new Vec3(x, y, z);
            }
        }
    }
}
