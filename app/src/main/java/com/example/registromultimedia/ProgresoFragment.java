package com.example.registromultimedia;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/**
 * Pestaña «Progreso»: nivel, valoración y comentarios de un integrante.
 *
 * <p>La valoración ya no se pierde en un Toast: se guarda en el integrante
 * elegido, así que el listado y la media de la bienvenida la reflejan.</p>
 */
public class ProgresoFragment extends Fragment {

    /** Campos que cuentan para la barra de progreso. */
    private static final int TOTAL_CAMPOS = 3;

    private Spinner spIntegrante;
    private Spinner spNivel;
    private RatingBar rbValoracion;
    private TextInputEditText etComentarios;
    private LinearProgressIndicator pbProgreso;
    private TextView tvProgreso;
    private TextView tvSinIntegrantes;
    private MaterialButton btnGuardar;

    /** Posición del elemento «Selecciona un nivel» dentro del Spinner. */
    private static final int POSICION_SIN_NIVEL = 0;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_progreso, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);

        spIntegrante = vista.findViewById(R.id.sp_integrante);
        spNivel = vista.findViewById(R.id.sp_nivel);
        rbValoracion = vista.findViewById(R.id.rb_valoracion);
        etComentarios = vista.findViewById(R.id.et_comentarios);
        pbProgreso = vista.findViewById(R.id.pb_progreso);
        tvProgreso = vista.findViewById(R.id.tv_progreso);
        tvSinIntegrantes = vista.findViewById(R.id.tv_sin_integrantes);
        btnGuardar = vista.findViewById(R.id.btn_guardar);

        configurarNiveles();
        configurarListeners();
        actualizarProgreso();
    }

    @Override
    public void onResume() {
        super.onResume();
        // La lista puede haber crecido mientras se usaba otra pestaña.
        cargarIntegrantes();
    }

    private void cargarIntegrantes() {
        List<Integrante> integrantes = IntegranteRepository.getIntegrantes();
        boolean vacio = integrantes.isEmpty();

        tvSinIntegrantes.setVisibility(vacio ? View.VISIBLE : View.GONE);
        spIntegrante.setEnabled(!vacio);
        btnGuardar.setEnabled(!vacio);

        ArrayAdapter<Integrante> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, integrantes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIntegrante.setAdapter(adapter);
    }

    private void configurarNiveles() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.niveles_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNivel.setAdapter(adapter);

        // El Spinner avisa de una selección en cuanto recibe su adaptador.
        // Por eso la primera opción es un texto de aviso en lugar de un nivel
        // real: así el progreso arranca en 0 % y no en 33 %, como ocurría
        // cuando «Principiante» quedaba seleccionado de entrada.
        spNivel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View vista, int posicion, long id) {
                actualizarProgreso();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                actualizarProgreso();
            }
        });
    }

    private void configurarListeners() {
        rbValoracion.setOnRatingBarChangeListener(
                (barra, valoracion, deUsuario) -> actualizarProgreso());

        etComentarios.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int inicio, int cuenta, int despues) { }

            @Override
            public void onTextChanged(CharSequence s, int inicio, int antes, int cuenta) {
                actualizarProgreso();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnGuardar.setOnClickListener(v -> guardarValoracion());
    }

    private void actualizarProgreso() {
        int completados = 0;

        if (hayNivelElegido()) completados++;
        if (rbValoracion.getRating() > 0) completados++;
        if (!comentario().isEmpty()) completados++;

        int porcentaje = (completados * 100) / TOTAL_CAMPOS;

        pbProgreso.setProgressCompat(porcentaje, true);
        tvProgreso.setText(getString(R.string.progreso_etiqueta, porcentaje));
    }

    private void guardarValoracion() {
        Object seleccion = spIntegrante.getSelectedItem();

        if (!(seleccion instanceof Integrante)) {
            Toast.makeText(
                    requireContext(),
                    R.string.progreso_sin_integrantes,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Integrante integrante = (Integrante) seleccion;

        integrante.setValoracion(rbValoracion.getRating());
        integrante.setComentario(comentario());

        Object nivel = spNivel.getSelectedItem();
        if (hayNivelElegido() && nivel != null) {
            integrante.setNivel(nivel.toString());
        }

        Toast.makeText(
                requireContext(),
                getString(R.string.exito_valoracion, integrante.getNombre()),
                Toast.LENGTH_SHORT
        ).show();

        limpiarFormulario();
    }

    private boolean hayNivelElegido() {
        return spNivel.getSelectedItemPosition() != POSICION_SIN_NIVEL;
    }

    @NonNull
    private String comentario() {
        return etComentarios.getText() == null
                ? ""
                : etComentarios.getText().toString().trim();
    }

    private void limpiarFormulario() {
        etComentarios.setText("");
        rbValoracion.setRating(0);
        spNivel.setSelection(POSICION_SIN_NIVEL);
        actualizarProgreso();
    }
}
