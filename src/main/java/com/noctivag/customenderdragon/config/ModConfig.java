package com.noctivag.customenderdragon.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noctivag.customenderdragon.CustomEnderDragonMod;
import com.noctivag.customenderdragon.dragon.DragonVariant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration system for the mod using JSON
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config config;

    public static class Config {
        public Map<String, VariantConfig> variants = new HashMap<>();
        public GeneralConfig general = new GeneralConfig();
        public VariantWeightsConfig variantWeights = new VariantWeightsConfig();
        public MessagesConfig messages = new MessagesConfig();

        public Config() {
            // Initialize default variant configs
            for (DragonVariant variant : DragonVariant.values()) {
                variants.put(variant.name(), new VariantConfig(variant));
            }
        }
    }

    public static class VariantConfig {
        public boolean enabled = true;
        public String displayName;
        public float health = 200.0f;
        public Map<String, AbilityConfig> abilities = new HashMap<>();
        public Map<String, ParticleConfig> particles = new HashMap<>();
        public Map<String, LootConfig> loot = new HashMap<>();

        public VariantConfig() {}

        public VariantConfig(DragonVariant variant) {
            this.displayName = variant.getDisplayName();
            this.health = switch (variant) {
                case FIRE -> 300.0f;
                case ICE -> 350.0f;
                case LIGHTNING -> 250.0f;
                case SHADOW -> 400.0f;
                case VOID -> 500.0f;
            };

            // Initialize default abilities
            abilities.put("fireball", new AbilityConfig(true, 100));
            abilities.put("fire-aura", new AbilityConfig(true, 60));
            abilities.put("meteor-shower", new AbilityConfig(true, 200));
        }
    }

    public static class AbilityConfig {
        public boolean enabled;
        public int cooldown; // in ticks
        public double damage = 10.0;
        public double radius = 10.0;

        public AbilityConfig() {}

        public AbilityConfig(boolean enabled, int cooldown) {
            this.enabled = enabled;
            this.cooldown = cooldown;
        }
    }

    public static class ParticleConfig {
        public String type = "FLAME";
        public int count = 5;
        public double offset = 0.5;
    }

    public static class LootConfig {
        public String material;
        public int chance = 100;
        public String amount = "1";
    }

    public static class GeneralConfig {
        public boolean enableCustomDragons = true;
        public boolean bossBarEnabled = true;
        public int particleUpdateInterval = 5;
        public int abilityUpdateInterval = 40;
    }

    public static class VariantWeightsConfig {
        public int fireWeight = 20;
        public int iceWeight = 20;
        public int lightningWeight = 20;
        public int shadowWeight = 20;
        public int voidWeight = 20;
    }

    public static class MessagesConfig {
        public String prefix = "§5§l[CustomDragon]§r ";
        public String noPermission = "§cYou don't have permission to do that!";
        public String dragonSpawned = "§aSpawned %variant% Dragon at your location!";
        public String dragonKilled = "§e%variant% Dragon has been slain!";
        public String invalidVariant = "§cInvalid variant! Use: FIRE, ICE, LIGHTNING, SHADOW, VOID";
        public String noDragons = "§cNo custom dragons found!";
    }

    public static void load() {
        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, "customenderdragon.json");

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, Config.class);
                CustomEnderDragonMod.LOGGER.info("Configuration loaded successfully");
            } catch (Exception e) {
                CustomEnderDragonMod.LOGGER.error("Failed to load config, using defaults", e);
                config = new Config();
                save();
            }
        } else {
            config = new Config();
            save();
        }
    }

    public static void save() {
        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, "customenderdragon.json");

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
            CustomEnderDragonMod.LOGGER.info("Configuration saved successfully");
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Failed to save config", e);
        }
    }

    // Getters
    public static VariantConfig getVariantConfig(DragonVariant variant) {
        return config.variants.get(variant.name());
    }

    public static AbilityConfig getAbilityConfig(DragonVariant variant, String abilityName) {
        VariantConfig variantConfig = getVariantConfig(variant);
        return variantConfig != null ? variantConfig.abilities.get(abilityName) : null;
    }

    public static Config getConfig() {
        return config;
    }
}
