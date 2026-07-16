package com.camaroncin.advancementguide;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Locale;

/**
 * Tema CODICE: el codice prehispanico, montado con las piezas dibujadas a mano
 * (marco de piedra, tablero, tarjetas y flechas), cada una en su propio PNG.
 *
 * ---- Por que el tablero va con 9 cortes ----
 * El hueco del marco mide 359x208 pero el tablero se dibujo a 203x119. Si se
 * estirase entero, la barra superior (15 px) creceria a 26 y las flechas (14 px)
 * se verian ridiculas dentro. Con los 9 cortes, los bordes y la barra se quedan
 * a 1:1 y solo se estiran los rellenos, que son de color plano: asi todo encaja
 * como se dibujo (flecha de 14 en barra de 15, icono de 16 en ranura de 16).
 */
public class CodiceScreen extends PantallaLogros {

    private static Identifier tex(String nombre) {
        return Identifier.fromNamespaceAndPath(AdvancementGuideClient.MOD_ID, "textures/gui/codice/" + nombre);
    }

    private static final Identifier MARCO = tex("marco.png");
    private static final Identifier TABLERO = tex("tablero.png");
    private static final Identifier CARD_OK = tex("card_normal.png");
    private static final Identifier CARD_SEL = tex("card_sel.png");
    private static final Identifier CARD_NO = tex("card_no.png");
    private static final Identifier FLECHA_IZQ = tex("flecha_izq.png");
    private static final Identifier FLECHA_DER = tex("flecha_der.png");
    private static final Identifier BUSCADOR = tex("buscador.png");
    private static final Identifier CAT_SLOT = tex("cat_slot.png");
    /** Tira de 96x16 con los 6 iconos propios, en el orden de CAT_ID. */
    private static final Identifier CAT_ICONOS = tex("cat_iconos.png");

    // ---- Marco ----
    public static final int ANCHO = 569;
    public static final int ALTO = 320;
    /** Hueco transparente del marco, donde entra el tablero. */
    private static final int VX = 144, VY = 63, VW = 359, VH = 208;

    // ---- Tablero (background.png) y sus cortes ----
    private static final int TAB_W = 203, TAB_H = 119;
    private static final int BORDE = 3;    // laterales, se quedan a 1:1
    private static final int TOP_H = 19;   // borde + barra + linea, se queda a 1:1
    private static final int BOT_H = 3;    // borde inferior, a 1:1

    // ---- Zonas resultantes (relativas al marco) ----
    private static final int BARRA_Y = VY + 3;              // 66..80 (interior de la barra)
    private static final int CONT_Y = VY + TOP_H;           // 82
    private static final int CONT_H = VH - TOP_H - BOT_H;   // 186 -> 82..267
    private static final int LISTA_X = VX + BORDE;          // 147
    private static final int LISTA_W = 200;                 // 147..346 (el separador cae en ~348)
    private static final int DET_X = 351, DET_W = 147;      // 351..497

    // ---- Barra superior ----
    // Todo se alinea con las columnas de abajo: flechas y buscador sobre la
    // LISTA (que es lo que filtran) sin pasar del separador, y el contador
    // centrado sobre la columna de DETALLE.
    private static final int NAV_W = 19, NAV_H = 14, NAV_Y = BARRA_Y + 1;
    private static final int PREV_X = LISTA_X + 3, NEXT_X = PREV_X + 22;
    private static final int MARCO_BUSC_X = 194, MARCO_BUSC_W = 152;   // 194..346
    private static final int BUSC_TEX_W = 40, BUSC_TEX_H = 15;         // textura del surco
    private static final int BUSC_CAP_I = 14, BUSC_CAP_D = 4;          // tapas que no se estiran
    // El texto empieza pasada la incrustacion de jade; el hueco del surco
    // ocupa las filas 3..11 de la textura.
    private static final int BUSC_X = MARCO_BUSC_X + 12, BUSC_Y = BARRA_Y + 3;
    private static final int BUSC_W = MARCO_BUSC_W - 12 - 6, BUSC_H = 9;
    private static final int PAG_CX = DET_X + DET_W / 2;               // 424

    // ---- Tarjetas ----
    // A 1,5x: la tarjeta pasa de 24 a 36 de alto y su ranura de 16 a 24, con el
    // icono escalado igual para que la siga llenando. Caben 4, como en la maqueta.
    private static final int POR_PAGINA = 4;
    private static final int CARD_W = 102, CARD_H = 24;     // tamano del PNG
    private static final float CARD_ESC = 1.5f;
    private static final int CARD_IZQ = 30, CARD_DER = 8;   // trozos que NO se estiran
    private static final int FILA_X = LISTA_X + 5;          // 152
    private static final int FILA_W = 190;                  // 152..341
    private static final int FILA_H = 36;
    private static final int FILA_Y = CONT_Y + 6;           // 88
    private static final int FILA_SEP = 44;                 // 88,132,176,220 -> acaba en 256

    // ---- Panel de detalle ----
    private static final int DET_PAD = 8;
    private static final int MAX_LIN_DESC = 3;      // con apartado de "como" debajo
    private static final int MAX_LIN_DESC_SOLA = 9; // sin el, la descripcion se queda con todo
    private static final int COMO_Y = 194, COMO_H = 61;     // 194..255
    private static final int SCROLL_X = 484, SCROLL_W = 6, THUMB_H = 14;

    // ---- Barra lateral (la obsidiana del marco: x 50..135, y 77..256) ----
    private static final int LAT_CX = 92;
    /** Hueco de la cabeza 3D. Escala = pixeles por bloque: con 40 la cabeza sale
     *  de ~21 px y al girar llega a ~27, o sea que en 34 le sobra sitio. */
    private static final int CARA_CY = 97, CARA_TAM = 34, CARA_ESCALA = 40;
    // Filas de categoria: ranura de 20 + nombre. Van de 130 a 255, justo lo que
    // deja la obsidiana (acaba en 256).
    private static final int CAT_X = 52, CAT_W = 81, CAT_H = 20, CAT_SEP = 21;
    private static final int CAT_Y0 = 130;
    private static final int SLOT = 20;          // lado de la ranura (hueco 16x16)
    private static final int SLOT_TEX_W = 40;    // normal en 0, elegida en 20
    private static final int ICO = 16;           // lado de cada icono de la tira
    private static final int ICO_TEX_W = 96;     // 6 iconos de 16

    // ---- Paleta (ARGB: el 0xFF de alfa es obligatorio) ----
    private static final int JADE = 0xFF487477;        // borde del tablero
    private static final int JADE_OSC = 0xFF102A30;    // hueco hundido
    private static final int JADE_CLARO = 0xFF7FD4C4;
    private static final int ORO = 0xFFFDCC5D;         // igual que el borde de card_sel
    private static final int PIEDRA_TXT = 0xFFCFE6E0;  // texto sobre obsidiana
    private static final int TXT_OK = 0xFF1E4620;      // nombre conseguido (sobre pergamino)
    private static final int TXT_NO = 0xFF453C33;      // nombre pendiente
    private static final int TXT_FECHA = 0xFF6B5B47;
    private static final int MORADO = 0xFFC98CE8;      // desafios (como en el juego)

    public CodiceScreen() {
        super("Codice de Logros");
    }

    // ==================================================================
    //  Geometria
    // ==================================================================

    @Override
    protected int ancho() {
        return ANCHO;
    }

    @Override
    protected int alto() {
        return ALTO;
    }

    @Override
    protected int porPagina() {
        return POR_PAGINA;
    }

    @Override
    protected Rect rectCategoria(int i) {
        return new Rect(x + CAT_X, y + CAT_Y0 + i * CAT_SEP, CAT_W, CAT_H);
    }

    @Override
    protected Rect rectPrev() {
        return new Rect(x + PREV_X, y + NAV_Y, NAV_W, NAV_H);
    }

    @Override
    protected Rect rectNext() {
        return new Rect(x + NEXT_X, y + NAV_Y, NAV_W, NAV_H);
    }

    @Override
    protected Rect rectFila(int i) {
        return new Rect(x + FILA_X, y + FILA_Y + i * FILA_SEP, FILA_W, FILA_H);
    }

    @Override
    protected Rect rectBuscador() {
        return new Rect(x + BUSC_X, y + BUSC_Y, BUSC_W, BUSC_H);
    }

    @Override
    protected Rect rectComo() {
        return new Rect(x + DET_X, y + COMO_Y - 14, DET_W, COMO_H + 14);
    }

    @Override
    protected int colorBuscador() {
        return PIEDRA_TXT;
    }

    // ==================================================================
    //  Dibujo
    // ==================================================================

    @Override
    protected void dibujar(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        // 1) Tablero primero y marco encima: asi el borde de piedra tapa
        //    cualquier pixel que se salga del hueco.
        tablero(g);
        g.blit(RenderPipelines.GUI_TEXTURED, MARCO, x, y, 0f, 0f, ANCHO, ALTO, ANCHO, ALTO);

        // 2) Titulo sobre la placa de jade (x 228..411, y 32..52)
        g.centeredText(font, Component.literal("CODICE DE LOGROS"), x + 320, y + 38, 0xFF0E2A26);

        // 3) Barra lateral: cara, contador y categorias
        barraLateral(g, mouseX, mouseY);

        // 4) Barra superior: flechas, buscador y pagina
        barraSuperior(g);

        if (lista.isEmpty()) {
            g.centeredText(font, Component.literal("Sin resultados"), x + 247, y + 170, PIEDRA_TXT);
            return;
        }

        // 5) Tarjetas
        int desde = (pagina - 1) * POR_PAGINA;
        for (int i = 0; i < POR_PAGINA; i++) {
            int idx = desde + i;
            if (idx >= lista.size()) {
                break;
            }
            fila(g, lista.get(idx), x + FILA_X, y + FILA_Y + i * FILA_SEP, idx == seleccion);
        }

        // 6) Detalle
        dibujarDetalle(g, lista.get(Math.min(seleccion, lista.size() - 1)));
    }

    /** El tablero, con los 9 cortes, dentro del hueco del marco. */
    private void tablero(GuiGraphicsExtractor g) {
        int vx = x + VX, vy = y + VY;
        int medioW = VW - BORDE * 2;             // 353 (destino)
        int srcW = TAB_W - BORDE * 2;            // 197 (origen)
        int medioH = VH - TOP_H - BOT_H;         // 186
        int srcH = TAB_H - TOP_H - BOT_H;        // 97
        int derX = vx + BORDE + medioW;
        int srcDerX = TAB_W - BORDE;

        // Franja superior (borde + barra + linea): alto 1:1, solo se estira a lo ancho.
        // Como ahi dentro solo hay lineas horizontales, estirar no las deforma.
        pieza(g, vx, vy, BORDE, TOP_H, 0, 0, BORDE, TOP_H);
        pieza(g, vx + BORDE, vy, medioW, TOP_H, BORDE, 0, srcW, TOP_H);
        pieza(g, derX, vy, BORDE, TOP_H, srcDerX, 0, BORDE, TOP_H);

        // Franja central (relleno plano + el separador vertical)
        int my = vy + TOP_H;
        pieza(g, vx, my, BORDE, medioH, 0, TOP_H, BORDE, srcH);
        pieza(g, vx + BORDE, my, medioW, medioH, BORDE, TOP_H, srcW, srcH);
        pieza(g, derX, my, BORDE, medioH, srcDerX, TOP_H, BORDE, srcH);

        // Franja inferior
        int by = vy + VH - BOT_H;
        int srcBy = TAB_H - BOT_H;
        pieza(g, vx, by, BORDE, BOT_H, 0, srcBy, BORDE, BOT_H);
        pieza(g, vx + BORDE, by, medioW, BOT_H, BORDE, srcBy, srcW, BOT_H);
        pieza(g, derX, by, BORDE, BOT_H, srcDerX, srcBy, BORDE, BOT_H);
    }

    /** Blit del tablero con origen y destino de distinto tamano. */
    private void pieza(GuiGraphicsExtractor g, int dx, int dy, int dw, int dh,
                       int su, int sv, int sw, int sh) {
        g.blit(RenderPipelines.GUI_TEXTURED, TABLERO, dx, dy, (float) su, (float) sv,
                dw, dh, sw, sh, TAB_W, TAB_H);
    }

    private void barraLateral(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        caraEn3D(g, x + LAT_CX, y + CARA_CY, CARA_TAM, CARA_ESCALA, mouseX, mouseY);

        int conseguidos = Logros.contarConseguidos(todos);
        g.centeredText(font, Component.literal(conseguidos + "/" + todos.size()),
                x + LAT_CX, y + 118, ORO);

        // Cada categoria: ranura tallada con el icono del juego dentro + nombre.
        for (int i = 0; i < CAT_ID.length; i++) {
            boolean activa = categoria.equals(CAT_ID[i]);
            int cx = x + CAT_X, cy = y + CAT_Y0 + i * CAT_SEP;

            if (activa) {
                g.fill(cx, cy, cx + CAT_W, cy + CAT_H, 0x552E6B66);
            }
            // La ranura elegida esta en la mitad derecha de la textura.
            g.blit(RenderPipelines.GUI_TEXTURED, CAT_SLOT, cx, cy,
                    activa ? (float) SLOT : 0f, 0f, SLOT, SLOT, SLOT, SLOT, SLOT_TEX_W, SLOT);
            // El hueco mide 16x16 y empieza en (2,2): el icono entra clavado.
            g.blit(RenderPipelines.GUI_TEXTURED, CAT_ICONOS, cx + 2, cy + 2,
                    (float) (i * ICO), 0f, ICO, ICO, ICO_TEX_W, ICO);

            g.text(font, Component.literal(CAT_NOM[i].toUpperCase(Locale.ROOT)),
                    cx + SLOT + 3, cy + 6, activa ? 0xFFFFFFFF : PIEDRA_TXT, false);
        }
    }

    private void barraSuperior(GuiGraphicsExtractor g) {
        // Flechas 1:1 (19x14), tal cual se dibujaron
        g.blit(RenderPipelines.GUI_TEXTURED, FLECHA_IZQ, x + PREV_X, y + NAV_Y,
                0f, 0f, NAV_W, NAV_H, NAV_W, NAV_H);
        g.blit(RenderPipelines.GUI_TEXTURED, FLECHA_DER, x + NEXT_X, y + NAV_Y,
                0f, 0f, NAV_W, NAV_H, NAV_W, NAV_H);

        // Surco del buscador, en 3 trozos: las tapas (con la incrustacion de
        // jade) a 1:1 y solo se estira el medio, que es liso.
        int bx = x + MARCO_BUSC_X, by = y + BARRA_Y;
        int medio = MARCO_BUSC_W - BUSC_CAP_I - BUSC_CAP_D;
        int srcMedio = BUSC_TEX_W - BUSC_CAP_I - BUSC_CAP_D;
        g.blit(RenderPipelines.GUI_TEXTURED, BUSCADOR, bx, by,
                0f, 0f, BUSC_CAP_I, BUSC_TEX_H, BUSC_CAP_I, BUSC_TEX_H, BUSC_TEX_W, BUSC_TEX_H);
        g.blit(RenderPipelines.GUI_TEXTURED, BUSCADOR, bx + BUSC_CAP_I, by,
                (float) BUSC_CAP_I, 0f, medio, BUSC_TEX_H, srcMedio, BUSC_TEX_H, BUSC_TEX_W, BUSC_TEX_H);
        g.blit(RenderPipelines.GUI_TEXTURED, BUSCADOR, bx + BUSC_CAP_I + medio, by,
                (float) (BUSC_TEX_W - BUSC_CAP_D), 0f, BUSC_CAP_D, BUSC_TEX_H, BUSC_CAP_D, BUSC_TEX_H,
                BUSC_TEX_W, BUSC_TEX_H);

        g.centeredText(font, Component.literal(pagina + "/" + paginas), x + PAG_CX, y + 70, ORO);
    }

    /** Una tarjeta: se corta en tres para poder ensancharla sin deformar ni la
     *  ranura del icono ni los bordes (solo se estira el pergamino, que es liso).
     *  Los extremos van a CARD_ESC, asi que la ranura sigue siendo cuadrada. */
    private void fila(GuiGraphicsExtractor g, Logros.Entrada e, int fx, int fy, boolean sel) {
        Identifier tex = sel ? CARD_SEL : (e.conseguido() ? CARD_OK : CARD_NO);
        int izq = Math.round(CARD_IZQ * CARD_ESC);         // 45
        int der = Math.round(CARD_DER * CARD_ESC);         // 12
        int medio = FILA_W - izq - der;                    // 133 (se estira)
        int srcMedio = CARD_W - CARD_IZQ - CARD_DER;       // 64

        g.blit(RenderPipelines.GUI_TEXTURED, tex, fx, fy,
                0f, 0f, izq, FILA_H, CARD_IZQ, CARD_H, CARD_W, CARD_H);
        g.blit(RenderPipelines.GUI_TEXTURED, tex, fx + izq, fy,
                (float) CARD_IZQ, 0f, medio, FILA_H, srcMedio, CARD_H, CARD_W, CARD_H);
        g.blit(RenderPipelines.GUI_TEXTURED, tex, fx + izq + medio, fy,
                (float) (CARD_W - CARD_DER), 0f, der, FILA_H, CARD_DER, CARD_H, CARD_W, CARD_H);

        // El icono, a la misma escala que la tarjeta, para que siga llenando la
        // ranura (16x16 en (5,4) del PNG -> 24x24 aqui). Se puede escalar porque
        // g.item se queda con una copia de la matriz actual.
        g.pose().pushMatrix();
        g.pose().translate(fx + 5 * CARD_ESC, fy + 4 * CARD_ESC);
        g.pose().scale(CARD_ESC, CARD_ESC);
        g.item(e.icono(), 0, 0);
        g.pose().popMatrix();

        // El texto se queda a 1:1 aposta: agrandarlo obligaria a recortar casi
        // todos los nombres (hay hasta 29 letras y solo caben ~125 px).
        boolean hecho = e.conseguido();
        int tx = fx + izq + 4;
        FormattedCharSequence nombre = font.split(e.titulo(), FILA_W - izq - der - 8).get(0);
        g.text(font, nombre, tx, fy + 9, hecho ? TXT_OK : TXT_NO, false);
        String fecha = hecho ? "Obtenido: " + fechaDe(e.progress()) : "--/--/----";
        g.text(font, Component.literal(fecha), tx, fy + 21, TXT_FECHA, false);
    }

    private void dibujarDetalle(GuiGraphicsExtractor g, Logros.Entrada e) {
        int centro = x + DET_X + DET_W / 2;
        int dx = x + DET_X + DET_PAD;
        int anchoTxt = DET_W - DET_PAD * 2;
        boolean hecho = e.conseguido();

        // Icono con su marco: se reaprovecha la ranura de la tarjeta (18x18 en (4,3))
        g.blit(RenderPipelines.GUI_TEXTURED, hecho ? CARD_OK : CARD_NO,
                centro - 9, y + 88, 4f, 3f, 18, 18, 18, 18, CARD_W, FILA_H);
        g.item(e.icono(), centro - 8, y + 89);

        FormattedCharSequence tit = font.split(e.titulo(), anchoTxt).get(0);
        g.text(font, tit, centro - font.width(tit) / 2, y + 110, hecho ? ORO : JADE_CLARO, false);
        g.centeredText(font, Component.literal(e.tipo().nombre().toUpperCase()),
                centro, y + 122, colorTipo(e.tipo()));

        // Si no hay nada que anadir abajo, la descripcion se queda con el panel.
        int anchoComo = (x + SCROLL_X) - dx - 4;
        List<FormattedCharSequence> lineas = lineasComo(e, anchoComo);
        boolean hayComo = !lineas.isEmpty();

        g.text(font, Component.literal("DESCRIPCION:"), dx, y + 138, JADE_CLARO, false);
        parrafo(g, e.descripcion(), dx, y + 149, anchoTxt,
                hayComo ? MAX_LIN_DESC : MAX_LIN_DESC_SOLA, 0xFFEAF2F0);

        if (!hayComo) {
            return;
        }

        // ---- COMO CONSEGUIRLO (con scroll) ----
        g.text(font, Component.literal("COMO CONSEGUIRLO:"), dx, y + 180, JADE_CLARO, false);

        int visibles = COMO_H / font.lineHeight;
        int maxScroll = Math.max(0, lineas.size() - visibles);
        scrollComo = Math.min(scrollComo, maxScroll);

        for (int i = 0; i < visibles && i + scrollComo < lineas.size(); i++) {
            g.text(font, lineas.get(i + scrollComo), dx, y + COMO_Y + i * font.lineHeight,
                    hecho ? 0xFF9BE3B4 : 0xFFEAF2F0, false);
        }

        // Carril y tirador (tampoco tienen textura propia)
        if (maxScroll > 0) {
            caja(g, x + SCROLL_X, y + COMO_Y, SCROLL_W, COMO_H, JADE_OSC, JADE);
            int recorrido = COMO_H - THUMB_H - 2;
            int ty = y + COMO_Y + 1 + Math.round(recorrido * (scrollComo / (float) maxScroll));
            g.fill(x + SCROLL_X + 1, ty, x + SCROLL_X + SCROLL_W - 1, ty + THUMB_H, ORO);
        }
    }

    private int colorTipo(Logros.Tipo tipo) {
        return switch (tipo) {
            case DESAFIO -> MORADO;
            case OBJETIVO -> ORO;
            default -> PIEDRA_TXT;
        };
    }

    /** Recuadro con borde de 1 px (g.fill trabaja con esquinas, no con ancho/alto). */
    private void caja(GuiGraphicsExtractor g, int cx, int cy, int cw, int ch, int relleno, int borde) {
        g.fill(cx, cy, cx + cw, cy + ch, borde);
        g.fill(cx + 1, cy + 1, cx + cw - 1, cy + ch - 1, relleno);
    }
}
