package com.noctivag.customenderdragon.utils;

import com.noctivag.customenderdragon.dragon.DragonPhase;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * Manages particle effects for custom dragons (Fabric)
 */
public class ParticleManager {

    public void spawnParticles(ServerWorld world, EnderDragonEntity dragon, DragonVariant variant) {
        ParticleEffect particle = getVariantParticle(variant);
        Vec3d pos = new Vec3d(dragon.getX(), dragon.getY() + 2, dragon.getZ());

        sendParticles(world, particle, pos, 0.5, 0.5, 0.5, 0.0, 5);

        // Wing trail effects
        spawnWingTrail(world, dragon, particle);
    }

    private void spawnWingTrail(ServerWorld world, EnderDragonEntity dragon, ParticleEffect particle) {
        Vec3d pos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
        Vec3d velocity = dragon.getVelocity();

        if (velocity.lengthSquared() < 0.01) {
            return;
        }

        Vec3d right = new Vec3d(velocity.z, 0, -velocity.x).normalize().multiply(4);
        Vec3d leftWing = pos.add(right);
        Vec3d rightWing = pos.subtract(right);

        sendParticles(world, particle, leftWing, 0.1, 0.1, 0.1, 0.0, 2);
        sendParticles(world, particle, rightWing, 0.1, 0.1, 0.1, 0.0, 2);
    }

    public void spawnPhaseChangeEffect(ServerWorld world, EnderDragonEntity dragon, DragonVariant variant, DragonPhase phase) {
        ParticleEffect particle = getVariantParticle(variant);
        Vec3d pos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());

        // Create expanding ring effect
        for (int ring = 0; ring < 3; ring++) {
            double radius = 3.0 + (ring * 2);
            int points = 30 + (ring * 10);

            for (int j = 0; j < points; j++) {
                double angle = 2 * Math.PI * j / points;
                double x = pos.x + radius * Math.cos(angle);
                double z = pos.z + radius * Math.sin(angle);
                double y = pos.y + 1;

                sendParticles(world, particle, new Vec3d(x, y, z), 0, 0, 0, 0.0, 1);
            }
        }
    }

    private void sendParticles(ServerWorld world, ParticleEffect particle, Vec3d pos,
                               double spreadX, double spreadY, double spreadZ,
                               double speed, int count) {
        final double maxDistance = 256 * 256; // squared distance check
        for (ServerPlayerEntity player : world.getPlayers(serverPlayer -> serverPlayer.getPos().squaredDistanceTo(pos) <= maxDistance)) {
            ParticleS2CPacket packet = new ParticleS2CPacket(
                particle,
                true,
                pos.x,
                pos.y,
                pos.z,
                (float) spreadX,
                (float) spreadY,
                (float) spreadZ,
                (float) speed,
                count
            );
            player.networkHandler.sendPacket(packet);
        }
    }

    private ParticleEffect getVariantParticle(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> ParticleTypes.FLAME;
            case ICE -> ParticleTypes.SNOWFLAKE;
            case LIGHTNING -> ParticleTypes.ELECTRIC_SPARK;
            case SHADOW -> ParticleTypes.LARGE_SMOKE;
            case VOID -> ParticleTypes.PORTAL;
        };
    }
}
