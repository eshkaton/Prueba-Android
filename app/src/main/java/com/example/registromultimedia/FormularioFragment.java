package com.example.registromultimedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Pestaña «Agregar»: alta de un integrante del equipo.
 *
 * <p>Antes este formulario construía un {@code Integrante} y lo descartaba,
 * así que agregar a alguien no tenía ningún efecto visible. Ahora lo guarda
 * en {@link IntegranteRepository} y aparece en la pestaña «Equipo».</p>
 */
public class FormularioFragment extends Fragment {

    /** Longitud mínima aceptada para el nombre. */
    private static final int MINIMO_NOMBRE = 3;

    private TextInputLayout tilNombre;
    private TextInputEditText etNombre;
    private Spinner spRol;
    private Spinner spJornada;
    private CheckBox cbJava, cbAndroid, cbWeb, cbIa, cbDiseno, cbOtros;
    private RadioGroup rgGenero;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_formulario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);

        tilNombre = vista.findViewById(R.id.til_nombre);
        etNombre = vista.findViewById(R.id.et_nombre);
        spRol = vista.findViewById(R.id.sp_rol);
        spJornada = vista.findViewById(R.id.sp_jornada);
        cbJava = vista.findViewById(R.id.cb_java);
        cbAndroid = vista.findViewById(R.id.cb_android);
        cbWeb = vista.findViewById(R.id.cb_web);
        cbIa = vista.findViewById(R.id.cb_ia);
        cbDiseno = vista.findViewById(R.id.cb_diseno);
        cbOtros = vista.findViewById(R.id.cb_otros);
        rgGenero = vista.findViewById(R.id.rg_genero);

        MaterialButton btnAgregar = vista.findViewById(R.id.btn_agregar);

        poblarSpinner(spRol, R.array.roles_array);
        poblarSpinner(spJornada, R.array.jornadas_array);

        etNombre.setOnFocusChangeListener((v, tieneFoco) -> {
            if (tieneFoco) tilNombre.setError(null);
        });

        btnAgregar.setOnClickListener(v -> agregarIntegrante());
    }

    /** Las opciones viven en strings.xml, no incrustadas en el código. */
    private void poblarSpinner(@NonNull Spinner spinner, int idArray) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), idArray, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void agregarIntegrante() {
        String nombre = etNombre.getText() == null ? "" : etNombre.getText().toString().trim();

        // Antes, un nombre vacío se rellenaba con «Diego Gonzalez» en vez de
        // avisar al usuario.
        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.error_nombre_vacio));
            etNombre.requestFocus();
            return;
        }

        if (nombre.length() < MINIMO_NOMBRE) {
            tilNombre.setError(getString(R.string.error_nombre_corto));
            etNombre.requestFocus();
            return;
        }

        tilNombre.setError(null);

        Integrante nuevo = new Integrante(
                nombre,
                seleccionDe(spRol),
                tecnologiasSeleccionadas(),
                generoSeleccionado(),
                seleccionDe(spJornada),
                Integrante.SIN_FOTO,
                0f
        );

        IntegranteRepository.agregar(nuevo);

        Toast.makeText(
                requireContext(),
                getString(R.string.exito_integrante, nombre),
                Toast.LENGTH_SHORT
        ).show();

        limpiarFormulario();
    }

    @NonNull
    private String seleccionDe(@NonNull Spinner spinner) {
        Object seleccion = spinner.getSelectedItem();
        return seleccion != null ? seleccion.toString() : "";
    }

    @NonNull
    private String tecnologiasSeleccionadas() {
        StringBuilder tecnologias = new StringBuilder();

        agregarSi(tecnologias, cbJava, R.string.opcion_java);
        agregarSi(tecnologias, cbAndroid, R.string.opcion_android);
        agregarSi(tecnologias, cbWeb, R.string.opcion_web);
        agregarSi(tecnologias, cbIa, R.string.opcion_ia);
        agregarSi(tecnologias, cbDiseno, R.string.opcion_diseno);
        agregarSi(tecnologias, cbOtros, R.string.opcion_otros);

        return tecnologias.length() == 0
                ? getString(R.string.sin_tecnologias)
                : tecnologias.toString();
    }

    private void agregarSi(@NonNull StringBuilder destino, @NonNull CheckBox casilla, int idTexto) {
        if (!casilla.isChecked()) {
            return;
        }

        if (destino.length() > 0) {
            destino.append(", ");
        }

        destino.append(getString(idTexto));
    }

    @NonNull
    private String generoSeleccionado() {
        int seleccionado = rgGenero.getCheckedRadioButtonId();

        if (seleccionado == R.id.rb_masculino) {
            return getString(R.string.genero_masculino);
        }
        if (seleccionado == R.id.rb_femenino) {
            return getString(R.string.genero_femenino);
        }
        if (seleccionado == R.id.rb_otro) {
            return getString(R.string.genero_otro);
        }

        return getString(R.string.detalle_sin_dato);
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        spRol.setSelection(0);
        spJornada.setSelection(0);

        cbJava.setChecked(false);
        cbAndroid.setChecked(false);
        cbWeb.setChecked(false);
        cbIa.setChecked(false);
        cbDiseno.setChecked(false);
        cbOtros.setChecked(false);

        rgGenero.clearCheck();
        etNombre.requestFocus();
    }
}
