package com.gravityfix;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

/**
 * Helper class for creating ItemEntity instances with gravity support.
 * Called by the coremod transformer instead of directly calling new ItemEntity().
 */
public class ItemEntityHelper {

    /**
     * Creates an ItemEntity, adjusting position based on gravity if applicable.
     * This method is called by the coremod-transformed Player.drop() method.
     */
    public static ItemEntity create(Level level, double x, double y, double z, ItemStack stack, Entity thrower) {
        // Try to get gravity direction
        Direction gravityDirection = getGravityDirection(thrower);

        if (gravityDirection != null && gravityDirection != Direction.DOWN) {
            // Non-standard gravity - adjust position
            Vec3 eyePos = thrower.getEyePosition();
            Vec3 offset = rotateVectorByGravity(0.0D, 0.3D, 0.0D, gravityDirection);
            Vec3 adjustedPos = eyePos.subtract(offset);
            return new ItemEntity(level, adjustedPos.x, adjustedPos.y, adjustedPos.z, stack);
        }

        // Normal gravity - use original position
        return new ItemEntity(level, x, y, z, stack);
    }

    /**
     * Simple version without thrower reference - uses original coordinates.
     */
    public static ItemEntity createSimple(Level level, double x, double y, double z, ItemStack stack) {
        return new ItemEntity(level, x, y, z, stack);
    }

    private static Direction getGravityDirection(Entity entity) {
        if (entity == null) return Direction.DOWN;

        try {
            Class<?> gravityApiClass = Class.forName("com.min01.solomonlib.gravity.api.GravityAPI");
            Method getDirection = gravityApiClass.getMethod("getGravityDirection", Entity.class);
            return (Direction) getDirection.invoke(null, entity);
        } catch (Exception e1) {
            try {
                Class<?> gravityApiClass = Class.forName("com.fusionflux.gravity_api.api.GravityChangerAPI");
                Method getDirection = gravityApiClass.getMethod("getGravityDirection", Entity.class);
                return (Direction) getDirection.invoke(null, entity);
            } catch (Exception e2) {
                return Direction.DOWN;
            }
        }
    }

    private static Vec3 rotateVectorByGravity(double x, double y, double z, Direction gravityDirection) {
        try {
            Class<?> rotationUtilClass = Class.forName("com.min01.solomonlib.gravity.util.RotationUtil");
            Method vecPlayerToWorld = rotationUtilClass.getMethod("vecPlayerToWorld",
                double.class, double.class, double.class, Direction.class);
            return (Vec3) vecPlayerToWorld.invoke(null, x, y, z, gravityDirection);
        } catch (Exception e1) {
            try {
                Class<?> rotationUtilClass = Class.forName("com.fusionflux.gravity_api.util.RotationUtil");
                Method vecPlayerToWorld = rotationUtilClass.getMethod("vecPlayerToWorld",
                    double.class, double.class, double.class, Direction.class);
                return (Vec3) vecPlayerToWorld.invoke(null, x, y, z, gravityDirection);
            } catch (Exception e2) {
                return new Vec3(x, y, z);
            }
        }
    }
}
