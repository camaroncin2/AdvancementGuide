package com.camaroncin.advancementguide;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * Tema LOGPAD: el aparato rojo, dibujado pixel a pixel sobre una sola textura.
 * La logica (filtro, buscador, paginas, scroll) esta en PantallaLogros.
 */
public class LogPadScreen extends PantallaLogros {

    private static final Identifier TEXTURA =
            Identifier.fromNamespaceAndPath(AdvancementGuideClient.MOD_ID, "textures/gui/logpad.png");

    public static final int ANCHO = 420;   // aparato visible
    public static final int ALTO = 272;
    private static final int TEX_W = 420;  // lienzo real (sprites debajo del aparato)
    private static final int TEX_H = 310;  // debe caber el sprite mas alto (276+26=302)

    // Sprites (fuera del aparato)
    private static final int SEL_U = 16, SEL_V = 276, SEL_W = 160, SEL_H = 26;
    private static final int CATON_U = 184, CATON_V = 276, CATON_W = 60, CATON_H = 19;
    private static final int THUMB_U = 4, THUMB_V = 276, THUMB_W = 7, THUMB_H = 16;

    // 60 de ancho y no 52: "Inframundo" mide 58 px y se salia
    private static final int CAT_X = 18, CAT_W = 60, CAT_H = 19;
    private static final int[] CAT_Y = {114, 135, 156, 177, 198, 219};

    private static final int BUSC_X = 174, BUSC_Y = 51, BUSC_W = 130, BUSC_H = 12;

    private static final int POR_PAGINA = 6;
    private static final int FILA_X = 96, FILA_Y = 76, FILA_W = 160, FILA_H = 26, FILA_SEP = 28;

    private static final int NAV_Y = 44, NAV_W = 24, NAV_H = 24, PREV_X = 96, NEXT_X = 124;

    private static final int DET_X = 268, DET_W = 134;
    private static final int PAD = 8;               // margen interior del panel
    private static final int MAX_LIN_DESC = 3;      // con apartado de "como" debajo
    private static final int MAX_LIN_DESC_SOLA = 8; // sin el, se queda con el panel

    private static final int COMO_Y = 198, COMO_H = 38, SCROLL_X = 390;

    public LogPadScreen() {
        super("LogPad");
    }

    // ---- Geometria ----

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
        return new Rect(x + CAT_X, y + CAT_Y[i], CAT_W, CAT_H);
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
        return new Rect(x + FILA_X, y + FILA_Y + i * FILA_SEP, FILA_W, FILA_H - 4);
    }

    @Override
    protected Rect rectBuscador() {
        return new Rect(x + BUSC_X, y + BUSC_Y, BUSC_W, BUSC_H);
    }

    @Override
    protected Rect rectComo() {
        return new Rect(x + DET_X, y + COMO_Y - 12, DET_W, COMO_H + 12);
    }

    @Override
    protected int colorBuscador() {
        return 0xFF2A3A4A;
    }

    // ---- Dibujo ----

    @Override
    protected void dibujar(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        // 1) El aparato de fondo (solo la parte visible del lienzo)
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURA, x, y, 0f, 0f, ANCHO, ALTO, TEX_W, TEX_H);

        // 2) Cabeza 3D dentro del aro (aro: 30,46 de 36x36 -> centro 48,64).
        //    Escala 42 -> ~22 px, y al girar ~28: cabe en el 32 sin cortarse.
        caraEn3D(g, x + 48, y + 64, 32, 42, mouseX, mouseY);

        // 3) Contador global (sobre el catalogo completo, no el filtrado)
        int conseguidos = Logros.contarConseguidos(todos);
        g.centeredText(font, Component.literal(conseguidos + "/" + todos.size()), x + 48, y + 90, 0xFFFFFFFF);
        g.centeredText(font, Component.literal("LOGROS"), x + 48, y + 102, 0xFFB9C6D8);

        // 3) Botones de categoria (el activo se resalta con su sprite)
        for (int i = 0; i < CAT_ID.length; i++) {
            boolean activa = categoria.equals(CAT_ID[i]);
            if (activa) {
                g.blit(RenderPipelines.GUI_TEXTURED, TEXTURA, x + CAT_X, y + CAT_Y[i],
                        CATON_U, CATON_V, CATON_W, CATON_H, TEX_W, TEX_H);
            }
            g.centeredText(font, Component.literal(CAT_NOM[i]), x + CAT_X + CAT_W / 2,
                    y + CAT_Y[i] + 6, activa ? 0xFFFFFFFF : 0xFFB9C6D8);
        }

        // 4) Pagina actual
        g.text(font, Component.literal(pagina + "/" + paginas), x + 316, y + 52, 0xFFFFFFFF);

        if (lista.isEmpty()) {
            g.centeredText(font, Component.literal("Sin resultados"), x + 176, y + 140, 0xFFFFFFFF);
            return;
        }

        // 5) Filas de la lista: icono + nombre + fecha VISIBLES
        int desde = (pagina - 1) * POR_PAGINA;
        for (int i = 0; i < POR_PAGINA; i++) {
            int idx = desde + i;
            if (idx >= lista.size()) {
                break;
            }
            Logros.Entrada e = lista.get(idx);
            int fy = y + FILA_Y + i * FILA_SEP;

            // marco dorado de seleccion (sprite, no un relleno que ensucia)
            if (idx == seleccion) {
                g.blit(RenderPipelines.GUI_TEXTURED, TEXTURA, x + FILA_X, fy,
                        SEL_U, SEL_V, SEL_W, SEL_H, TEX_W, TEX_H);
            }

            // Icono y nombre SIEMPRE visibles (tambien en los no conseguidos).
            // Sin sombra (false): sobre la tarjeta clara la sombra se ve mal.
            g.item(e.icono(), x + FILA_X + 5, fy + 5);
            g.text(font, font.split(e.titulo(), FILA_W - 32).get(0), x + FILA_X + 28, fy + 4,
                    e.conseguido() ? 0xFF1E5B2A : 0xFF3D4A5C, false);
            String fechaTxt = e.conseguido() ? "Obtenido: " + fechaDe(e.progress()) : "--/--/----";
            g.text(font, Component.literal(fechaTxt), x + FILA_X + 28, fy + 15,
                    e.conseguido() ? 0xFF4A5568 : 0xFF8A97A8, false);
        }

        // 6) Panel de detalle (derecha)
        dibujarDetalle(g, lista.get(Math.min(seleccion, lista.size() - 1)));
    }

    /**
     * Panel de detalle. Se parte el texto a mano y se limita el numero de lineas
     * para que NUNCA se salga del panel.
     */
    private void dibujarDetalle(GuiGraphicsExtractor g, Logros.Entrada e) {
        int centro = x + DET_X + DET_W / 2;
        int dx = x + DET_X + PAD;
        int anchoTxt = DET_W - PAD * 2;
        boolean hecho = e.conseguido();

        // icono grande dentro del aro (aro en y+80, 32x32)
        g.item(e.icono(), centro - 8, y + 88);

        // titulo (recortado a una linea para que no rompa el panel)
        FormattedCharSequence tit = font.split(e.titulo(), anchoTxt).get(0);
        g.text(font, tit, centro - font.width(tit) / 2, y + 118,
                hecho ? 0xFFFFFFFF : 0xFFD6E4F0, false);
        g.centeredText(font, Component.literal(e.tipo().nombre().toUpperCase()),
                centro, y + 129, colorTipo(e.tipo()));

        // Si no hay nada que anadir abajo, la descripcion se queda con el panel.
        List<FormattedCharSequence> lineas = lineasComo(e, anchoTxt - 10);
        boolean hayComo = !lineas.isEmpty();

        g.text(font, Component.literal("DESCRIPCION:"), dx, y + 143, 0xFFFFE08A, false);
        parrafo(g, e.descripcion(), dx, y + 154, anchoTxt,
                hayComo ? MAX_LIN_DESC : MAX_LIN_DESC_SOLA, 0xFFEAF2FA);

        if (!hayComo) {
            return;
        }

        // ---- COMO CONSEGUIRLO (zona con scroll) ----
        g.text(font, Component.literal("COMO CONSEGUIRLO:"), dx, y + 186, 0xFFFFE08A, false);

        int visibles = COMO_H / font.lineHeight;
        int maxScroll = Math.max(0, lineas.size() - visibles);
        scrollComo = Math.min(scrollComo, maxScroll);

        for (int i = 0; i < visibles && i + scrollComo < lineas.size(); i++) {
            g.text(font, lineas.get(i + scrollComo), dx, y + COMO_Y + i * font.lineHeight,
                    hecho ? 0xFF9BE39B : 0xFFEAF2FA, false);
        }

        // Barra de scroll (el carril ya esta en la textura; el tirador se dibuja aqui)
        if (maxScroll > 0) {
            int recorrido = COMO_H - THUMB_H;
            int ty = y + COMO_Y + Math.round(recorrido * (scrollComo / (float) maxScroll));
            g.blit(RenderPipelines.GUI_TEXTURED, TEXTURA, x + SCROLL_X, ty,
                    THUMB_U, THUMB_V, THUMB_W, THUMB_H, TEX_W, TEX_H);
        }
    }

    private int colorTipo(Logros.Tipo tipo) {
        return switch (tipo) {
            case DESAFIO -> 0xFFD9A6F0;   // morado, como en el juego
            case OBJETIVO -> 0xFFFFE08A;
            default -> 0xFFB9C6D8;
        };
    }
}
