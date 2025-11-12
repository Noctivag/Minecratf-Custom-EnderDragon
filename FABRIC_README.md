# Custom EnderDragon Mod for Fabric 1.21.1

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.java.com/)
[![Fabric](https://img.shields.io/badge/Fabric-0.16.9-blue.svg)](https://fabricmc.net/)

## 🐲 Overview

A Fabric mod that adds custom EnderDragon variants with unique abilities, phases, and epic battles. **Fully compatible with Fabric 1.21.1!**

## ✨ Version Information

- **Minecraft Version**: 1.21.1
- **Fabric Loader**: 0.16.9+
- **Fabric API**: 0.107.0+1.21.1
- **Java Version**: 21
- **Mod Version**: 2.0.0

## 🔧 Compatibility Status

✅ **FULLY COMPATIBLE WITH FABRIC 1.21.1**

All dependencies, APIs, and code have been updated to work seamlessly with Minecraft 1.21.1 on Fabric.

### Recent Updates (Fabric 1.21.1 Compatibility)

#### 1. Updated Gradle Properties
- Minecraft: `1.20.4` → `1.21.1`
- Yarn Mappings: `1.20.4+build.3` → `1.21.1+build.3`
- Fabric Loader: `0.15.3` → `0.16.9`
- Fabric API: `0.96.4+1.20.4` → `0.107.0+1.21.1`

#### 2. Updated Build Configuration
- Fabric Loom: `1.5-SNAPSHOT` → `1.8-SNAPSHOT`
- Java: `17` → `21`
- Cloth Config: `11.1.106` → `15.0.140`

#### 3. Updated Mod Metadata (`fabric.mod.json`)
- Minecraft requirement: `~1.20.4` → `~1.21.1`
- Java requirement: `>=17` → `>=21`
- Fabric Loader requirement: `>=0.15.0` → `>=0.16.0`
- Cloth Config suggestion: `>=11.0.0` → `>=15.0.0`

#### 4. Updated Mixin Compatibility
- Compatibility Level: `JAVA_17` → `JAVA_21`

#### 5. Fixed Code for 1.21.1 APIs
- Updated damage sources API: `getDamageSources().mob()` → `getDamageSources().mobAttack()`
- Updated explosion API to use `ServerWorld.ExplosionSourceType.MOB`
- Cleaned up all Bukkit/Spigot references
- Fixed corrupted code files
- All imports now properly reference Fabric APIs

## 🎮 Features

### 🔥 Dragon Variants

- **Fire Dragon**: Fireball attacks, fire aura, meteor shower (Phase 3)
- **Ice Dragon**: Ice shards with slowness effect, freeze aura
- **Lightning Dragon**: Lightning strikes on players
- **Shadow Dragon**: Shadow strike with blindness effect
- **Void Dragon**: Void pulse with wither effect

### ⚔️ Phase System

Dragons have 3 phases based on health percentage:
- **Phase 1** (100-67% HP): Basic abilities
- **Phase 2** (66-34% HP): Increased difficulty
- **Phase 3** (33-0% HP): Ultimate abilities unlocked

### 💻 Commands

All commands require permission level 2 (OP by default):

- `/customdragon spawn <variant>` - Spawn a custom dragon
  - Variants: FIRE, ICE, LIGHTNING, SHADOW, VOID
- `/customdragon list` - List active custom dragons
- `/customdragon remove` - Remove all custom dragons
- `/customdragon reload` - Reload configuration

## 📦 Installation

1. **Install Java 21**
   - Download from [Adoptium](https://adoptium.net/) or your preferred source

2. **Install Fabric Loader 0.16.9+**
   - Download from [Fabric MC](https://fabricmc.net/use/)
   - Select Minecraft 1.21.1

3. **Install Fabric API 0.107.0+1.21.1**
   - Download from [Modrinth](https://modrinth.com/mod/fabric-api) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api)

4. **(Optional) Install Cloth Config 15.0.140+**
   - Download from [Modrinth](https://modrinth.com/mod/cloth-config) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
   - Required for in-game config editing

5. **Add the mod**
   - Place the mod JAR in your `.minecraft/mods` folder

## 🔨 Building from Source

```bash
# Clone the repository
git clone https://github.com/Noctivag/Minecratf-Custom-EnderDragon.git
cd Minecratf-Custom-EnderDragon

# Generate Gradle wrapper (if needed)
gradle wrapper

# Build the mod
./gradlew build

# The built JAR will be in build/libs/
```

## 🛠️ Development

This mod is built using:
- Fabric Loom for build system
- Yarn mappings for deobfuscation
- Brigadier for command handling
- Fabric API for hooks and utilities

## ⚠️ Known Issues

- Display entities for 3D decorations are currently stubbed out (work in progress)
- Config system needs implementation

## 📝 License

MIT License - See LICENSE file for details

## 👤 Author

**Noctivag**

- GitHub: [@Noctivag](https://github.com/Noctivag)

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## 📜 Changelog

### Version 2.0.0 - Fabric 1.21.1 Compatibility Update
- ✅ Updated all dependencies to Fabric 1.21.1
- ✅ Migrated from Java 17 to Java 21
- ✅ Fixed all API compatibility issues
- ✅ Cleaned up corrupted code files
- ✅ Removed all Bukkit/Spigot references
- ✅ Updated damage and explosion APIs
- ✅ Verified build compatibility

---

**Enjoy epic dragon battles on Fabric 1.21.1!** 🐉

## Features

- **5 Dragon-Varianten**: Fire, Ice, Lightning, Shadow, Void
- **3 Kampfphasen**: Basierend auf Gesundheit
- **Einzigartige Fähigkeiten**: Jede Variante hat spezielle Angriffe
- **Partikeleffekte**: Visuelle Effekte für jede Variante
- **JSON-Konfiguration**: Einfach anzupassende Einstellungen
- **Commands**: `/customdragon` Befehl mit Brigadier

## Installation

1. Installiere Fabric Loader (0.15.0 oder höher)
2. Installiere Fabric API für Minecraft 1.20.4
3. Platziere die Mod-JAR im `mods` Ordner
4. Starte den Server/Client

## Commands

- `/customdragon spawn <FIRE|ICE|LIGHTNING|SHADOW|VOID>` - Spawnt einen benutzerdefinierten Drachen
- `/customdragon list` - Zeigt alle aktiven Drachen
- `/customdragon remove` - Entfernt alle benutzerdefinierten Drachen  
- `/customdragon reload` - Lädt die Konfiguration neu

## Konfiguration

Die Konfiguration befindet sich in `config/customenderdragon.json` und wird beim ersten Start automatisch erstellt.

## Build

```bash
./gradlew build
```

Die fertige Mod-JAR befindet sich in `build/libs/`.

## Anforderungen

- Minecraft 1.20.4
- Fabric Loader 0.15.0+
- Fabric API
- Java 17+

## Unterschiede zur Bukkit-Version

- Verwendet Brigadier für Commands anstelle von Bukkit's CommandExecutor
- Verwendet Fabric Events anstelle von Bukkit Events
- JSON-Konfiguration anstelle von YAML
- Native Minecraft-Entitäten anstelle von Bukkit-API

## TODO

- Display Entity Decorations (erfordert Minecraft 1.19.4+)
- Erweiterte Crystal Arena Strukturen
- Custom Loot Tables
- Boss Bar Integration

## Lizenz

MIT License
