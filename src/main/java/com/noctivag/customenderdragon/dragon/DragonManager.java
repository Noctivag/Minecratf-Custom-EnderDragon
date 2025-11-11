package com.noctivag.customenderdragon.dragon;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

import java.util.*;

/**
 * Manages all active custom dragons
 */
public class DragonManager {
    private final CustomEnderDragonPlugin plugin;
    private final Map<UUID, CustomDragon> activeDragons;

    public DragonManager(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
        this.activeDragons = new HashMap<>();
    }

    public CustomDragon spawnDragon(Location location, DragonVariant variant) {
        EnderDragon dragon = (EnderDragon) location.getWorld()
                .spawnEntity(location, EntityType.ENDER_DRAGON);

        CustomDragon customDragon = new CustomDragon(plugin, dragon, variant);
        activeDragons.put(dragon.getUniqueId(), customDragon);

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

    public CustomDragon getCustomDragon(EnderDragon dragon) {
        return activeDragons.get(dragon.getUniqueId());
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

    public boolean isCustomDragon(EnderDragon dragon) {
        return activeDragons.containsKey(dragon.getUniqueId());
    }

    public void shutdown() {
        removeAllDragons();
    }
}
