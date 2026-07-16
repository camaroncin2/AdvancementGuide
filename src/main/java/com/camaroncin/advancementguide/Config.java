package com.camaroncin.advancementguide;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Ajustes del mod. De momento solo el tema elegido, guardado en
 * config/advancementguide.properties para que se recuerde entre partidas.
 */
public final class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger("AdvancementGuide");
    private static final Path RUTA =
            FabricLoader.getInstance().getConfigDir().resolve("advancementguide.properties");

    private static Tema tema;

    private Config() {
    }

    public static Tema tema() {
        if (tema == null) {
            cargar();
        }
        return tema;
    }

    /** Pasa al siguiente tema y lo deja guardado. */
    public static void cambiarTema() {
        tema = tema().siguiente();
        guardar();
        LOGGER.info("[AdvancementGuide] Tema: {}", tema.nombre());
    }

    private static void cargar() {
        tema = Tema.CODICE;   // por defecto, el nuevo
        try {
            if (!Files.exists(RUTA)) {
                return;
            }
            for (String linea : Files.readAllLines(RUTA)) {
                String l = linea.trim();
                if (l.startsWith("tema=")) {
                    String v = l.substring(5).trim();
                    for (Tema t : Tema.values()) {
                        if (t.name().equalsIgnoreCase(v)) {
                            tema = t;
                            return;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("[AdvancementGuide] No se pudo leer {}: {}", RUTA, ex.getMessage());
        }
    }

    private static void guardar() {
        try {
            Files.createDirectories(RUTA.getParent());
            Files.write(RUTA, List.of(
                    "# LogPad — tema del menu. Valores: LOGPAD, CODICE",
                    "# (tambien se cambia con la tecla T dentro del menu)",
                    "tema=" + tema.name()));
        } catch (Exception ex) {
            LOGGER.warn("[AdvancementGuide] No se pudo guardar {}: {}", RUTA, ex.getMessage());
        }
    }
}
