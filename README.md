# BigStacks

Paper-Plugin fuer **Minecraft 1.21.11** (Java 21).

- Abgebaute Bloecke landen direkt im Inventar (Auto-Pickup)
- Groessere Stacks (Standard 99, auch in Kisten, Faessern, Shulkerboxen, Trichtern)

> Minecraft erlaubt technisch maximal **99** pro Stack (Item-Komponente `max_stack_size`). 300 ist nicht moeglich.

## Bauen

```
mvn package
```

Die fertige Datei liegt in `target/BigStacks.jar`. Bei GitHub baut die Action sie automatisch (Reiter *Actions* -> Artifact).

## Installation

JAR in den `plugins`-Ordner des Paper-Servers legen und neu starten.

## Config

```yaml
max-stack-size: 99
auto-pickup: true
normalize-interval-ticks: 20
```

## Permission

`bigstacks.autopickup` (Standard: alle Spieler)
