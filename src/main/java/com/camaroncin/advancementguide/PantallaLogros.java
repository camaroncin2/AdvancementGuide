package com.camaroncin.advancementguide;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Logica comun a todos los temas de la pantalla de logros.
 *
 * Aqui vive TODO lo que no depende del dibujo: catalogo, filtro por categoria,
 * buscador, paginacion, seleccion y scroll del "como conseguirlo". Cada tema
 * (LOGPAD, CODICE) solo aporta su geometria y su forma de pintar, asi un arreglo
 * en la logica vale para los dos.
 */
public abstract class PantallaLogros extends Screen {

    protected static final String[] CAT_ID = {"", "story", "nether", "end", "adventure", "husbandry"};
    protected static final String[] CAT_NOM = {"Todos", "Minería", "Inframundo", "El Fin", "Combate", "Granja"};

    protected static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());

    /** Zona rectangular en coordenadas de pantalla. */
    public record Rect(int x, int y, int w, int h) {
        public boolean dentro(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    protected List<Logros.Entrada> todos = List.of();   // catalogo completo
    protected List<Logros.Entrada> lista = List.of();   // ya filtrado
    protected EditBox buscador;
    protected String categoria = "";                    // "" = todas
    protected int x;
    protected int y;
    protected int pagina = 1;
    protected int paginas = 1;
    protected int seleccion = 0;
    protected int scrollComo = 0;

    protected PantallaLogros(String titulo) {
        super(Component.literal(titulo));
    }

    // ==================================================================
    //  Lo que define cada tema
    // ==================================================================

    protected abstract int ancho();

    protected abstract int alto();

    protected abstract int porPagina();

    protected abstract Rect rectCategoria(int i);

    protected abstract Rect rectPrev();

    protected abstract Rect rectNext();

    /** Fila i de la pagina actual (0 .. porPagina()-1). */
    protected abstract Rect rectFila(int i);

    /** Donde va el EditBox del buscador. */
    protected abstract Rect rectBuscador();

    /** Zona scrolleable de "como conseguirlo" (para la rueda del raton). */
    protected abstract Rect rectComo();

    /** Color ARGB del texto del buscador (¡el 0xFF de alfa es obligatorio!). */
    protected abstract int colorBuscador();

    /** Pinta el tema. El buscador lo pinta la base despues, para que quede encima. */
    protected abstract void dibujar(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick);

    // ==================================================================
    //  Ciclo de vida
    // ==================================================================

    @Override
    protected void init() {
        this.x = (this.width - ancho()) / 2;
        this.y = (this.height - alto()) / 2;
        this.todos = Logros.cargar();

        Rect r = rectBuscador();
        buscador = new EditBox(font, r.x(), r.y(), r.w(), r.h(), Component.literal("Buscar logro..."));
        buscador.setMaxLength(48);
        buscador.setHint(Component.literal("Buscar logro..."));
        buscador.setBordered(false);          // el marco lo pone el tema
        // OJO: el color es ARGB. Sin el 0xFF de alfa el texto y el cursor
        // salen transparentes (invisibles).
        buscador.setTextColor(colorBuscador());
        buscador.setResponder(t -> filtrar());
        // addWidget (no addRenderableWidget): solo registra los eventos; lo
        // dibujamos al final para que el fondo del tema no lo tape.
        addWidget(buscador);

        filtrar();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        dibujar(g, mouseX, mouseY, partialTick);
        // SIEMPRE el ultimo: si se pinta antes, el fondo del tema lo tapa y no
        // se ve ni el texto ni el cursor.
        buscador.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    // ==================================================================
    //  Datos: filtro, paginacion
    // ==================================================================

    /** Aplica categoria + texto del buscador. */
    protected void filtrar() {
        String q = buscador == null ? "" : buscador.getValue().trim().toLowerCase(Locale.ROOT);
        List<Logros.Entrada> out = new ArrayList<>();
        for (Logros.Entrada e : todos) {
            if (!categoria.isEmpty() && !categoriaDe(e).equals(categoria)) {
                continue;
            }
            if (!q.isEmpty() && !e.titulo().getString().toLowerCase(Locale.ROOT).contains(q)) {
                continue;
            }
            out.add(e);
        }
        this.lista = out;
        this.paginas = Math.max(1, (int) Math.ceil(lista.size() / (double) porPagina()));
        this.pagina = 1;
        this.seleccion = 0;
        this.scrollComo = 0;
    }

    /** "minecraft:story/mine_stone" -> "story" */
    protected String categoriaDe(Logros.Entrada e) {
        String id = e.id();
        int i = id.indexOf(':');
        String path = i >= 0 ? id.substring(i + 1) : id;
        int b = path.indexOf('/');
        return b > 0 ? path.substring(0, b) : path;
    }

    /** Entrada seleccionada, o null si la lista esta vacia. */
    protected Logros.Entrada seleccionada() {
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(Math.min(seleccion, lista.size() - 1));
    }

    // ==================================================================
    //  Cabeza 3D
    // ==================================================================

    /**
     * Cuanto gira la cabeza con el raton, en grados por radian de desvio.
     * Vanilla usa 20 para el cuerpo entero; en un retrato conviene menos, porque
     * al girar, la silueta de la cabeza crece (un cubo girado ocupa mas que de
     * frente) y hay que dejarla holgada dentro del recuadro.
     */
    private static final float GIRO = 12.0f;
    /** El centro del cubo de la cabeza queda un poco por encima de los ojos. */
    private static final float SOBRE_OJOS = 0.11f;
    /** Altura del centro de la cabeza de pie, por si el estado no trae los ojos. */
    private static final float CENTRO_CABEZA = 1.75f;

    /**
     * La cabeza del jugador en 3D, girando hacia el raton.
     *
     * Es la misma idea que usa la pantalla de inventario, pero copiada aqui en
     * vez de llamar a su ayudante, porque hay que tocar el estado de render y
     * el ayudante lo crea por dentro.
     *
     * Solo la cabeza: se marca el estado como ESPECTADOR. No es un apaño; es lo
     * que hace vanilla con los espectadores, que se ven flotando de cabeza:
     * PlayerModel.setupAnim apaga cuerpo, brazos y piernas (las capas de ropa
     * cuelgan de esas partes, asi que se van con ellas) y AvatarRenderer deja de
     * dibujar las capas de encima: armadura, capa y lo que se lleve en la cabeza.
     * Antes se recortaba con el recuadro, pero el recuadro tiene que bajar del
     * centro de la cabeza para darle sitio al girar, y justo ahi empieza el
     * cuerpo: por eso el hombro siempre asomaba y se veia el corte.
     *
     * Encuadre: el punto del modelo que queda centrado en el recuadro es el que
     * diga la Y de la traslacion. Se usa la altura de los ojos (+ un poco) en
     * vez de un valor fijo para que siga bien aunque el jugador este agachado.
     * La escala son pixeles por bloque, y la cabeza mide ~0,53 con el sombrero.
     */
    protected void caraEn3D(GuiGraphicsExtractor g, int cx, int cy, int tam, int escala,
                            int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer jugador = mc.player;
        if (jugador == null) {
            return;
        }

        EntityRenderer<? super LocalPlayer, ?> render =
                mc.getEntityRenderDispatcher().getRenderer(jugador);
        EntityRenderState estado = render.createRenderState(jugador, 1.0f);
        estado.shadowPieces.clear();
        estado.outlineColor = EntityRenderState.NO_OUTLINE;
        // Sin esto sale flotando el nombre del jugador sobre la cabeza.
        estado.nameTag = null;
        estado.scoreText = null;
        // Y con esto se queda SOLO la cabeza (ver arriba: es el truco del
        // espectador). Como ya no hay cuerpo, el recuadro no corta nada.
        if (estado instanceof AvatarRenderState avatar) {
            avatar.isSpectator = true;
        }

        int mitad = tam / 2;
        int x1 = cx - mitad, y1 = cy - mitad, x2 = cx + mitad, y2 = cy + mitad;
        float giroY = (float) Math.atan(((x1 + x2) / 2.0f - mouseX) / 40.0f);
        float giroX = (float) Math.atan(((y1 + y2) / 2.0f - mouseY) / 40.0f);

        // rotateZ(PI) es lo que pone al modelo derecho en la GUI
        Quaternionf giro = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf inclinacion = new Quaternionf()
                .rotateX(giroX * GIRO * ((float) Math.PI / 180f));
        giro.mul(inclinacion);

        float altura = CENTRO_CABEZA;
        if (estado instanceof LivingEntityRenderState vivo) {
            vivo.bodyRot = 180.0f + giroY * GIRO;
            vivo.yRot = giroY * GIRO;
            vivo.xRot = vivo.pose == Pose.FALL_FLYING ? 0.0f : -giroX * GIRO;
            vivo.boundingBoxWidth /= vivo.scale;
            vivo.boundingBoxHeight /= vivo.scale;
            vivo.scale = 1.0f;
        }
        if (estado.eyeHeight > 0.1f) {
            altura = estado.eyeHeight + SOBRE_OJOS;
        }

        g.entity(estado, escala, new Vector3f(0.0f, altura, 0.0f),
                giro, inclinacion, x1, y1, x2, y2);
    }

    // ==================================================================
    //  Texto
    // ==================================================================

    /** Dibuja un parrafo limitado a maxLineas. Devuelve la Y siguiente. */
    protected int parrafo(GuiGraphicsExtractor g, Component texto, int px, int py, int anchoTxt,
                          int maxLineas, int color) {
        List<FormattedCharSequence> lineas = font.split(texto, anchoTxt);
        int n = Math.min(lineas.size(), maxLineas);
        for (int i = 0; i < n; i++) {
            g.text(font, lineas.get(i), px, py + i * font.lineHeight, color, false);
        }
        return py + n * font.lineHeight;
    }

    /**
     * Las lineas del "como conseguirlo" ya partidas (para poder scrollear).
     * Lista VACIA = no hay nada que anadir y el apartado no se dibuja.
     */
    protected List<FormattedCharSequence> lineasComo(Logros.Entrada e, int anchoTxt) {
        String texto = comoConseguirlo(e);
        if (texto == null) {
            return List.of();
        }
        return font.split(Component.literal(texto), anchoTxt);
    }

    /**
     * Que poner en "COMO CONSEGUIRLO", o null si no hay nada que anadir.
     *
     * Minecraft guarda UN solo texto por logro, y en 102 de los 126 ese texto ya
     * es la instruccion ("Mata un breeze devolviendole su carga de viento"), asi
     * que repetirlo aqui no aporta nada. Por orden:
     *  1. la guia que haya escrito el jugador (config/advancementguide-guias.json);
     *  2. si ya lo tiene, decirlo;
     *  3. si son varios requisitos, lo que le falta de verdad (eso SI es una guia);
     *  4. si no, null: la descripcion ya lo explica y se queda con todo el panel.
     */
    protected String comoConseguirlo(Logros.Entrada e) {
        String propia = Guias.de(e.id());
        if (propia != null && !propia.isEmpty()) {
            return propia;
        }
        if (e.conseguido()) {
            return "¡Ya lo tienes!";
        }

        // Requisitos que faltan de verdad (si el cliente los conoce)
        AdvancementProgress p = e.progress();
        if (p != null && p.hasProgress()) {
            List<String> faltan = new ArrayList<>();
            for (String c : p.getRemainingCriteria()) {
                faltan.add(limpiar(c));
            }
            int hechos = 0;
            for (String ignored : p.getCompletedCriteria()) {
                hechos++;
            }
            if (faltan.size() > 1 || hechos > 0) {
                return "Te faltan " + faltan.size() + " de " + (hechos + faltan.size()) + ":\n"
                        + String.join(", ", faltan);
            }
        }

        // Varios requisitos -> listarlos (del catalogo del juego)
        if (e.totalReq() > 1) {
            List<String> reqs = new ArrayList<>();
            for (String c : e.requisitos()) {
                reqs.add(limpiar(c));
            }
            String extra = e.totalReq() > reqs.size()
                    ? " (y " + (e.totalReq() - reqs.size()) + " mas)" : "";
            return "Completa los " + e.totalReq() + ":\n" + String.join(", ", reqs) + extra;
        }

        return null;
    }

    /** "minecraft:snowy_slopes" -> "Snowy slopes" (mas legible). */
    protected String limpiar(String c) {
        String s = c;
        int i = s.indexOf(':');
        if (i >= 0) {
            s = s.substring(i + 1);
        }
        int b = s.lastIndexOf('/');
        if (b >= 0) {
            s = s.substring(b + 1);
        }
        s = s.replace('_', ' ').trim();
        if (s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    protected String fechaDe(AdvancementProgress p) {
        if (p == null) {
            return "?";
        }
        Instant ultima = null;
        for (String c : p.getCompletedCriteria()) {
            Instant t = p.getCriterion(c).getObtained();
            if (t != null && (ultima == null || t.isAfter(ultima))) {
                ultima = t;
            }
        }
        return ultima != null ? FECHA.format(ultima) : "?";
    }

    // ==================================================================
    //  Entrada: clics, rueda, teclado
    // ==================================================================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x();
        double my = event.y();

        for (int i = 0; i < CAT_ID.length; i++) {
            if (rectCategoria(i).dentro(mx, my)) {
                categoria = CAT_ID[i];
                filtrar();
                return true;
            }
        }

        if (rectPrev().dentro(mx, my) && pagina > 1) {
            pagina--;
            irAlPrimeroDeLaPagina();
            return true;
        }
        if (rectNext().dentro(mx, my) && pagina < paginas) {
            pagina++;
            irAlPrimeroDeLaPagina();
            return true;
        }

        int desde = (pagina - 1) * porPagina();
        for (int i = 0; i < porPagina(); i++) {
            int idx = desde + i;
            if (idx >= lista.size()) {
                break;
            }
            if (rectFila(i).dentro(mx, my)) {
                seleccion = idx;
                scrollComo = 0;   // al cambiar de logro, el scroll vuelve arriba
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    /** Al cambiar de pagina, seleccionar el primero VISIBLE (si no, el panel de
     *  detalle mostraria un logro que ya no esta en la lista). */
    private void irAlPrimeroDeLaPagina() {
        seleccion = Math.min((pagina - 1) * porPagina(), Math.max(0, lista.size() - 1));
        scrollComo = 0;
    }

    /** Rueda del raton: solo hace scroll dentro de la zona de "como conseguirlo". */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (rectComo().dentro(mouseX, mouseY)) {
            scrollComo = Math.max(0, scrollComo - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // T cambia de tema. Si el buscador tiene el foco hay que dejar escribir
        // la letra, claro.
        if (event.key() == GLFW.GLFW_KEY_T && (buscador == null || !buscador.isFocused())) {
            Config.cambiarTema();
            if (minecraft != null) {
                minecraft.setScreenAndShow(Config.tema().crear());
            }
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
