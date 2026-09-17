package com.example.registromultimedia;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Punto único donde se define dónde viven los audios grabados.
 * Tanto la Grabadora como el Reproductor usan esta misma carpeta,
 * por lo que todo lo que se guarda en una ventana aparece en la otra.
 */
public class Almacenamiento {

    private static final String NOMBRE_CARPETA = "Grabaciones";

    public static File carpetaGrabaciones(Context contexto) {

        File base = contexto.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (base == null) {
            base = contexto.getFilesDir();
        }

        File carpeta = new File(base, NOMBRE_CARPETA);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        return carpeta;
    }
}
