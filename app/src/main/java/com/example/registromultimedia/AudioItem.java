package com.example.registromultimedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

/** Una grabación guardada, tal y como la muestra la biblioteca de audios. */
public class AudioItem {

    private final File archivo;
    private String nombre = "";
    private String fecha = "";
    private long duracionMs;

    public AudioItem(@NonNull File archivo) {
        this.archivo = archivo;
    }

    @NonNull
    public File getArchivo() {
        return archivo;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@Nullable String nombre) {
        this.nombre = nombre != null ? nombre : "";
    }

    @NonNull
    public String getFecha() {
        return fecha;
    }

    public void setFecha(@Nullable String fecha) {
        this.fecha = fecha != null ? fecha : "";
    }

    public long getDuracionMs() {
        return duracionMs;
    }

    public void setDuracionMs(long duracionMs) {
        this.duracionMs = Math.max(0L, duracionMs);
    }

    /** Dos elementos son el mismo audio si apuntan al mismo fichero. */
    public boolean esMismoArchivo(@Nullable AudioItem otro) {
        return otro != null && archivo.getAbsolutePath().equals(otro.archivo.getAbsolutePath());
    }
}
