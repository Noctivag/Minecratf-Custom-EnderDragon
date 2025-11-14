# Build-Anleitung

## Produktions-Build

### Voraussetzungen
- Java 21 oder höher
- Gradle 8.10 (über Wrapper enthalten)

### Build-Befehle

#### Standard-Build
```bash
./gradlew clean build
```

Dieser Befehl erstellt eine produktionsreife JAR-Datei:
- **Ausgabe**: `build/libs/customenderdragon-2.0.0.jar`
- **Größe**: ~1.1 MB (inkl. eingebettete Abhängigkeiten)
- **Enthält**: Kompilierte .class-Dateien, Cloth Config, Ressourcen

#### Nur JAR erstellen (ohne Tests)
```bash
./gradlew clean remapJar
```

### Build-Artefakte

Nach einem erfolgreichen Build finden Sie folgende Dateien:

- **`build/libs/customenderdragon-2.0.0.jar`** - Produktions-JAR für Minecraft
  - Enthält alle kompilierten Klassen
  - Inkludiert Cloth Config Bibliothek
  - Remapped auf intermediary mappings
  - Bereit für die Installation in `.minecraft/mods/`

- **`build/devlibs/customenderdragon-2.0.0.jar`** - Entwicklungs-JAR
  - Nur für interne Verwendung
  - Nicht remapped

### Projekt-Details

- **Mod-Version**: 2.0.0
- **Minecraft-Version**: 1.21.1
- **Fabric Loader**: 0.16.9+
- **Fabric API**: 0.110.0+1.21.1
- **Java-Version**: 21
- **Lizenz**: MIT

### JAR-Verifizierung

Um den Inhalt der JAR-Datei zu überprüfen:

```bash
# Liste aller Dateien
unzip -l build/libs/customenderdragon-2.0.0.jar

# Manifest anzeigen
unzip -p build/libs/customenderdragon-2.0.0.jar META-INF/MANIFEST.MF

# Anzahl der kompilierten Klassen
unzip -l build/libs/customenderdragon-2.0.0.jar | grep -c "\.class$"
```

### Build-Features

✅ **Reproduzierbare Builds**: Timestamps normalisiert auf 1980-01-01  
✅ **Optimierte JAR-Kompression**: Kleinere Dateigrößen  
✅ **Detailliertes Manifest**: Build-Informationen und Metadaten  
✅ **Eingebettete Abhängigkeiten**: Cloth Config automatisch inkludiert  
✅ **Remapping**: Kompatibel mit allen Minecraft-Obfuscation-Typen  

### Troubleshooting

#### Problem: `./gradlew: No such file or directory`
**Lösung**: Gradle Wrapper fehlt. Führen Sie aus:
```bash
gradle wrapper --gradle-version 8.10
```

#### Problem: Java-Version zu alt
**Lösung**: Stellen Sie sicher, dass Java 21+ installiert ist:
```bash
java -version
# Sollte "21" oder höher zeigen
```

#### Problem: Build schlägt mit Dependency-Fehler fehl
**Lösung**: Löschen Sie den Cache und bauen neu:
```bash
./gradlew clean --refresh-dependencies
./gradlew build
```

### Deployment

1. Lokales Testen:
   ```bash
   cp build/libs/customenderdragon-2.0.0.jar ~/.minecraft/mods/
   ```

2. Server-Deployment:
   ```bash
   cp build/libs/customenderdragon-2.0.0.jar /path/to/server/mods/
   ```

3. Release vorbereiten:
   - JAR-Datei aus `build/libs/` verwenden
   - Mod-Beschreibung aus `fabric.mod.json` prüfen
   - LICENSE-Datei mit einpacken
