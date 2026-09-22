package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Pantalla de bienvenida y centro de navegación de la app.
 *
 * <p>Es lo primero que se ve tras iniciar sesión. Desde aquí se llega al
 * resto de vistas por dos menús:</p>
 *
 * <ul>
 *     <li>la <b>barra inferior</b>, que intercambia los fragmentos de inicio,
 *     equipo, alta y progreso;</li>
 *     <li>la <b>barra superior</b>, con la grabadora, la biblioteca de audios
 *     y el cierre de sesión.</li>
 * </ul>
 *
 * <p>Sustituye a la antigua {@code MainActivity}, que apilaba el formulario,
 * la valoración y el listado en una sola pantalla con menú lateral.</p>
 */
public class WelcomeActivity extends AppCompatActivity {

    /** Guarda la pestaña activa para restaurarla al girar la pantalla. */
    private static final String ESTADO_SECCION = "seccion_activa";

    private MaterialToolbar toolbar;
    private BottomNavigationView menuInferior;

    @IdRes
    private int seccionActiva = R.id.nav_inicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        toolbar = findViewById(R.id.toolbar);
        menuInferior = findViewById(R.id.menuInferior);

        // La Toolbar se usa suelta (el tema es NoActionBar): así el menú se
        // gestiona con su propio listener, sin pasar por la ActionBar.
        toolbar.setOnMenuItemClickListener(this::alPulsarMenuSuperior);

        if (savedInstanceState != null) {
            seccionActiva = savedInstanceState.getInt(ESTADO_SECCION, R.id.nav_inicio);
            // El FragmentManager ya restauró el fragmento: solo falta el título.
            actualizarTitulo(seccionActiva);
        } else {
            mostrarSeccion(R.id.nav_inicio);
        }

        // Se marca la pestaña antes de registrar el listener para que
        // restaurar el estado no vuelva a recrear el fragmento.
        menuInferior.setSelectedItemId(seccionActiva);

        menuInferior.setOnItemSelectedListener(item -> {
            mostrarSeccion(item.getItemId());
            return true;
        });

        // Volver a tocar la pestaña activa no debe recrear el fragmento.
        menuInferior.setOnItemReselectedListener(item -> { });

        configurarBotonAtras();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ESTADO_SECCION, seccionActiva);
    }

    /**
     * Sustituye a {@code onBackPressed()}, obsoleto desde Android 13.
     *
     * <p>Desde cualquier pestaña, «atrás» lleva primero a Inicio; solo desde
     * Inicio se sale de la app.</p>
     */
    private void configurarBotonAtras() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (seccionActiva != R.id.nav_inicio) {
                    menuInferior.setSelectedItemId(R.id.nav_inicio);
                    return;
                }

                // Nadie más maneja el gesto: se delega en el comportamiento
                // por defecto del sistema.
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void mostrarSeccion(@IdRes int idSeccion) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.contenedorVistas, crearFragmento(idSeccion))
                .commit();

        seccionActiva = idSeccion;
        actualizarTitulo(idSeccion);
    }

    @NonNull
    private Fragment crearFragmento(@IdRes int idSeccion) {
        if (idSeccion == R.id.nav_equipo) {
            return new IntegranteFragment();
        }
        if (idSeccion == R.id.nav_agregar) {
            return new FormularioFragment();
        }
        if (idSeccion == R.id.nav_progreso) {
            return new ProgresoFragment();
        }
        return new InicioFragment();
    }

    private void actualizarTitulo(@IdRes int idSeccion) {
        @StringRes int titulo;

        if (idSeccion == R.id.nav_equipo) {
            titulo = R.string.nav_equipo;
        } else if (idSeccion == R.id.nav_agregar) {
            titulo = R.string.nav_agregar;
        } else if (idSeccion == R.id.nav_progreso) {
            titulo = R.string.nav_progreso;
        } else {
            titulo = R.string.app_name;
        }

        toolbar.setTitle(titulo);
    }

    /** Acciones del menú superior: grabadora, biblioteca y cierre de sesión. */
    private boolean alPulsarMenuSuperior(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.accion_grabar) {
            startActivity(new Intent(this, GrabacionAudioActivity.class));
            return true;
        }

        if (id == R.id.accion_audios) {
            startActivity(new Intent(this, ReproductorAudioActivity.class));
            return true;
        }

        if (id == R.id.accion_cerrar_sesion) {
            cerrarSesion();
            return true;
        }

        return false;
    }

    private void cerrarSesion() {
        SesionManager.cerrarSesion();
        Toast.makeText(this, R.string.sesion_cerrada, Toast.LENGTH_SHORT).show();

        // Se limpia la pila para que «atrás» no devuelva a la sesión cerrada.
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /** Permite a los fragmentos saltar a otra pestaña (los accesos rápidos). */
    public void irASeccion(@IdRes int idSeccion) {
        menuInferior.setSelectedItemId(idSeccion);
    }
}
