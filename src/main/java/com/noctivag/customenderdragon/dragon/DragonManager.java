package com.noctivag.customenderdragon.dragon;

import com.noctivag.customenderdragon.CustomEnderDragonMod;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * Manages all active custom dragons
 */
public class DragonManager {
    private final Map<UUID, CustomDragon> activeDragons;

    public DragonManager() {
        this.activeDragons = new HashMap<>();
    }

    public CustomDragon spawnDragon(ServerWorld world, BlockPos pos, DragonVariant variant) {
        try {
            if (world == null || pos == null || variant == null) {
                CustomEnderDragonMod.LOGGER.error("Cannot spawn dragon with null parameters");
                return null;
            }

            EnderDragonEntity dragon = new EnderDragonEntity(net.minecraft.entity.EntityType.ENDER_DRAGON, world);
            dragon.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);

            world.spawnEntity(dragon);

            CustomDragon customDragon = new CustomDragon(dragon, variant);
            activeDragons.put(dragon.getUuid(), customDragon);

            CustomEnderDragonMod.LOGGER.info("Spawned {} dragon at {}", variant.name(), pos);
            return customDragon;
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Failed to spawn dragon", e);
            return null;
        }
    }

    public void removeDragon(UUID uuid) {
        try {
            if (uuid == null) {
                return;
            }
            CustomDragon dragon = activeDragons.remove(uuid);
            if (dragon != null) {
                dragon.remove();
            }
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error removing dragon", e);
        }
    }

    public CustomDragon getCustomDragon(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return activeDragons.get(uuid);
    }

    public CustomDragon getCustomDragon(EnderDragonEntity dragon) {
        if (dragon == null) {
            return null;
        }
        try {
            return activeDragons.get(dragon.getUuid());
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error getting custom dragon", e);
            return null;
        }
    }

    public Collection<CustomDragon> getAllDragons() {
        return new ArrayList<>(activeDragons.values());
    }

    public int removeAllDragons() {
        try {
            int count = activeDragons.size();
            activeDragons.values().forEach(dragon -> {
                try {
                    dragon.remove();
                } catch (Exception e) {
                    CustomEnderDragonMod.LOGGER.error("Error removing dragon during cleanup", e);
                }
            });
            activeDragons.clear();
            return count;
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error during removeAllDragons", e);
            return 0;
        }
    }

    public boolean isCustomDragon(EnderDragonEntity dragon) {
        if (dragon == null) {
            return false;
        }
        try {
            return activeDragons.containsKey(dragon.getUuid());
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error checking if dragon is custom", e);
            return false;
        }
    }

    /**
     * Called every tick to update all dragons
     */
    public void tick() {
        try {
            // Remove dead dragons with safety checks
            activeDragons.entrySet().removeIf(entry -> {
                try {
                    CustomDragon dragon = entry.getValue();
                    if (dragon == null || dragon.getDragon() == null) {
                        return true; // Remove null entries
                    }
                    EnderDragonEntity dragonEntity = dragon.getDragon();
                    return dragonEntity.isDead() || dragonEntity.isRemoved();
                } catch (Exception e) {
                    CustomEnderDragonMod.LOGGER.error("Error checking dragon state, removing from list", e);
                    return true; // Remove problematic entries
                }
            });

            // Tick all active dragons with individual error handling
            activeDragons.values().forEach(dragon -> {
                try {
                    if (dragon != null) {
                        dragon.tick();
                    }
                } catch (Exception e) {
                    CustomEnderDragonMod.LOGGER.error("Error during dragon tick", e);
                }
            });
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Critical error during dragon manager tick", e);
        }
    }

    public void shutdown() {
        try {
            removeAllDragons();
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error during shutdown", e);
        }
    }
}
