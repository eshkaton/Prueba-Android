package com.example.registromultimedia;

import java.io.File;

public class AudioItem {

    private final File archivo;
    private String nombre;
    private String fecha;
    private long duracionMs;

    public AudioItem(File archivo) {
        this.archivo = archivo;
    }

    public File getArchivo() {
        return archivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public long getDuracionMs() {
        return duracionMs;
    }

    public void setDuracionMs(long duracionMs) {
        this.duracionMs = duracionMs;
    }
}
