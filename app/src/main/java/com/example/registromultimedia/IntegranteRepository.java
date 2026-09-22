package com.example.registromultimedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lista de integrantes compartida por todas las vistas.
 *
 * <p>Antes cada pantalla creaba su propia lista: el formulario construía un
 * {@code Integrante} y lo descartaba, y el listado siempre mostraba los tres
 * integrantes de ejemplo. Con un repositorio único, lo que se agrega en la
 * pestaña «Agregar» aparece en «Equipo» y se puede valorar en «Progreso».</p>
 */
public final class IntegranteRepository {

    private static final List<Integrante> INTEGRANTES =
            Collections.synchronizedList(new ArrayList<>());

    static {
        cargarEjemplos();
    }

    private IntegranteRepository() {
        // Clase de utilidad: no se instancia.
    }

    private static void cargarEjemplos() {
        INTEGRANTES.add(new Integrante(
                "Lucas Bilbao", "Desarrollador", "Java, Android",
                "Masculino", "Diurno", R.drawable.lucas, 5.0f));

        INTEGRANTES.add(new Integrante(
                "Bruno Antio", "Analista", "Python, SQL",
                "Masculino", "Diurno", R.drawable.bruno, 4.5f));

        INTEGRANTES.add(new Integrante(
                "Anghel Lopez", "Diseñador", "Figma, UX",
                "Masculino", "Vespertino", R.drawable.anghel, 4.0f));
    }

    /** Copia de la lista: el llamante puede recorrerla sin riesgo de conflicto. */
    @NonNull
    public static List<Integrante> getIntegrantes() {
        synchronized (INTEGRANTES) {
            return new ArrayList<>(INTEGRANTES);
        }
    }

    public static void agregar(@NonNull Integrante integrante) {
        INTEGRANTES.add(integrante);
    }

    public static int total() {
        return INTEGRANTES.size();
    }

    public static boolean estaVacio() {
        return INTEGRANTES.isEmpty();
    }

    @Nullable
    public static Integrante enPosicion(int posicion) {
        synchronized (INTEGRANTES) {
            if (posicion < 0 || posicion >= INTEGRANTES.size()) {
                return null;
            }
            return INTEGRANTES.get(posicion);
        }
    }

    /** Media de valoraciones, o 0 si todavía no hay integrantes. */
    public static float valoracionMedia() {
        synchronized (INTEGRANTES) {
            if (INTEGRANTES.isEmpty()) {
                return 0f;
            }

            float suma = 0f;
            for (Integrante integrante : INTEGRANTES) {
                suma += integrante.getValoracion();
            }

            return suma / INTEGRANTES.size();
        }
    }
}
