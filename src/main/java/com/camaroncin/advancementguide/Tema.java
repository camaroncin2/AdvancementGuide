package com.camaroncin.advancementguide;

import net.minecraft.client.gui.screens.Screen;

/** Los aspectos disponibles del menu. Se cambia con T dentro de la pantalla. */
public enum Tema {

    /** El aparato rojo original. */
    LOGPAD("LogPad"),
    /** El codice prehispanico (piedra, jade y obsidiana). */
    CODICE("Codice");

    private final String nombre;

    Tema(String nombre) {
        this.nombre = nombre;
    }

    public String nombre() {
        return nombre;
    }

    public Screen crear() {
        return this == CODICE ? new CodiceScreen() : new LogPadScreen();
    }

    public Tema siguiente() {
        return this == CODICE ? LOGPAD : CODICE;
    }
}
