# Custom EnderDragon Mod - Fabric Edition

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.java.com/)
[![Fabric](https://img.shields.io/badge/Fabric-API-blue.svg)](https://fabricmc.net/)

## 🐲 Overview

Transform the Ender Dragon fight into an **epic boss battle** with this Fabric mod! Featuring **5 unique dragon variants**, each with their own abilities, visual effects, and combat phases. Every EnderDragon that spawns will automatically become a custom variant!

## ✨ Key Features

### 🔥 5 Unique Dragon Variants

Each naturally spawning EnderDragon is **automatically converted** to a random variant:

- **FIRE Dragon** - Master of flames and meteors
- **ICE Dragon** - Controller of frost and blizzards  
- **LIGHTNING Dragon** - Wielder of thunder and storms
- **SHADOW Dragon** - Manipulator of darkness
- **VOID Dragon** - Reality warper with devastating power

### ⚙️ Auto-Conversion System

✅ **No manual spawning required!** The mod automatically:
- Detects when an EnderDragon spawns (naturally or via `/summon`)
- Converts it to a random custom variant based on configured weights
- Applies unique attributes, abilities, and visual effects
- Broadcasts a spawn message to all players

### ⚔️ Dynamic Combat System

- **3-Phase Combat** - Dragons evolve and become more dangerous as they lose health
  - Phase 1 (100-67% HP): Awakening
  - Phase 2 (66-34% HP): Enraged
  - Phase 3 (33-0% HP): Desperate Fury
- **Phase-Specific Abilities** - Ultimate abilities unlock in later phases
- **Boss Bars** - Real-time health tracking with phase indicators
- **Smart AI** - Dragons target nearest players and use abilities strategically

### 🎨 Epic Visual Effects

- **Custom Particle Systems** - Unique particles for each dragon variant
- **Wing Trails** - Dragons leave spectacular trails as they fly
- **Ability Effects** - Stunning visual effects for every ability
- **Phase Change Animations** - Explosive effects when entering new phases
- **3D Particle Formations** - Spheres, helixes, vortexes, toruses, and more

### 🌟 3D Visual Decorations (Serverside Only!)

**No client mods or resource packs required!** Uses Display Entities (1.19.4+)

#### Dragon Decorations
- **Orbiting Elemental Orbs** - Glowing orbs that orbit around the dragon
- **Floating Crystals** - Large crystals that hover and rotate near the dragon
- **Rune Circles** - Mystical runes that rotate horizontally around the dragon
- **Aura Sphere** - Translucent particle sphere surrounding the dragon

#### Crystal Arena Structures
- **Crystal Pillars** - Massive elemental pillars spawn in a circle around the dragon
  - **Animated Bases** - Glowing block displays for pillar foundations
  - **Rotating Crystal Tops** - Large spinning crystals at the top
  - **Energy Cores** - Pulsing orbs of elemental energy
  - **Orbiting Runes** - Floating runes circle each pillar

#### Advanced Particle Formations
- **3D Spheres** - Perfect Fibonacci sphere distributions
- **Helixes** - DNA-like double/triple helix effects
- **Vortexes** - Swirling tornado formations
- **Toruses** - Donut-shaped particle rings
- **Energy Beams** - Spiral beams connecting crystals
- **Star Bursts** - Explosive radial patterns
- **Geometric Cubes** - Wireframe cubic formations

All decorations:
- Animate smoothly in real-time
- Auto-cleanup when dragon dies
- Fully configurable in config.yml
- Variant-specific colors and materials
- Zero client-side requirements

### 💎 Custom Loot System

- **Variant-Specific Drops** - Each dragon drops unique themed loot
- **Configurable Chances** - Adjust drop rates for all items
- **Range-Based Amounts** - Random quantities for exciting rewards
- **5x Experience** - Dragons drop 12,000 XP (vs normal 2,400)

### 🎯 Dragon Abilities

#### Fire Dragon
- **Fireball** - Explosive projectile attacks
- **Fire Aura** - Burns nearby players
- **Meteor Shower** (Phase 3) - Rains fire from the sky

#### Ice Dragon
- **Ice Shard** - Freezing projectiles that slow players
- **Freeze Aura** - Applies slowness to nearby players
- **Blizzard** (Phase 3) - Massive area-of-effect freeze attack

#### Lightning Dragon
- **Lightning Strike** - Calls down lightning with chain damage
- **Thunder Aura** - Random lightning strikes near the dragon
- **Storm Rage** (Phase 3) - Unleashes a devastating storm

#### Shadow Dragon
- **Shadow Strike** - Blinds and damages targets
- **Teleport** - Instantly moves toward players
- **Shadow Clones** (Phase 2) - Creates decoys (reserved for future)

#### Void Dragon
- **Void Beam** - Applies wither effect and massive damage
- **Gravity Well** - Pulls players toward the dragon
- **Reality Tear** (Phase 3) - Ultimate devastating attack

## 🎮 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/customdragon spawn <variant>` | Manually spawn a specific variant | op level 2 |
| `/customdragon list` | List all active custom dragons | op level 2 |
| `/customdragon remove` | Remove all custom dragons | op level 2 |

**Note:** Dragons also spawn automatically when summoned via `/summon minecraft:ender_dragon`

## ⚙️ Configuration

The mod uses a **JSON configuration** file located at `config/customenderdragon.json`:

### General Settings
```json
{
  "general": {
    "enableCustomDragons": true,
    "bossBarEnabled": true,
    "particleUpdateInterval": 5,
    "abilityUpdateInterval": 40
  }
}
```

### Variant Spawn Weights
Control the probability of each variant spawning:
```json
{
  "variantWeights": {
    "fireWeight": 20,
    "iceWeight": 20,
    "lightningWeight": 20,
    "shadowWeight": 20,
    "voidWeight": 20
  }
}
```

Higher weights = higher spawn chance. Set a weight to `0` to disable that variant entirely.

### Per-Variant Configuration
```json
{
  "variants": {
    "FIRE": {
      "enabled": true,
      "displayName": "Fire",
      "health": 300.0,
      "abilities": {
        "fireball": {
          "enabled": true,
          "cooldown": 100,
          "damage": 10.0,
          "radius": 10.0
        }
      }
    }
  }
}
```

## 📦 Installation

### For Players
1. **Install Fabric Loader** for Minecraft 1.21.1
2. **Download Fabric API** from [Modrinth](https://modrinth.com/mod/fabric-api) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. **Download this mod** (customenderdragon-2.0.0.jar)
4. **Place both JARs** in your `.minecraft/mods` folder
5. **Launch Minecraft** with the Fabric profile

### For Servers
1. **Install Fabric Loader** for your server
2. **Place** both Fabric API and this mod in the `mods` folder
3. **Start** the server
4. **Configure** settings in `config/customenderdragon.json`
5. **Restart** the server to apply changes

## 🔨 Building from Source

See [BUILD.md](BUILD.md) for detailed build instructions.

Quick build:
```bash
./gradlew clean build
```

Output: `build/libs/customenderdragon-2.0.0.jar`

## 📋 Requirements

- **Minecraft:** 1.21.1
- **Fabric Loader:** 0.16.0+
- **Fabric API:** 0.110.0+1.21.1
- **Java:** 21 or higher
- **Client Requirements:** Fabric Loader + Fabric API + This Mod

## 🎯 How It Works

1. **Dragon Spawns** - Either naturally in The End or via `/summon`
2. **Auto-Detection** - Mod detects the new EnderDragon entity
3. **Variant Selection** - Chooses a random variant based on configured weights
4. **Conversion** - Applies custom attributes, name, and abilities
5. **Visual Effects** - Starts particle effects and 3D decorations
6. **Combat** - Dragon uses abilities and changes phases as it takes damage
7. **Death** - Broadcasts message, drops custom loot, cleanup

## 📝 Configuration Example

Disable custom dragons entirely:
```json
{
  "general": {
    "enableCustomDragons": false
  }
}
```

Only spawn Fire and Ice dragons:
```json
{
  "variantWeights": {
    "fireWeight": 50,
    "iceWeight": 50,
    "lightningWeight": 0,
    "shadowWeight": 0,
    "voidWeight": 0
  }
}
```

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/Noctivag/Minecratf-Custom-EnderDragon/issues)
- **Discussions:** [GitHub Discussions](https://github.com/Noctivag/Minecratf-Custom-EnderDragon/discussions)

## 🌟 Credits

- **Author:** Noctivag
- **Built with:** Fabric API + Cloth Config
- **Fabric Version:** 1.21.1

---

**Made with ❤️ for the Minecraft community**

*Every dragon fight is now unique and epic!*
