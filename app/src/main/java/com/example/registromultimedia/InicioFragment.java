package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import java.util.Locale;

/**
 * Contenido de la pestaña «Inicio»: la vista de bienvenida.
 *
 * <p>Saluda al usuario de la sesión, resume el estado del equipo y ofrece
 * accesos rápidos al resto de pantallas.</p>
 */
public class InicioFragment extends Fragment {

    private TextView txtNombreUsuario;
    private TextView txtTotalIntegrantes;
    private TextView txtValoracionMedia;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);

        txtNombreUsuario = vista.findViewById(R.id.txtNombreUsuario);
        txtTotalIntegrantes = vista.findViewById(R.id.txtTotalIntegrantes);
        txtValoracionMedia = vista.findViewById(R.id.txtValoracionMedia);

        configurarAtajo(
                vista, R.id.atajoEquipo, R.drawable.ic_equipo,
                R.string.atajo_equipo_titulo, R.string.atajo_equipo_detalle,
                v -> irASeccion(R.id.nav_equipo));

        configurarAtajo(
                vista, R.id.atajoAgregar, R.drawable.ic_agregar_persona,
                R.string.atajo_agregar_titulo, R.string.atajo_agregar_detalle,
                v -> irASeccion(R.id.nav_agregar));

        configurarAtajo(
                vista, R.id.atajoGrabar, R.drawable.ic_microfono,
                R.string.atajo_grabar_titulo, R.string.atajo_grabar_detalle,
                v -> abrir(GrabacionAudioActivity.class));

        configurarAtajo(
                vista, R.id.atajoAudios, R.drawable.ic_biblioteca_audio,
                R.string.atajo_audios_titulo, R.string.atajo_audios_detalle,
                v -> abrir(ReproductorAudioActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al volver de otra pestaña las cifras pueden haber cambiado.
        actualizarDatos();
    }

    private void actualizarDatos() {
        txtNombreUsuario.setText(
                SesionManager.getNombreVisible(getString(R.string.bienvenida_invitado)));

        txtTotalIntegrantes.setText(
                String.format(Locale.getDefault(), "%d", IntegranteRepository.total()));

        txtValoracionMedia.setText(
                String.format(Locale.getDefault(), "%.1f", IntegranteRepository.valoracionMedia()));
    }

    /**
     * Rellena una de las tarjetas de acceso rápido.
     *
     * <p>Las cuatro comparten el layout {@code item_atajo}, así que se busca
     * cada campo dentro de la tarjeta concreta y no en toda la pantalla.</p>
     */
    private void configurarAtajo(
            @NonNull View raiz,
            @IdRes int idTarjeta,
            @DrawableRes int icono,
            @StringRes int titulo,
            @StringRes int detalle,
            @NonNull View.OnClickListener alPulsar
    ) {
        View tarjeta = raiz.findViewById(idTarjeta);

        ImageView imagen = tarjeta.findViewById(R.id.imgAtajo);
        TextView textoTitulo = tarjeta.findViewById(R.id.txtAtajoTitulo);
        TextView textoDetalle = tarjeta.findViewById(R.id.txtAtajoDetalle);

        imagen.setImageResource(icono);
        textoTitulo.setText(titulo);
        textoDetalle.setText(detalle);

        tarjeta.setContentDescription(getString(titulo));
        tarjeta.setOnClickListener(alPulsar);
    }

    private void irASeccion(@IdRes int idSeccion) {
        if (getActivity() instanceof WelcomeActivity) {
            ((WelcomeActivity) getActivity()).irASeccion(idSeccion);
        }
    }

    private void abrir(@NonNull Class<?> actividad) {
        startActivity(new Intent(requireContext(), actividad));
    }
}
