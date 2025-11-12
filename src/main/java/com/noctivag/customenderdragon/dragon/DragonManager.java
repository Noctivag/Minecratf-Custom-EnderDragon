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
        EnderDragonEntity dragon = new EnderDragonEntity(net.minecraft.entity.EntityType.ENDER_DRAGON, world);
        dragon.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        
        world.spawnEntity(dragon);

        CustomDragon customDragon = new CustomDragon(dragon, variant);
        activeDragons.put(dragon.getUuid(), customDragon);

        CustomEnderDragonMod.LOGGER.info("Spawned {} dragon at {}", variant.name(), pos);
        return customDragon;
    }

    public void removeDragon(UUID uuid) {
        CustomDragon dragon = activeDragons.remove(uuid);
        if (dragon != null) {
            dragon.remove();
        }
    }

    public CustomDragon getCustomDragon(UUID uuid) {
        return activeDragons.get(uuid);
    }

    public CustomDragon getCustomDragon(EnderDragonEntity dragon) {
        return activeDragons.get(dragon.getUuid());
    }

    public Collection<CustomDragon> getAllDragons() {
        return new ArrayList<>(activeDragons.values());
    }

    public int removeAllDragons() {
        int count = activeDragons.size();
        activeDragons.values().forEach(CustomDragon::remove);
        activeDragons.clear();
        return count;
    }

    public boolean isCustomDragon(EnderDragonEntity dragon) {
        return activeDragons.containsKey(dragon.getUuid());
    }

    /**
     * Called every tick to update all dragons
     */
    public void tick() {
        // Remove dead dragons
        activeDragons.entrySet().removeIf(entry -> {
            CustomDragon dragon = entry.getValue();
            return dragon.getDragon().isDead() || dragon.getDragon().isRemoved();
        });

        // Tick all active dragons
        activeDragons.values().forEach(CustomDragon::tick);
    }

    public void shutdown() {
        removeAllDragons();
    }
}
