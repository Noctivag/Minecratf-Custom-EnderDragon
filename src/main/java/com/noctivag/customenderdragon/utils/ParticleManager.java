package com.noctivag.customenderdragon.utils;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.DragonPhase;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Manages particle effects for custom dragons
 */
public class ParticleManager {
    private final CustomEnderDragonPlugin plugin;

    public ParticleManager(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnParticles(EnderDragon dragon, DragonVariant variant) {
        Location loc = dragon.getLocation();
        ConfigurationSection particleConfig = plugin.getConfig()
                .getConfigurationSection("variants." + variant.name() + ".particles");

        if (particleConfig == null) return;

        List<?> particles = plugin.getConfig().getList("variants." + variant.name() + ".particles");
        if (particles == null) return;

        for (Object particleObj : particles) {
            if (particleObj instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) particleObj;
                try {
                    String typeStr = section.getString("type", "FLAME");
                    Particle particle = Particle.valueOf(typeStr);
                    int count = section.getInt("count", 5);
                    double offset = section.getDouble("offset", 0.5);

                    dragon.getWorld().spawnParticle(
                        particle,
                        loc.clone().add(0, 2, 0),
                        count,
                        offset, offset, offset,
                        0
                    );
                } catch (IllegalArgumentException e) {
                    // Invalid particle type, skip
                }
            }
        }

        // Add wing trail effects
        spawnWingTrail(dragon, variant);
    }

    private void spawnWingTrail(EnderDragon dragon, DragonVariant variant) {
        Location loc = dragon.getLocation();
        Vector direction = dragon.getVelocity().normalize();

        if (direction.lengthSquared() < 0.01) return;

        // Calculate wing positions
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(4);
        Location leftWing = loc.clone().add(right);
        Location rightWing = loc.clone().subtract(right);

        Particle trailParticle = getTrailParticle(variant);

        dragon.getWorld().spawnParticle(trailParticle, leftWing, 2, 0.1, 0.1, 0.1, 0);
        dragon.getWorld().spawnParticle(trailParticle, rightWing, 2, 0.1, 0.1, 0.1, 0);
    }

    private Particle getTrailParticle(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Particle.FLAME;
            case ICE -> Particle.SNOWFLAKE;
            case LIGHTNING -> Particle.ELECTRIC_SPARK;
            case SHADOW -> Particle.SMOKE_LARGE;
            case VOID -> Particle.PORTAL;
        };
    }

    public void spawnPhaseChangeEffect(EnderDragon dragon, DragonVariant variant, DragonPhase phase) {
        Location loc = dragon.getLocation();
        Particle mainParticle = getTrailParticle(variant);

        // Create expanding ring effect
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                double radius = 3.0 + (finalI * 2);
                int points = 30 + (finalI * 10);

                for (int j = 0; j < points; j++) {
                    double angle = 2 * Math.PI * j / points;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location particleLoc = loc.clone().add(x, 1, z);
                    dragon.getWorld().spawnParticle(
                        mainParticle,
                        particleLoc,
                        5,
                        0.1, 0.1, 0.1,
                        0
                    );
                }
            }, i * 5L);
        }

        // Upward burst
        dragon.getWorld().spawnParticle(
            mainParticle,
            loc.clone().add(0, 2, 0),
            50,
            0.5, 2, 0.5,
            0.1
        );
    }

    public void spawnAbilityEffect(Location location, DragonVariant variant, String abilityName) {
        Particle particle = getTrailParticle(variant);

        switch (abilityName) {
            case "fireball", "fire-aura" -> {
                location.getWorld().spawnParticle(Particle.FLAME, location, 30, 1, 1, 1, 0.05);
                location.getWorld().spawnParticle(Particle.LAVA, location, 10, 0.5, 0.5, 0.5, 0);
            }
            case "ice-shard", "freeze-aura" -> {
                location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 30, 1, 1, 1, 0.05);
                location.getWorld().spawnParticle(Particle.CLOUD, location, 15, 0.5, 0.5, 0.5, 0);
            }
            case "lightning-strike" -> {
                location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 40, 0.5, 2, 0.5, 0.1);
                location.getWorld().spawnParticle(Particle.END_ROD, location, 20, 0.3, 1, 0.3, 0.05);
            }
            case "shadow-strike" -> {
                location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 30, 1, 1, 1, 0.05);
                location.getWorld().spawnParticle(Particle.SQUID_INK, location, 15, 0.5, 0.5, 0.5, 0);
            }
            case "void-beam" -> {
                location.getWorld().spawnParticle(Particle.PORTAL, location, 50, 1, 1, 1, 0.1);
                location.getWorld().spawnParticle(Particle.DRAGON_BREATH, location, 25, 0.5, 0.5, 0.5, 0.05);
            }
        }
    }

    public void spawnCircleEffect(Location center, double radius, Particle particle, int points) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location particleLoc = center.clone().add(x, 0, z);
            center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    public void spawnSpiralEffect(Location center, Particle particle, int height, double radius) {
        for (int i = 0; i < height * 10; i++) {
            double angle = i * 0.3;
            double y = i * 0.1;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location particleLoc = center.clone().add(x, y, z);
            center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    // ========== 3D Particle Formations ==========

    /**
     * Creates a 3D sphere of particles
     */
    public void spawn3DSphere(Location center, Particle particle, double radius, int density) {
        int points = density * 10;
        for (int i = 0; i < points; i++) {
            // Fibonacci sphere distribution for even point distribution
            double y = 1 - (i / (double) (points - 1)) * 2;
            double radiusAtY = Math.sqrt(1 - y * y) * radius;
            double theta = Math.PI * (1 + Math.sqrt(5)) * i;

            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            Location particleLoc = center.clone().add(x, y * radius, z);
            center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    /**
     * Creates a 3D helix/DNA strand effect
     */
    public void spawn3DHelix(Location base, Particle particle, double height, double radius, int strands) {
        int steps = (int) (height * 20);
        for (int i = 0; i < steps; i++) {
            double y = (i / (double) steps) * height;
            double angle = (i / (double) steps) * Math.PI * 8; // 4 full rotations

            for (int strand = 0; strand < strands; strand++) {
                double strandAngle = angle + (2 * Math.PI * strand / strands);
                double x = Math.cos(strandAngle) * radius;
                double z = Math.sin(strandAngle) * radius;

                Location particleLoc = base.clone().add(x, y, z);
                base.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Creates a 3D torus (donut shape)
     */
    public void spawn3DTorus(Location center, Particle particle, double majorRadius, double minorRadius, int density) {
        int outerSteps = density * 10;
        int innerSteps = density * 5;

        for (int i = 0; i < outerSteps; i++) {
            double u = (2 * Math.PI * i) / outerSteps;

            for (int j = 0; j < innerSteps; j++) {
                double v = (2 * Math.PI * j) / innerSteps;

                double x = (majorRadius + minorRadius * Math.cos(v)) * Math.cos(u);
                double y = minorRadius * Math.sin(v);
                double z = (majorRadius + minorRadius * Math.cos(v)) * Math.sin(u);

                Location particleLoc = center.clone().add(x, y, z);
                center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Creates a 3D vortex effect
     */
    public void spawn3DVortex(Location base, Particle particle, double height, double baseRadius, double topRadius) {
        int layers = (int) (height * 10);

        for (int layer = 0; layer < layers; layer++) {
            double progress = layer / (double) layers;
            double y = progress * height;
            double currentRadius = baseRadius + (topRadius - baseRadius) * progress;
            double rotation = progress * Math.PI * 6; // Spiral effect

            int pointsInLayer = 12 + (int) (currentRadius * 4);
            for (int i = 0; i < pointsInLayer; i++) {
                double angle = (2 * Math.PI * i / pointsInLayer) + rotation;
                double x = Math.cos(angle) * currentRadius;
                double z = Math.sin(angle) * currentRadius;

                Location particleLoc = base.clone().add(x, y, z);
                base.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Creates energy beam between two points
     */
    public void spawn3DBeam(Location start, Location end, Particle particle, int segments) {
        Vector direction = end.toVector().subtract(start.toVector());
        double distance = direction.length();
        direction.normalize();

        for (int i = 0; i <= segments; i++) {
            double progress = i / (double) segments;
            Vector offset = direction.clone().multiply(distance * progress);
            Location particleLoc = start.clone().add(offset);

            // Add slight spiral to beam
            double angle = progress * Math.PI * 4;
            double beamRadius = 0.3;
            double spiralX = Math.cos(angle) * beamRadius;
            double spiralZ = Math.sin(angle) * beamRadius;

            particleLoc.add(spiralX, 0, spiralZ);
            start.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    /**
     * Creates a 3D star/explosion burst
     */
    public void spawn3DStarBurst(Location center, Particle particle, double radius, int rays) {
        for (int ray = 0; ray < rays; ray++) {
            // Random direction for each ray
            double theta = (2 * Math.PI * ray) / rays;
            double phi = Math.acos(2 * (ray / (double) rays) - 1);

            Vector direction = new Vector(
                Math.sin(phi) * Math.cos(theta),
                Math.cos(phi),
                Math.sin(phi) * Math.sin(theta)
            );

            // Create particles along the ray
            for (int i = 0; i < 20; i++) {
                double distance = (i / 20.0) * radius;
                Location particleLoc = center.clone().add(direction.clone().multiply(distance));
                center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Creates a 3D geometric cube outline
     */
    public void spawn3DCube(Location center, Particle particle, double size, int pointsPerEdge) {
        double half = size / 2;

        // Draw 12 edges of cube
        for (int i = 0; i < pointsPerEdge; i++) {
            double t = i / (double) (pointsPerEdge - 1);

            // Bottom square (y = -half)
            spawnParticleOnEdge(center, particle, -half + t * size, -half, -half);
            spawnParticleOnEdge(center, particle, half, -half, -half + t * size);
            spawnParticleOnEdge(center, particle, half - t * size, -half, half);
            spawnParticleOnEdge(center, particle, -half, -half, half - t * size);

            // Top square (y = half)
            spawnParticleOnEdge(center, particle, -half + t * size, half, -half);
            spawnParticleOnEdge(center, particle, half, half, -half + t * size);
            spawnParticleOnEdge(center, particle, half - t * size, half, half);
            spawnParticleOnEdge(center, particle, -half, half, half - t * size);

            // Vertical edges
            spawnParticleOnEdge(center, particle, -half, -half + t * size, -half);
            spawnParticleOnEdge(center, particle, half, -half + t * size, -half);
            spawnParticleOnEdge(center, particle, half, -half + t * size, half);
            spawnParticleOnEdge(center, particle, -half, -half + t * size, half);
        }
    }

    private void spawnParticleOnEdge(Location center, Particle particle, double x, double y, double z) {
        Location loc = center.clone().add(x, y, z);
        center.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);
    }

    /**
     * Creates a 3D wave/ripple effect on ground
     */
    public void spawn3DRipple(Location center, Particle particle, double radius, int rings) {
        for (int ring = 0; ring < rings; ring++) {
            double ringRadius = (radius * ring) / rings;
            int points = 8 + ring * 4;

            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI * i) / points;
                double x = Math.cos(angle) * ringRadius;
                double z = Math.sin(angle) * ringRadius;

                Location particleLoc = center.clone().add(x, 0, z);
                center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0.1, 0, 0);
            }
        }
    }
}
