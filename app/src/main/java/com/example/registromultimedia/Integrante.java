package com.example.registromultimedia;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Integrante del equipo.
 *
 * <p>Antes había dos constructores y el más corto colocaba el género en el
 * campo {@code jornada}, así que la ficha mostraba «Jornada: Masculino».
 * Ahora género y jornada son campos distintos y hay un único constructor,
 * de modo que el compilador impide volver a confundirlos.</p>
 */
public class Integrante {

    /** Valor de {@link #getFotoResId()} cuando el integrante no tiene foto. */
    public static final int SIN_FOTO = 0;

    private final String nombre;
    private final String rol;
    private final String tecnologias;
    private final String genero;
    private final String jornada;
    @DrawableRes
    private final int fotoResId;

    private float valoracion;
    private String nivel;
    private String comentario;

    public Integrante(
            @NonNull String nombre,
            @NonNull String rol,
            @NonNull String tecnologias,
            @NonNull String genero,
            @NonNull String jornada,
            @DrawableRes int fotoResId,
            float valoracion
    ) {
        this.nombre = nombre;
        this.rol = rol;
        this.tecnologias = tecnologias;
        this.genero = genero;
        this.jornada = jornada;
        this.fotoResId = fotoResId;
        this.valoracion = valoracion;
        this.nivel = "";
        this.comentario = "";
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    @NonNull
    public String getRol() {
        return rol;
    }

    @NonNull
    public String getTecnologias() {
        return tecnologias;
    }

    @NonNull
    public String getGenero() {
        return genero;
    }

    @NonNull
    public String getJornada() {
        return jornada;
    }

    /** {@link #SIN_FOTO} si hay que dibujar el avatar genérico. */
    @DrawableRes
    public int getFotoResId() {
        return fotoResId;
    }

    public boolean tieneFoto() {
        return fotoResId != SIN_FOTO;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = Math.max(0f, Math.min(5f, valoracion));
    }

    @NonNull
    public String getNivel() {
        return nivel;
    }

    public void setNivel(@Nullable String nivel) {
        this.nivel = nivel != null ? nivel : "";
    }

    @NonNull
    public String getComentario() {
        return comentario;
    }

    public void setComentario(@Nullable String comentario) {
        this.comentario = comentario != null ? comentario : "";
    }

    @NonNull
    @Override
    public String toString() {
        // Lo usa el Spinner de la pantalla de progreso.
        return nombre;
    }
}
