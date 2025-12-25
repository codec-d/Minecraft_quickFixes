package com.gravityfix.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * Fixes the @Redirect mixin conflict between Beyond the Abyss and JCraft.
 *
 * Uses @Overwrite to completely replace the drop method, preventing
 * both conflicting @Redirect mixins from applying.
 */
@Mixin(value = Player.class, priority = 900)
public abstract class PlayerDropRedirectFix {

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Vec3 getEyePosition();

    @Shadow
    public abstract float getXRot();

    @Shadow
    public abstract float getYRot();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getEyeY();

    @Shadow
    public abstract double getZ();

    /**
     * @author GravityFix
     * @reason Resolve @Redirect mixin conflict between Beyond the Abyss and JCraft
     */
    @Overwrite
    @Nullable
    public ItemEntity drop(ItemStack itemStack, boolean throwRandomly, boolean retainOwnership) {
        if (itemStack.isEmpty()) {
            return null;
        }

        Player self = (Player) (Object) this;
        Level level = this.level();

        if (level.isClientSide) {
            // Client-side swing arm animation would be here
        }

        // Get gravity direction using reflection
        Direction gravityDirection = getGravityDirection(self);

        double x, y, z;

        if (gravityDirection == null || gravityDirection == Direction.DOWN) {
            // Normal gravity
            x = this.getX();
            y = this.getEyeY() - 0.3D;
            z = this.getZ();
        } else {
            // Non-standard gravity - adjust position
            Vec3 eyePos = this.getEyePosition();
            Vec3 offset = rotateVectorByGravity(0.0D, 0.3D, 0.0D, gravityDirection);
            Vec3 adjustedPos = eyePos.subtract(offset);
            x = adjustedPos.x;
            y = adjustedPos.y;
            z = adjustedPos.z;
        }

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        itemEntity.setPickUpDelay(40);

        if (retainOwnership) {
            itemEntity.setThrower(self);
        }

        float f;
        float f1;
        Random random = new Random();

        if (throwRandomly) {
            f = random.nextFloat() * 0.5F;
            f1 = random.nextFloat() * ((float) Math.PI * 2F);
            itemEntity.setDeltaMovement(-Math.sin(f1) * f, 0.2D, Math.cos(f1) * f);
        } else {
            float xRot = this.getXRot();
            float yRot = this.getYRot();
            f = 0.3F;
            itemEntity.setDeltaMovement(
                -Math.sin(yRot * ((float) Math.PI / 180F)) * Math.cos(xRot * ((float) Math.PI / 180F)) * f,
                -Math.sin(xRot * ((float) Math.PI / 180F)) * f + 0.1F,
                Math.cos(yRot * ((float) Math.PI / 180F)) * Math.cos(xRot * ((float) Math.PI / 180F)) * f
            );
        }

        level.addFreshEntity(itemEntity);
        return itemEntity;
    }

    private static Direction getGravityDirection(Player player) {
        try {
            Class<?> gravityApiClass = Class.forName("com.min01.solomonlib.gravity.api.GravityAPI");
            Method getDirection = gravityApiClass.getMethod("getGravityDirection",
                Class.forName("net.minecraft.world.entity.Entity"));
            return (Direction) getDirection.invoke(null, player);
        } catch (Exception e1) {
            try {
                Class<?> gravityApiClass = Class.forName("com.fusionflux.gravity_api.api.GravityChangerAPI");
                Method getDirection = gravityApiClass.getMethod("getGravityDirection",
                    Class.forName("net.minecraft.world.entity.Entity"));
                return (Direction) getDirection.invoke(null, player);
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
