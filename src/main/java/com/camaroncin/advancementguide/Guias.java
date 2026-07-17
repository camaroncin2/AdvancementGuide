package com.camaroncin.advancementguide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Guias escritas por el jugador: config/advancementguide-guias.json
 *
 * Minecraft solo guarda UN texto por logro, y en 102 de los 126 ese texto ya es
 * la instruccion; por eso "descripcion" y "como conseguirlo" salian iguales.
 * Aqui se puede escribir un texto propio para cada logro, que es lo que se
 * muestra en "COMO CONSEGUIRLO".
 *
 * El mod ya trae 105 guias escritas, y van en los archivos de idioma
 * (assets/advancementguide/lang/*.json) con la clave que devuelve clave(). Ahi y
 * no en un JSON aparte porque asi Minecraft elige solo el idioma del jugador y
 * cae a ingles si algo falta, sin escribir ni una linea de codigo.
 *
 * El archivo de config MANDA sobre las del mod, y se genera solo la primera vez,
 * con los 126 logros y su nombre ya traducido para que sea facil encontrarlos.
 * Si en una version futura aparecen logros nuevos, se anaden sin tocar lo ya
 * escrito.
 */
public final class Guias {

    private static final Logger LOGGER = LoggerFactory.getLogger("AdvancementGuide");
    private static final Path RUTA =
            FabricLoader.getInstance().getConfigDir().resolve("advancementguide-guias.json");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting().disableHtmlEscaping().create();

    private static final String[] AYUDA = {
            "Tu propia guia de como conseguir cada logro. Manda sobre la que trae el mod.",
            "Escribe el texto en 'guia'. Si lo dejas vacio se usa la del mod, en tu idioma.",
            "Usa \\n para partir en varias lineas: el apartado tiene scroll, puede ser largo.",
            "'logro' es solo para que los encuentres; no hace falta tocarlo.",
            "Los logros de varios requisitos (biomas, comidas...) ya listan solos lo que te falta,",
            "asi que ahi la guia solo hace falta si quieres decir algo mas."
    };

    private static Map<String, String> guias;

    private Guias() {
    }

    /**
     * La clave de idioma de la guia de un logro.
     * "minecraft:story/mine_stone" -> "advancementguide.guia.minecraft.story.mine_stone"
     * (los ':' y '/' no pintan nada en una clave de idioma).
     */
    public static String clave(String id) {
        return "advancementguide.guia." + id.replace(':', '.').replace('/', '.');
    }

    /** La guia que haya escrito el jugador para ese logro, o null si no hay. */
    public static String de(String id) {
        if (guias == null) {
            guias = leer();
        }
        return guias.get(id);
    }

    /** Crea el archivo si falta, o le anade los logros que no esten todavia. */
    public static void prepararSiFalta(List<Logros.Entrada> todos) {
        Map<String, String> actuales = leer();
        guias = actuales;

        boolean faltaAlguno = false;
        for (Logros.Entrada e : todos) {
            if (!actuales.containsKey(e.id())) {
                faltaAlguno = true;
                break;
            }
        }
        if (!faltaAlguno && Files.exists(RUTA)) {
            return;
        }
        escribir(todos, actuales);
    }

    private static Map<String, String> leer() {
        Map<String, String> out = new HashMap<>();
        if (!Files.exists(RUTA)) {
            return out;
        }
        try (Reader r = Files.newBufferedReader(RUTA, StandardCharsets.UTF_8)) {
            JsonObject raiz = JsonParser.parseReader(r).getAsJsonObject();
            if (!raiz.has("logros")) {
                return out;
            }
            for (JsonElement el : raiz.getAsJsonArray("logros")) {
                JsonObject o = el.getAsJsonObject();
                if (!o.has("id")) {
                    continue;
                }
                String id = o.get("id").getAsString();
                String guia = o.has("guia") ? o.get("guia").getAsString().trim() : "";
                // Se guarda tambien la vacia: asi sabemos que el logro YA esta
                // en el archivo y no hay que volver a anadirlo.
                out.put(id, guia.isEmpty() ? "" : guia);
            }
            long escritas = out.values().stream().filter(s -> !s.isEmpty()).count();
            LOGGER.info("[AdvancementGuide] Guias: {} logros en el archivo, {} con texto propio.",
                    out.size(), escritas);
        } catch (Exception ex) {
            LOGGER.warn("[AdvancementGuide] No se pudo leer {}: {}", RUTA, ex.getMessage());
        }
        return out;
    }

    /** Reescribe el archivo conservando lo ya escrito. */
    private static void escribir(List<Logros.Entrada> todos, Map<String, String> actuales) {
        try {
            Files.createDirectories(RUTA.getParent());
            JsonObject raiz = new JsonObject();
            JsonArray ayuda = new JsonArray();
            for (String linea : AYUDA) {
                ayuda.add(linea);
            }
            raiz.add("_ayuda", ayuda);

            JsonArray arr = new JsonArray();
            for (Logros.Entrada e : todos) {
                JsonObject o = new JsonObject();
                o.addProperty("id", e.id());
                // El nombre ya traducido, para reconocerlo de un vistazo.
                o.addProperty("logro", e.titulo().getString());
                o.addProperty("guia", actuales.getOrDefault(e.id(), ""));
                arr.add(o);
            }
            raiz.add("logros", arr);

            try (Writer w = Files.newBufferedWriter(RUTA, StandardCharsets.UTF_8)) {
                GSON.toJson(raiz, w);
            }
            for (Logros.Entrada e : todos) {
                actuales.putIfAbsent(e.id(), "");
            }
            LOGGER.info("[AdvancementGuide] Guias: escrito {} con {} logros.", RUTA, todos.size());
        } catch (Exception ex) {
            LOGGER.warn("[AdvancementGuide] No se pudo escribir {}: {}", RUTA, ex.getMessage());
        }
    }
}
