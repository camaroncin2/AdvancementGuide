package com.camaroncin.advancementguide;

import com.camaroncin.advancementguide.mixin.ClientAdvancementsAccessor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lista COMPLETA de logros.
 *
 * El cliente solo recibe del servidor los logros que le hace "visibles", asi que
 * no basta con leer ClientAdvancements. La lista completa (126 logros vanilla) se
 * saca de un catalogo incluido en el mod, extraido de las definiciones del propio
 * Minecraft, y se fusiona con el progreso real del cliente.
 */
public final class Logros {

    private static final Logger LOGGER = LoggerFactory.getLogger("AdvancementGuide");
    private static final String CATALOGO = "/assets/advancementguide/logros_vanilla.json";

    /** Tarea, objetivo o desafio (el "frame" del logro; los desafios son los duros). */
    public enum Tipo {
        TAREA("Tarea"), OBJETIVO("Objetivo"), DESAFIO("Desafio");

        private final String nombre;

        Tipo(String nombre) {
            this.nombre = nombre;
        }

        public String nombre() {
            return nombre;
        }

        static Tipo de(String frame) {
            return switch (frame) {
                case "challenge" -> DESAFIO;
                case "goal" -> OBJETIVO;
                default -> TAREA;
            };
        }
    }

    /** Un logro: datos del catalogo + progreso real (null si el cliente no lo tiene). */
    public record Entrada(String id, Component titulo, Component descripcion, ItemStack icono,
                          Tipo tipo, int totalReq, List<String> requisitos,
                          AdvancementProgress progress) {

        public boolean conseguido() {
            return progress != null && progress.isDone();
        }
    }

    private record Def(String id, String iconId, Component titulo, Component descripcion,
                       Tipo tipo, int totalReq, List<String> requisitos) {
    }

    private static List<Def> catalogo;

    private Logros() {
    }

    /** Carga (una vez) el catalogo de logros incluido en el mod. */
    private static List<Def> catalogo() {
        if (catalogo != null) {
            return catalogo;
        }
        List<Def> out = new ArrayList<>();
        try (InputStream in = Logros.class.getResourceAsStream(CATALOGO)) {
            if (in == null) {
                LOGGER.error("[AdvancementGuide] No se encontro el catalogo {}", CATALOGO);
                catalogo = out;
                return out;
            }
            JsonArray arr = JsonParser.parseReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonArray();
            for (JsonElement el : arr) {
                JsonObject o = el.getAsJsonObject();
                String id = o.get("id").getAsString();
                String icon = o.has("icon") ? o.get("icon").getAsString() : "minecraft:paper";
                Component titulo = o.has("title")
                        ? Component.translatable(o.get("title").getAsString())
                        : Component.literal(o.has("titleLit") ? o.get("titleLit").getAsString() : id);
                Component desc = o.has("desc")
                        ? Component.translatable(o.get("desc").getAsString())
                        : Component.literal(o.has("descLit") ? o.get("descLit").getAsString() : "");
                Tipo tipo = Tipo.de(o.has("frame") ? o.get("frame").getAsString() : "task");
                int totalReq = o.has("critN") ? o.get("critN").getAsInt() : 0;
                List<String> reqs = new ArrayList<>();
                if (o.has("crit")) {
                    for (JsonElement c : o.getAsJsonArray("crit")) {
                        reqs.add(c.getAsString());
                    }
                }
                out.add(new Def(id, icon, titulo, desc, tipo, totalReq, reqs));
            }
            LOGGER.info("[AdvancementGuide] Catalogo cargado: {} logros.", out.size());
        } catch (Exception ex) {
            LOGGER.error("[AdvancementGuide] Fallo leyendo el catalogo: {}", ex.getMessage());
        }
        catalogo = out;
        return out;
    }

    /** Todos los logros, con el progreso real del jugador donde exista. */
    public static List<Entrada> cargar() {
        Map<String, AdvancementProgress> porId = progresoDelCliente();

        List<Entrada> out = new ArrayList<>();
        for (Def d : catalogo()) {
            out.add(new Entrada(d.id(), d.titulo(), d.descripcion(), icono(d.iconId()),
                    d.tipo(), d.totalReq(), d.requisitos(), porId.get(d.id())));
        }
        LOGGER.info("[AdvancementGuide] Logros: {} (con progreso conocido: {})", out.size(), porId.size());
        // Deja listo el archivo de guias del jugador (con los nombres ya traducidos).
        Guias.prepararSiFalta(out);
        return out;
    }

    /** Progreso que el cliente conoce, indexado por id. */
    private static Map<String, AdvancementProgress> progresoDelCliente() {
        Map<String, AdvancementProgress> porId = new HashMap<>();
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener conn = mc.getConnection();
        if (conn == null) {
            return porId;
        }
        ClientAdvancements adv = conn.getAdvancements();
        Map<AdvancementHolder, AdvancementProgress> progreso =
                ((ClientAdvancementsAccessor) adv).ag$getProgress();
        for (Map.Entry<AdvancementHolder, AdvancementProgress> e : progreso.entrySet()) {
            porId.put(e.getKey().id().toString(), e.getValue());
        }
        return porId;
    }

    private static ItemStack icono(String itemId) {
        try {
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
            return new ItemStack(item);
        } catch (Exception ex) {
            return new ItemStack(Items.PAPER);
        }
    }

    public static int contarConseguidos(List<Entrada> lista) {
        int n = 0;
        for (Entrada e : lista) {
            if (e.conseguido()) {
                n++;
            }
        }
        return n;
    }
}
