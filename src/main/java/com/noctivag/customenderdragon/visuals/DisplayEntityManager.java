package com.noctivag.customenderdragon.visuals;

import com.noctivag.customenderdragon.dragon.DragonVariant;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

/**
 * Manages 3D visual decorations around dragons (Stub for Fabric)
 * Note: Display entities are a 1.19.4+ feature
 */
public class DisplayEntityManager {

    public DragonDecorations createDragonDecorations(EnderDragonEntity dragon, DragonVariant variant) {
        // TODO: Implement display entity decorations when targeting Minecraft 1.19.4+
        return new DragonDecorations();
    }

    public static class DragonDecorations {
        public void update(EnderDragonEntity dragon) {
            // TODO: Update decoration positions
        }

        public void remove() {
            // TODO: Remove all decoration entities
        }
    }
}
