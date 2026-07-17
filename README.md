# Advancement Guide

Menú de logros para Minecraft **26.2** (Fabric, **solo cliente**).

Vanilla solo te enseña los logros que ya tienes cerca y te esconde el resto tras
un `???`. Aquí ves **los 126**, los tengas o no, con su nombre, su icono, la
fecha en que lo conseguiste y **cómo conseguirlo**.

## Qué hace

- **Todos los logros, siempre.** El servidor solo manda al cliente los que te
  hace visibles, así que la lista completa va incluida en el mod, sacada de las
  definiciones del propio juego.
- **105 guías escritas a mano**, en **español e inglés**, explicando cómo se
  consigue cada logro de verdad (no la frase de una línea del juego).
- **Cómo conseguirlo de verdad.** En los logros de varios requisitos (los 55
  biomas, las 41 criaturas, las 40 comidas...) te lista **lo que te falta**, no
  una frase genérica.
- **Guías tuyas.** Puedes cambiar el texto de cualquier logro en
  `config/advancementguide-guias.json`, que manda sobre las del mod. El archivo
  se genera solo, con los nombres ya traducidos a tu idioma.
- **Español e inglés**, siguiendo el idioma del juego.
- **Buscador** y **filtro por categoría** (Minería, Inframundo, El Fin, Combate,
  Granja).
- **Tu cabeza en 3D**, girando hacia el ratón.
- **Dos aspectos**, que se cambian con **T** sin salir del menú:
  - **Códice** — piedra tallada, jade y obsidiana, con serpiente emplumada.
  - **LogPad** — el aparato original.

## Instalación

1. [Fabric Loader](https://fabricmc.net/use/) **0.19.3+** para Minecraft **26.2**
2. [Fabric API](https://modrinth.com/mod/fabric-api) **0.154.2+26.2**
3. Copia `advancementguide-1.0.0.jar` en `.minecraft/mods/`

Necesita **Java 25+** (el que ya usa 26.2).

> Es **solo cliente**: el servidor no necesita nada, y funciona en cualquier
> servidor. Eso sí, cada jugador que lo quiera tiene que instalarlo.

## Uso

| Tecla | Qué hace |
|---|---|
| **G** | Abre el menú (se puede cambiar en Opciones → Controles) |
| **T** | Cambia de aspecto (Códice ↔ LogPad) |

La rueda del ratón hace scroll en "cómo conseguirlo".

## Las guías

El mod trae **105 guías escritas a mano**, en español e inglés. Viven en los
archivos de idioma (`assets/advancementguide/lang/*.json`) con la clave
`advancementguide.guia.<id del logro con puntos>`, así que Minecraft elige solo
el idioma del jugador. Se pueden cambiar con un **resource pack**.

Los otros 21 logros son los de varios requisitos (los 55 biomas, las 41
criaturas...): esos no necesitan guía, porque el panel te lista **lo que te
falta de verdad**.

### Cambiarlas por jugador

Al abrir el menú por primera vez se crea `config/advancementguide-guias.json`:

```json
{
  "logros": [
    { "id": "minecraft:story/mine_stone", "logro": "La Edad de Piedra", "guia": "" }
  ]
}
```

Escribe en `guia` y eso será lo que salga en **CÓMO CONSEGUIRLO**, por encima de
la del mod. Usa `\n` para saltos de línea. **Se relee cada vez que abres el
menú**, así que no hace falta reiniciar. Los que dejes vacíos usan la del mod.

## Traducir a otro idioma

Copia `src/main/resources/assets/advancementguide/lang/en_us.json` a tu idioma
(por ejemplo `pt_br.json`) y traduce los valores, nunca las claves. Lo que no
traduzcas cae a inglés solo. Las pull requests son bienvenidas.

> **Ojo con las variantes regionales.** Minecraft no hace fallback dentro de un
> idioma: carga `en_us` y luego el código **exacto** que tengas elegido. Por eso
> `es_mx` (Español de México) **no** lee `es_es.json`, y quien lo tuviera veía
> todo en inglés. En el repo solo se mantiene `es_es.json`; `build.gradle` lo
> copia al compilar a `es_ar`, `es_cl`, `es_ec`, `es_mx`, `es_uy`, `es_ve` y
> `esan`. Si traduces una variante de verdad, tu archivo manda sobre la copia.

## Compilar

```bash
./gradlew build
```

El jar sale en `build/libs/`. Minecraft 26.2 no está ofuscado, así que no hacen
falta mappings.

## Créditos

Interfaz y texturas: **camaroncin**.

## Licencia

[MIT](LICENSE).

---

**English:** client-side advancement menu for Minecraft 26.2 (Fabric). Shows all
126 advancements — including the ones you don't have yet — with name, icon, date
and how to get them, listing the exact criteria you're missing. Includes a
search box, category filters, a 3D player head that follows your mouse, custom
per-advancement guides you can write yourself, and two skins (press **T**).
Press **G** to open.
