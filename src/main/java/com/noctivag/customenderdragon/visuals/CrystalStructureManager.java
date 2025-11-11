package com.noctivag.customenderdragon.visuals;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates large 3D crystal/pillar structures on the ground
 * These serve as arena decorations and power sources for the dragon
 */
public class CrystalStructureManager {
    private final CustomEnderDragonPlugin plugin;

    public CrystalStructureManager(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawns crystal pillars around a location in a circle
     */
    public CrystalArena createCrystalArena(Location center, DragonVariant variant) {
        CrystalArena arena = new CrystalArena();

        if (!plugin.getConfig().getBoolean("visuals.arena.enabled", true)) {
            return arena;
        }

        int pillarCount = plugin.getConfig().getInt("visuals.arena.pillar-count", 4);
        double radius = plugin.getConfig().getDouble("visuals.arena.radius", 15.0);

        for (int i = 0; i < pillarCount; i++) {
            double angle = (2 * Math.PI * i) / pillarCount;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location pillarLoc = center.clone().add(x, 0, z);

            // Find ground level
            while (pillarLoc.getBlock().getType() == Material.AIR && pillarLoc.getY() > center.getY() - 10) {
                pillarLoc.subtract(0, 1, 0);
            }
            pillarLoc.add(0, 1, 0); // One block above ground

            CrystalPillar pillar = createPillar(pillarLoc, variant);
            arena.pillars.add(pillar);
        }

        // Start arena effects
        startArenaEffects(arena, center);

        return arena;
    }

    /**
     * Creates a single crystal pillar
     */
    private CrystalPillar createPillar(Location location, DragonVariant variant) {
        CrystalPillar pillar = new CrystalPillar();
        pillar.baseLocation = location.clone();

        Material baseMaterial = getVariantBaseMaterial(variant);
        Material crystalMaterial = getVariantCrystalMaterial(variant);
        Color glowColor = getVariantColor(variant);

        // Create base (3-block pillar)
        for (int y = 0; y < 3; y++) {
            Location blockLoc = location.clone().add(0, y, 0);
            BlockDisplay base = (BlockDisplay) location.getWorld().spawnEntity(blockLoc, EntityType.BLOCK_DISPLAY);
            base.setBlock(baseMaterial.createBlockData());
            base.setBrightness(new Display.Brightness(15, 15));
            base.setGlowing(true);
            base.setGlowColorOverride(glowColor);

            pillar.baseBlocks.add(base);
        }

        // Create crystal top (larger, rotating)
        Location crystalLoc = location.clone().add(0, 3.5, 0);
        BlockDisplay crystal = (BlockDisplay) location.getWorld().spawnEntity(crystalLoc, EntityType.BLOCK_DISPLAY);
        crystal.setBlock(crystalMaterial.createBlockData());
        crystal.setBrightness(new Display.Brightness(15, 15));
        crystal.setGlowing(true);
        crystal.setGlowColorOverride(glowColor);

        // Scale up the crystal
        Transformation trans = crystal.getTransformation();
        trans.getScale().set(2.0f, 3.0f, 2.0f);
        crystal.setTransformation(trans);

        pillar.crystalTop = crystal;

        // Create energy core (glowing orb)
        Location coreLoc = location.clone().add(0, 5.5, 0);
        ItemDisplay core = (ItemDisplay) location.getWorld().spawnEntity(coreLoc, EntityType.ITEM_DISPLAY);
        core.setItemStack(new org.bukkit.inventory.ItemStack(getVariantCoreMaterial(variant)));
        core.setBrightness(new Display.Brightness(15, 15));
        core.setBillboard(Display.Billboard.CENTER);
        core.setGlowing(true);
        core.setGlowColorOverride(glowColor);

        Transformation coreTrans = core.getTransformation();
        coreTrans.getScale().set(1.5f, 1.5f, 1.5f);
        core.setTransformation(coreTrans);

        pillar.energyCore = core;

        // Add floating runes around pillar
        int runeCount = 4;
        double runeRadius = 1.5;
        for (int i = 0; i < runeCount; i++) {
            double angle = (2 * Math.PI * i) / runeCount;
            double x = Math.cos(angle) * runeRadius;
            double z = Math.sin(angle) * runeRadius;

            Location runeLoc = location.clone().add(x, 2, z);
            ItemDisplay rune = (ItemDisplay) location.getWorld().spawnEntity(runeLoc, EntityType.ITEM_DISPLAY);
            rune.setItemStack(new org.bukkit.inventory.ItemStack(Material.ENCHANTED_BOOK));
            rune.setBrightness(new Display.Brightness(15, 15));
            rune.setBillboard(Display.Billboard.VERTICAL);
            rune.setGlowing(true);
            rune.setGlowColorOverride(glowColor);

            pillar.floatingRunes.add(rune);
        }

        // Start pillar animations
        startPillarAnimation(pillar);

        return pillar;
    }

    private void startPillarAnimation(CrystalPillar pillar) {
        new BukkitRunnable() {
            double rotation = 0;
            double pulsePhase = 0;

            @Override
            public void run() {
                if (pillar.crystalTop == null || !pillar.crystalTop.isValid()) {
                    cancel();
                    return;
                }

                rotation += 0.02;
                pulsePhase += 0.05;

                // Rotate crystal
                Transformation trans = pillar.crystalTop.getTransformation();
                trans.getLeftRotation().set(new AxisAngle4f((float) rotation, 0, 1, 0));
                pillar.crystalTop.setTransformation(trans);

                // Pulse energy core
                if (pillar.energyCore != null && pillar.energyCore.isValid()) {
                    float scale = 1.5f + (float) Math.sin(pulsePhase) * 0.3f;
                    Transformation coreTrans = pillar.energyCore.getTransformation();
                    coreTrans.getScale().set(scale, scale, scale);
                    pillar.energyCore.setTransformation(coreTrans);
                }

                // Orbit runes
                for (int i = 0; i < pillar.floatingRunes.size(); i++) {
                    ItemDisplay rune = pillar.floatingRunes.get(i);
                    if (!rune.isValid()) continue;

                    double angle = rotation + (2 * Math.PI * i / pillar.floatingRunes.size());
                    double runeRadius = 1.5 + Math.sin(pulsePhase + i) * 0.3;

                    double x = Math.cos(angle) * runeRadius;
                    double z = Math.sin(angle) * runeRadius;
                    double y = 2 + Math.sin(pulsePhase + i * 0.5) * 0.5;

                    Location runeLoc = pillar.baseLocation.clone().add(x, y, z);
                    rune.teleport(runeLoc);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startArenaEffects(CrystalArena arena, Location center) {
        new BukkitRunnable() {
            double phase = 0;

            @Override
            public void run() {
                if (arena.pillars.isEmpty()) {
                    cancel();
                    return;
                }

                phase += 0.1;

                // Energy beams between pillars
                if (plugin.getConfig().getBoolean("visuals.arena.energy-beams", true)) {
                    // Particles will create beams between pillars
                    // This is handled by ParticleManager's 3D formations
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private Material getVariantBaseMaterial(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Material.BLACKSTONE;
            case ICE -> Material.PACKED_ICE;
            case LIGHTNING -> Material.GOLD_BLOCK;
            case SHADOW -> Material.DEEPSLATE;
            case VOID -> Material.CRYING_OBSIDIAN;
        };
    }

    private Material getVariantCrystalMaterial(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Material.RED_STAINED_GLASS;
            case ICE -> Material.BLUE_ICE;
            case LIGHTNING -> Material.YELLOW_STAINED_GLASS;
            case SHADOW -> Material.BLACK_STAINED_GLASS;
            case VOID -> Material.PURPLE_STAINED_GLASS;
        };
    }

    private Material getVariantCoreMaterial(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Material.FIRE_CHARGE;
            case ICE -> Material.SNOWBALL;
            case LIGHTNING -> Material.GLOWSTONE_DUST;
            case SHADOW -> Material.ENDER_PEARL;
            case VOID -> Material.DRAGON_BREATH;
        };
    }

    private Color getVariantColor(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Color.fromRGB(255, 69, 0);
            case ICE -> Color.fromRGB(173, 216, 230);
            case LIGHTNING -> Color.fromRGB(255, 255, 0);
            case SHADOW -> Color.fromRGB(75, 0, 130);
            case VOID -> Color.fromRGB(138, 43, 226);
        };
    }

    // ========== Data Classes ==========

    public static class CrystalArena {
        public List<CrystalPillar> pillars = new ArrayList<>();

        public void removeAll() {
            pillars.forEach(CrystalPillar::remove);
            pillars.clear();
        }
    }

    public static class CrystalPillar {
        public Location baseLocation;
        public List<BlockDisplay> baseBlocks = new ArrayList<>();
        public BlockDisplay crystalTop;
        public ItemDisplay energyCore;
        public List<ItemDisplay> floatingRunes = new ArrayList<>();

        public void remove() {
            baseBlocks.forEach(Entity::remove);
            if (crystalTop != null) crystalTop.remove();
            if (energyCore != null) energyCore.remove();
            floatingRunes.forEach(Entity::remove);

            baseBlocks.clear();
            floatingRunes.clear();
        }
    }
}
