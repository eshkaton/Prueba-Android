package com.example.registromultimedia;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

/**
 * Punto único donde se define dónde viven los audios grabados.
 *
 * <p>Tanto la grabadora como el reproductor usan esta carpeta, así que todo
 * lo que se guarda en una pantalla aparece en la otra. La grabadora antes
 * calculaba la ruta por su cuenta y no contemplaba que
 * {@code getExternalFilesDir} pueda devolver {@code null} si el
 * almacenamiento externo no está montado.</p>
 */
public final class Almacenamiento {

    private static final String NOMBRE_CARPETA = "Grabaciones";

    /** Extensión de los audios que la app reconoce. */
    public static final String EXTENSION = ".m4a";

    private Almacenamiento() {
        // Clase de utilidad: no se instancia.
    }

    @NonNull
    public static File carpetaGrabaciones(@NonNull Context contexto) {
        File base = contexto.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (base == null) {
            // Sin almacenamiento externo se recurre al interno de la app.
            base = contexto.getFilesDir();
        }

        File carpeta = new File(base, NOMBRE_CARPETA);

        if (!carpeta.exists() && !carpeta.mkdirs() && !carpeta.isDirectory()) {
            // No se pudo crear: se devuelve el directorio interno, que existe.
            return contexto.getFilesDir();
        }

        return carpeta;
    }

    /** Nombre único para una grabación nueva. */
    @NonNull
    public static File nuevoArchivo(@NonNull Context contexto) {
        return new File(
                carpetaGrabaciones(contexto),
                "audio_" + System.currentTimeMillis() + EXTENSION
        );
    }
}
