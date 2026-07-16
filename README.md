# Advancement Guide

Menú de logros para Minecraft **26.2** (Fabric, **solo cliente**).

Vanilla solo te enseña los logros que ya tienes cerca y te esconde el resto tras
un `???`. Aquí ves **los 126**, los tengas o no, con su nombre, su icono, la
fecha en que lo conseguiste y **cómo conseguirlo**.

## Qué hace

- **Todos los logros, siempre.** El servidor solo manda al cliente los que te
  hace visibles, así que la lista completa va incluida en el mod, sacada de las
  definiciones del propio juego.
- **Cómo conseguirlo de verdad.** En los logros de varios requisitos (los 55
  biomas, las 41 criaturas, las 40 comidas...) te lista **lo que te falta**, no
  una frase genérica.
- **Guías tuyas.** Puedes escribir tu propio texto para cada logro en
  `config/advancementguide-guias.json`. El archivo se genera solo, con los
  nombres ya traducidos a tu idioma.
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

## Escribir tus propias guías

Al abrir el menú por primera vez se crea `config/advancementguide-guias.json`:

```json
{
  "logros": [
    { "id": "minecraft:story/mine_stone", "logro": "Piedra mineral", "guia": "" }
  ]
}
```

Escribe en `guia` y eso será lo que salga en **CÓMO CONSEGUIRLO**. Usa `\n` para
saltos de línea. **Se relee cada vez que abres el menú**, así que no hace falta
reiniciar. Los que dejes vacíos usan el texto del juego.

De los 126 logros, **24 ya se explican solos** (los de varios requisitos). Los
otros 102 tienen un único requisito: ahí la descripción del juego ya *es* la
instrucción, así que el apartado no aparece hasta que le escribas tu guía.

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
