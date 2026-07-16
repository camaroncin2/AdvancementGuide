package com.camaroncin.advancementguide;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Mod de CLIENTE: abre el menu LOGPAD con una tecla (G por defecto).
 * Al ser un mod, la interfaz se dibuja pixel a pixel donde queramos.
 */
public class AdvancementGuideClient implements ClientModInitializer {

    public static final String MOD_ID = "advancementguide";

    private static KeyMapping abrirMenu;

    @Override
    public void onInitializeClient() {
        abrirMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.advancementguide.abrir",
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (abrirMenu.consumeClick()) {
                // El tema elegido se recuerda; dentro del menu se cambia con T.
                client.setScreenAndShow(Config.tema().crear());
            }
        });
    }
}
