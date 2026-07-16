package com.camaroncin.advancementguide.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Da acceso al mapa privado de progreso de logros del cliente.
 * Se usa un accessor (en vez de setListener) para NO pisar el listener que usa
 * la pantalla de logros vanilla.
 */
@Mixin(ClientAdvancements.class)
public interface ClientAdvancementsAccessor {

    @Accessor("progress")
    Map<AdvancementHolder, AdvancementProgress> ag$getProgress();
}
