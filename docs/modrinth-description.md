<!-- ══════════════════════════════════════════════════════════════════════
     COPIA TODO LO DE ABAJO Y PEGALO EN Modrinth -> Settings -> Description
     Busca "PEGA-AQUI" para encontrar los huecos de las imagenes.
     Sube antes las capturas a Gallery: al subirlas te dan una URL, y esa
     es la que pegas.
     ══════════════════════════════════════════════════════════════════════ -->

<!-- ──────── IMAGEN 1: el Códice entero, con un logro seleccionado ──────── -->
![Advancement Guide](PEGA-AQUI-LA-URL-DE-LA-IMAGEN)

## Minecraft hides half its advancements from you

Open the vanilla screen and there they are: rows of `???`. No name, no icon, no
idea what they even are — let alone how to get them. And the ones you *can* see
only give you a single line of text.

**Advancement Guide shows you all 126.** The ones you have and the ones you
don't, each with its name, its icon, the date you unlocked it, and a real
explanation of how to get it.

Press **G**. That's it.

---

## Everything, always — no more `???`

Servers only send your client the advancements they decide to show you, so the
complete list ships inside the mod, read straight from the game's own
definitions. Nothing is hidden: name, icon and date for every single one.

<!-- ──────── IMAGEN 2: la lista con logros conseguidos y pendientes ──────── -->
![Every advancement](PEGA-AQUI-LA-URL-DE-LA-IMAGEN)

## 105 guides, written by hand

Not auto-generated filler. Someone sat down and wrote how each advancement is
*actually* done — the trick, the biome, the enchantment, the height to mine at.

> **Cover Me With Diamonds** — *Find diamond ore in the deep layers of the world;
> Y -59 is one of the best heights. Use an iron pickaxe or better to mine it.
> With Fortune III you get more diamonds.*

Available in **English and Spanish**, following your game's language.

## It tells you exactly what you're missing

For the big collection advancements — the 55 biomes, the 41 mobs, the 40 foods —
a guide would be useless. So instead the panel lists **the ones you still need**,
pulled from your real progress:

> **Adventuring Time** — *You're missing 51 of 55: Snowy slopes, Jagged peaks,
> Bamboo jungle, Cherry grove, Deep dark…*

<!-- ──────── IMAGEN 3: un logro de varios requisitos, con la lista ──────── -->
![What you're missing](PEGA-AQUI-LA-URL-DE-LA-IMAGEN)

## Find it fast

A **search box** and **category filters** — Mining, Nether, The End, Combat,
Farming — so you are not paging through 126 entries looking for one.

## Two skins, one key

Press **T** to switch without leaving the menu. Your choice is remembered.

| | |
|---|---|
| **Códice** | Carved stone, jade and obsidian, watched over by a feathered serpent. |
| **LogPad** | The original device. Red, clean, functional. |

<!-- ──────── IMAGEN 4: los dos aspectos, uno al lado del otro ──────── -->
![Two skins](PEGA-AQUI-LA-URL-DE-LA-IMAGEN)

## Your head, in 3D

Your own skin, rendered in 3D in the sidebar, following your mouse.

## Make the guides yours

Every guide can be rewritten. Open the menu once and you get
`config/advancementguide-guias.json`, with all 126 advancements **already named
in your language** so you can find them:

```json
{ "id": "minecraft:story/mine_stone", "logro": "Stone Age", "guia": "" }
```

Write your text, press **G**, and it's there — **the file is re-read every time
you open the menu**, no restart needed. Perfect for servers with custom rules or
private jokes.

---

## How to use

| Key | Action |
|---|---|
| **G** | Open the menu — rebindable in Options → Controls |
| **T** | Switch skin |

The mouse wheel scrolls the *how to get it* panel.

## Client side only

**The server needs nothing.** It works on any server — vanilla, modded, public,
whatever — because everything happens on your side. Each player who wants it
just installs it.

## Requires

- **Fabric Loader** 0.19.3+
- **Fabric API**
- **Java 25+** (the one 26.2 already uses)

---

# En español

Minecraft te esconde los logros que aún no tienes detrás de un `???`, y de los
que sí ves solo te da una línea de texto.

**Advancement Guide te enseña los 126**: los que tienes y los que no, con su
nombre, su icono, la fecha y **cómo se consigue de verdad**.

- **105 guías escritas a mano**, en español e inglés, explicando el truco real de
  cada logro.
- **Te dice lo que te falta.** En los de varios requisitos (los 55 biomas, las 41
  criaturas, las 40 comidas) te lista **cuáles te faltan**, no una frase genérica.
- **Buscador** y **filtro por categoría**: Minería, Inframundo, El Fin, Combate,
  Granja.
- **Tu cabeza en 3D**, siguiendo al ratón.
- **Dos aspectos** que se cambian con **T**: el **Códice** (piedra, jade y
  obsidiana) y el **LogPad** original.
- **Puedes reescribir cualquier guía** en `config/advancementguide-guias.json`,
  que se relee cada vez que abres el menú.

**Solo cliente**: el servidor no necesita nada y funciona en cualquiera. Abre con
**G**.
