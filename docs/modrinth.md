# Ficha para Modrinth (copiar y pegar)

## Datos del proyecto

| Campo | Valor |
|---|---|
| **Name** | Advancement Guide |
| **Slug** | `advancement-guide` |
| **Summary** | See every advancement — including the ones you're missing — with the exact criteria you still need |
| **Categories** | `Utility`, `Adventure` |
| **Client side** | **Required** |
| **Server side** | **Unsupported** |
| **License** | MIT |
| **Source code** | https://github.com/camaroncin2/AdvancementGuide |
| **Issue tracker** | https://github.com/camaroncin2/AdvancementGuide/issues |
| **Environment** | Client |
| **Loaders** | Fabric |
| **Game versions** | 26.2 |

> El **Summary** es lo único que se ve en los resultados de búsqueda: por eso
> dice qué problema resuelve y no cómo se llama.

---

## Description (cuerpo de la página)

Vanilla hides advancements you haven't unlocked behind a `???`, and when it does
show one, it only gives you a one-line description. **Advancement Guide** shows
you all **126** — the ones you have and the ones you don't — with the name, the
icon, the date you got it, and **how to get it**.

### Features

- **Every advancement, always.** Servers only send the client the advancements
  they make visible to you, so the full list ships with the mod, taken from the
  game's own definitions.
- **Real "how to get it".** For multi-criteria advancements (the 55 biomes, the
  41 mobs, the 40 foods…) it lists **exactly what you're still missing** —
  not a generic line.
- **Write your own guides.** Add your own text for any advancement in
  `config/advancementguide-guias.json`. The file is generated for you, with
  every advancement already named in your language, and it's re-read every time
  you open the menu — no restart needed.
- **Search box** and **category filters** (Mining, Nether, The End, Combat,
  Farming).
- **Your head in 3D**, following your mouse.
- **Two skins**, switch with **T** without leaving the menu:
  - **Códice** — carved stone, jade and obsidian, with a feathered serpent.
  - **LogPad** — the original device.

### How to use

| Key | Action |
|---|---|
| **G** | Open the menu (rebindable in Options → Controls) |
| **T** | Switch skin |

Mouse wheel scrolls the "how to get it" panel.

### Client side only

The server needs nothing and it works on any server, vanilla or modded. Each
player who wants it installs it themselves.

### Requires

- Fabric Loader 0.19.3+
- Fabric API
- Java 25+ (the one 26.2 already uses)

---

## Antes de publicar

1. **Icono**: usa `src/main/resources/assets/advancementguide/icon.png`.
2. **Gallery**: sube tus capturas del juego. Van MUCHO más que cualquier texto;
   pon primero la del Códice completo.
3. **Version number**: `1.0.0` · **Channel**: Release · **Loader**: Fabric ·
   **Game version**: 26.2
4. **Featured**: marca la primera versión.
