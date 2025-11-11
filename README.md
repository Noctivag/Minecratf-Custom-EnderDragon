# Custom EnderDragon Plugin - Next Level Edition

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.4-brightgreen.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.java.com/)
[![Paper](https://img.shields.io/badge/Paper-API-blue.svg)](https://papermc.io/)

## 🐲 Overview

Take your Minecraft dragon fights to the **next level** with this advanced Custom EnderDragon plugin! Featuring **5 unique dragon variants**, each with their own abilities, particle effects, and combat phases. This plugin transforms the standard EnderDragon fight into an epic, customizable boss battle experience.

## ✨ Features

### 🔥 5 Unique Dragon Variants

- **FIRE Dragon** - Master of flames and meteors
- **ICE Dragon** - Controller of frost and blizzards
- **LIGHTNING Dragon** - Wielder of thunder and storm
- **SHADOW Dragon** - Manipulator of darkness and teleportation
- **VOID Dragon** - Reality warper with devastating power

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
| `/customdragon spawn <variant>` | Spawn a custom dragon | `customdragon.spawn` |
| `/customdragon list` | List all active dragons | `customdragon.use` |
| `/customdragon remove` | Remove all custom dragons | `customdragon.remove` |
| `/customdragon reload` | Reload configuration | `customdragon.reload` |

**Aliases:** `/cdragon`, `/cdr`

## 🔑 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `customdragon.*` | All permissions | op |
| `customdragon.use` | Use base command | op |
| `customdragon.spawn` | Spawn dragons | op |
| `customdragon.spawn.fire` | Spawn Fire dragons | op |
| `customdragon.spawn.ice` | Spawn Ice dragons | op |
| `customdragon.spawn.lightning` | Spawn Lightning dragons | op |
| `customdragon.spawn.shadow` | Spawn Shadow dragons | op |
| `customdragon.spawn.void` | Spawn Void dragons | op |
| `customdragon.remove` | Remove dragons | op |
| `customdragon.reload` | Reload config | op |

## ⚙️ Configuration

The plugin is **fully configurable** through `config.yml`:

- **Dragon Stats** - Health, damage, speed per variant
- **Ability Settings** - Cooldowns, damage, effects for each ability
- **Particle Effects** - Type, count, and offset for visual effects
- **Phase System** - Health thresholds and phase settings
- **Loot Tables** - Complete control over drops and chances
- **Messages** - Customize all plugin messages
- **Boss Bars** - Configure colors and update rates

### Example Configuration Snippet

```yaml
variants:
  FIRE:
    enabled: true
    display-name: "&c&lFire"
    health: 300.0
    damage-multiplier: 1.5
    abilities:
      fireball:
        enabled: true
        cooldown: 60
        damage: 12.0
        explosion-power: 3.0
```

See `config.yml` for full configuration options.

## 📦 Installation

1. **Download** the latest release JAR file
2. **Place** it in your server's `plugins` folder
3. **Restart** your server
4. **Configure** settings in `plugins/CustomEnderDragon/config.yml` (optional)
5. **Reload** with `/customdragon reload`

## 🔨 Building from Source

```bash
# Clone the repository
git clone https://github.com/Noctivag/Minecratf-Custom-EnderDragon.git
cd Minecratf-Custom-EnderDragon

# Build with Maven
mvn clean package

# Find the JAR in target/CustomEnderDragon-2.0.0.jar
```

## 📋 Requirements

- **Minecraft Version:** 1.20.4+
- **Server Software:** Paper, Purpur, or compatible fork
- **Java Version:** 17 or higher

## 🎯 Usage Examples

### Spawn a Fire Dragon
```
/customdragon spawn FIRE
```

### List Active Dragons
```
/customdragon list
```

### Remove All Dragons
```
/customdragon remove
```

## 🎨 Design Philosophy

This plugin was built with these principles:

- **Epic Boss Fights** - Create memorable, challenging encounters
- **Visual Spectacle** - Stunning particle effects and animations
- **Full Customization** - Server owners control everything
- **Performance** - Optimized for smooth gameplay
- **Vanilla-Friendly** - Works with existing game mechanics

## 🐛 Known Issues

- Shadow Dragon clone ability is reserved for future implementation
- Boss bars may occasionally desync with dragon health (resets on next update)

## 🔮 Planned Features

- [ ] Custom dragon models/textures support
- [ ] Arena system with automatic spawning
- [ ] Dragon taming mechanics
- [ ] Rewards GUI
- [ ] Dragon egg hatching system
- [ ] World boss events

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/Noctivag/Minecratf-Custom-EnderDragon/issues)
- **Discussions:** [GitHub Discussions](https://github.com/Noctivag/Minecratf-Custom-EnderDragon/discussions)

## 🌟 Credits

- **Author:** Noctivag
- **Built with:** Paper API
- **Special Thanks:** Minecraft community for inspiration

---

**Made with ❤️ for the Minecraft community**

*Ready to fight some epic dragons? Download now and unleash chaos!*
