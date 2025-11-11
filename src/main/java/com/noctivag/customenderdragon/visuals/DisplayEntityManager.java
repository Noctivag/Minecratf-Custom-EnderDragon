package com.noctivag.customenderdragon.visuals;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages 3D display entities for serverside visual enhancements
 * Uses Display Entities (1.19.4+) - fully serverside, no client mods required!
 */
public class DisplayEntityManager {
    private final CustomEnderDragonPlugin plugin;

    public DisplayEntityManager(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a complete 3D decoration set for a dragon
     */
    public DragonDecorations createDragonDecorations(EnderDragon dragon, DragonVariant variant) {
        DragonDecorations decorations = new DragonDecorations();
        Location loc = dragon.getLocation();

        if (!plugin.getConfig().getBoolean("visuals.3d-decorations.enabled", true)) {
            return decorations;
        }

        // Create orbiting elemental orbs
        if (plugin.getConfig().getBoolean("visuals.3d-decorations.orbiting-orbs", true)) {
            decorations.orbitingOrbs = createOrbitingOrbs(dragon, variant);
        }

        // Create floating crystals
        if (plugin.getConfig().getBoolean("visuals.3d-decorations.floating-crystals", true)) {
            decorations.floatingCrystals = createFloatingCrystals(dragon, variant);
        }

        // Create rune circles
        if (plugin.getConfig().getBoolean("visuals.3d-decorations.rune-circles", true)) {
            decorations.runeCircles = createRuneCircles(dragon, variant);
        }

        // Create elemental aura sphere
        if (plugin.getConfig().getBoolean("visuals.3d-decorations.aura-sphere", true)) {
            decorations.auraSphere = createAuraSphere(dragon, variant);
        }

        return decorations;
    }

    /**
     * Creates orbiting elemental orbs around the dragon
     */
    private List<Display> createOrbitingOrbs(EnderDragon dragon, DragonVariant variant) {
        List<Display> orbs = new ArrayList<>();
        int orbCount = plugin.getConfig().getInt("visuals.3d-decorations.orb-count", 6);
        double orbitRadius = plugin.getConfig().getDouble("visuals.3d-decorations.orbit-radius", 5.0);

        Material orbMaterial = getVariantMaterial(variant);

        for (int i = 0; i < orbCount; i++) {
            double angle = (2 * Math.PI * i) / orbCount;
            double x = Math.cos(angle) * orbitRadius;
            double z = Math.sin(angle) * orbitRadius;

            Location orbLoc = dragon.getLocation().clone().add(x, 2, z);

            ItemDisplay display = (ItemDisplay) dragon.getWorld().spawnEntity(orbLoc, EntityType.ITEM_DISPLAY);
            display.setItemStack(new ItemStack(orbMaterial));
            display.setBrightness(new Display.Brightness(15, 15)); // Full bright
            display.setBillboard(Display.Billboard.CENTER);

            // Set transformation for size
            Transformation trans = display.getTransformation();
            trans.getScale().set(0.8f, 0.8f, 0.8f);
            display.setTransformation(trans);

            display.setGlowing(true);
            display.setGlowColorOverride(getVariantColor(variant));

            orbs.add(display);
        }

        // Start orbit animation
        startOrbitAnimation(orbs, dragon, orbitRadius);

        return orbs;
    }

    /**
     * Creates floating crystals that hover around the dragon
     */
    private List<Display> createFloatingCrystals(EnderDragon dragon, DragonVariant variant) {
        List<Display> crystals = new ArrayList<>();
        int crystalCount = plugin.getConfig().getInt("visuals.3d-decorations.crystal-count", 4);

        Material crystalMaterial = getVariantCrystalMaterial(variant);

        for (int i = 0; i < crystalCount; i++) {
            double angle = (2 * Math.PI * i) / crystalCount;
            double radius = 8.0;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = Math.sin(angle * 2) * 3 + 3; // Wave pattern

            Location crystalLoc = dragon.getLocation().clone().add(x, y, z);

            BlockDisplay display = (BlockDisplay) dragon.getWorld().spawnEntity(crystalLoc, EntityType.BLOCK_DISPLAY);
            display.setBlock(crystalMaterial.createBlockData());
            display.setBrightness(new Display.Brightness(15, 15));

            // Rotating crystal transformation
            Transformation trans = display.getTransformation();
            trans.getScale().set(1.5f, 2.5f, 1.5f); // Tall crystal
            display.setTransformation(trans);

            display.setGlowing(true);
            display.setGlowColorOverride(getVariantColor(variant));

            crystals.add(display);
        }

        // Start floating animation
        startCrystalAnimation(crystals, dragon);

        return crystals;
    }

    /**
     * Creates rotating rune circles around the dragon
     */
    private List<Display> createRuneCircles(EnderDragon dragon, DragonVariant variant) {
        List<Display> runes = new ArrayList<>();
        int runeCount = plugin.getConfig().getInt("visuals.3d-decorations.rune-count", 8);
        double circleRadius = 4.0;

        Material runeMaterial = getVariantRuneMaterial(variant);

        // Create horizontal ring
        for (int i = 0; i < runeCount; i++) {
            double angle = (2 * Math.PI * i) / runeCount;
            double x = Math.cos(angle) * circleRadius;
            double z = Math.sin(angle) * circleRadius;

            Location runeLoc = dragon.getLocation().clone().add(x, 1, z);

            ItemDisplay display = (ItemDisplay) dragon.getWorld().spawnEntity(runeLoc, EntityType.ITEM_DISPLAY);
            display.setItemStack(new ItemStack(runeMaterial));
            display.setBrightness(new Display.Brightness(15, 15));
            display.setBillboard(Display.Billboard.VERTICAL);

            Transformation trans = display.getTransformation();
            trans.getScale().set(0.6f, 0.6f, 0.6f);
            display.setTransformation(trans);

            display.setGlowing(true);
            display.setGlowColorOverride(getVariantColor(variant));

            runes.add(display);
        }

        // Start rotation animation
        startRuneAnimation(runes, dragon, circleRadius);

        return runes;
    }

    /**
     * Creates a translucent aura sphere around the dragon
     */
    private List<Display> createAuraSphere(EnderDragon dragon, DragonVariant variant) {
        List<Display> sphereParts = new ArrayList<>();

        // Create multiple layers for sphere effect
        int layers = 3;
        for (int layer = 0; layer < layers; layer++) {
            double radius = 6.0 + layer;
            int points = 12 + (layer * 4);

            for (int i = 0; i < points; i++) {
                double theta = (2 * Math.PI * i) / points;
                double phi = Math.PI * (layer + 0.5) / layers;

                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(phi) * Math.sin(theta);

                Location sphereLoc = dragon.getLocation().clone().add(x, y + 3, z);

                ItemDisplay display = (ItemDisplay) dragon.getWorld().spawnEntity(sphereLoc, EntityType.ITEM_DISPLAY);
                display.setItemStack(new ItemStack(Material.GLASS));
                display.setBrightness(new Display.Brightness(15, 15));
                display.setBillboard(Display.Billboard.CENTER);

                Transformation trans = display.getTransformation();
                trans.getScale().set(0.3f, 0.3f, 0.3f);
                display.setTransformation(trans);

                display.setGlowing(true);
                display.setGlowColorOverride(getVariantColor(variant));

                sphereParts.add(display);
            }
        }

        startSphereAnimation(sphereParts, dragon);

        return sphereParts;
    }

    // ========== Animation Tasks ==========

    private void startOrbitAnimation(List<Display> orbs, EnderDragon dragon, double radius) {
        new BukkitRunnable() {
            double angle = 0;

            @Override
            public void run() {
                if (!dragon.isValid() || dragon.isDead()) {
                    cancel();
                    return;
                }

                angle += 0.05;
                Location center = dragon.getLocation().clone().add(0, 2, 0);

                for (int i = 0; i < orbs.size(); i++) {
                    Display orb = orbs.get(i);
                    if (!orb.isValid()) continue;

                    double orbAngle = angle + (2 * Math.PI * i / orbs.size());
                    double x = Math.cos(orbAngle) * radius;
                    double z = Math.sin(orbAngle) * radius;
                    double y = Math.sin(orbAngle * 2) * 1.5; // Sine wave up/down

                    Location newLoc = center.clone().add(x, y, z);
                    orb.teleport(newLoc);

                    // Rotate the orb
                    Transformation trans = orb.getTransformation();
                    trans.getLeftRotation().set(new AxisAngle4f((float) orbAngle, 0, 1, 0));
                    orb.setTransformation(trans);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startCrystalAnimation(List<Display> crystals, EnderDragon dragon) {
        new BukkitRunnable() {
            double time = 0;

            @Override
            public void run() {
                if (!dragon.isValid() || dragon.isDead()) {
                    cancel();
                    return;
                }

                time += 0.03;
                Location center = dragon.getLocation();

                for (int i = 0; i < crystals.size(); i++) {
                    Display crystal = crystals.get(i);
                    if (!crystal.isValid()) continue;

                    double angle = (2 * Math.PI * i / crystals.size()) + time;
                    double radius = 8.0 + Math.sin(time * 2) * 1.5;

                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    double y = Math.sin(angle * 2 + time) * 3 + 3;

                    Location newLoc = center.clone().add(x, y, z);
                    crystal.teleport(newLoc);

                    // Rotate crystal
                    Transformation trans = crystal.getTransformation();
                    trans.getLeftRotation().set(new AxisAngle4f((float) time, 0, 1, 0));
                    trans.getRightRotation().set(new AxisAngle4f((float) (time * 0.5), 1, 0, 0));
                    crystal.setTransformation(trans);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startRuneAnimation(List<Display> runes, EnderDragon dragon, double radius) {
        new BukkitRunnable() {
            double rotation = 0;

            @Override
            public void run() {
                if (!dragon.isValid() || dragon.isDead()) {
                    cancel();
                    return;
                }

                rotation += 0.03;
                Location center = dragon.getLocation().clone().add(0, 1, 0);

                for (int i = 0; i < runes.size(); i++) {
                    Display rune = runes.get(i);
                    if (!rune.isValid()) continue;

                    double angle = rotation + (2 * Math.PI * i / runes.size());
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;

                    Location newLoc = center.clone().add(x, 0, z);
                    rune.teleport(newLoc);

                    // Pulse effect
                    float scale = 0.6f + (float) Math.sin(rotation * 3 + i) * 0.2f;
                    Transformation trans = rune.getTransformation();
                    trans.getScale().set(scale, scale, scale);
                    rune.setTransformation(trans);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startSphereAnimation(List<Display> sphereParts, EnderDragon dragon) {
        new BukkitRunnable() {
            double phase = 0;

            @Override
            public void run() {
                if (!dragon.isValid() || dragon.isDead()) {
                    cancel();
                    return;
                }

                phase += 0.05;

                for (int i = 0; i < sphereParts.size(); i++) {
                    Display part = sphereParts.get(i);
                    if (!part.isValid()) continue;

                    // Pulsing glow effect
                    float alpha = 0.3f + (float) Math.sin(phase + i * 0.1) * 0.2f;

                    // Slight rotation
                    Transformation trans = part.getTransformation();
                    trans.getLeftRotation().set(new AxisAngle4f((float) phase, 0, 1, 0));
                    part.setTransformation(trans);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    // ========== Helper Methods ==========

    private Material getVariantMaterial(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Material.FIRE_CHARGE;
            case ICE -> Material.ICE;
            case LIGHTNING -> Material.GLOWSTONE_DUST;
            case SHADOW -> Material.ENDER_PEARL;
            case VOID -> Material.DRAGON_BREATH;
        };
    }

    private Material getVariantCrystalMaterial(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Material.RED_STAINED_GLASS;
            case ICE -> Material.BLUE_ICE;
            case LIGHTNING -> Material.YELLOW_STAINED_GLASS;
            case SHADOW -> Material.OBSIDIAN;
            case VOID -> Material.PURPLE_STAINED_GLASS;
        };
    }

    private Material getVariantRuneMaterial(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Material.MAGMA_CREAM;
            case ICE -> Material.SNOWBALL;
            case LIGHTNING -> Material.GLOWSTONE_DUST;
            case SHADOW -> Material.ECHO_SHARD;
            case VOID -> Material.END_CRYSTAL;
        };
    }

    private Color getVariantColor(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Color.fromRGB(255, 69, 0);      // Orange-red
            case ICE -> Color.fromRGB(173, 216, 230);    // Light blue
            case LIGHTNING -> Color.fromRGB(255, 255, 0); // Yellow
            case SHADOW -> Color.fromRGB(75, 0, 130);    // Indigo
            case VOID -> Color.fromRGB(138, 43, 226);    // Purple
        };
    }

    /**
     * Container for all dragon decorations
     */
    public static class DragonDecorations {
        public List<Display> orbitingOrbs = new ArrayList<>();
        public List<Display> floatingCrystals = new ArrayList<>();
        public List<Display> runeCircles = new ArrayList<>();
        public List<Display> auraSphere = new ArrayList<>();

        public void removeAll() {
            orbitingOrbs.forEach(Entity::remove);
            floatingCrystals.forEach(Entity::remove);
            runeCircles.forEach(Entity::remove);
            auraSphere.forEach(Entity::remove);

            orbitingOrbs.clear();
            floatingCrystals.clear();
            runeCircles.clear();
            auraSphere.clear();
        }

        public List<Display> getAllDisplays() {
            List<Display> all = new ArrayList<>();
            all.addAll(orbitingOrbs);
            all.addAll(floatingCrystals);
            all.addAll(runeCircles);
            all.addAll(auraSphere);
            return all;
        }
    }
}
