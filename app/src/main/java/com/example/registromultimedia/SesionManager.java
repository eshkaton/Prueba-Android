package com.example.registromultimedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Guarda quién ha iniciado sesión durante la ejecución de la app.
 *
 * <p>La pantalla de bienvenida la consulta para saludar al usuario y el menú
 * superior la limpia al cerrar sesión.</p>
 */
public final class SesionManager {

    @Nullable
    private static volatile User usuarioActivo;

    private SesionManager() {
        // Clase de utilidad: no se instancia.
    }

    public static void iniciarSesion(@NonNull User usuario) {
        usuarioActivo = usuario;
    }

    public static void cerrarSesion() {
        usuarioActivo = null;
    }

    public static boolean haySesion() {
        return usuarioActivo != null;
    }

    @Nullable
    public static User getUsuarioActivo() {
        return usuarioActivo;
    }

    /** Nombre corto para el saludo; nunca devuelve null. */
    @NonNull
    public static String getNombreVisible(@NonNull String porDefecto) {
        User usuario = usuarioActivo;
        return usuario != null ? usuario.getNombreVisible() : porDefecto;
    }
}
