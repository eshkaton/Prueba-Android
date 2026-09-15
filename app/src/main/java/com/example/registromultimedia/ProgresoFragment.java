package com.example.registromultimedia;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

// Clase para controlar el progreso y la valoración del integrante
// Estructurado por Juan Carlos Levin
public class ProgresoFragment extends Fragment {

    private Spinner spNivel;
    private RatingBar rbValoracion;
    private EditText etComentarios;
    private ProgressBar pbProgreso;
    private TextView tvProgreso;
    private Button btnGuardar;

    private boolean nivelSeleccionado = false;

    // Inicializa la vista y enlaza los componentes
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progreso, container, false);

        spNivel = view.findViewById(R.id.sp_nivel);
        rbValoracion = view.findViewById(R.id.rb_valoracion);
        etComentarios = view.findViewById(R.id.et_comentarios);
        pbProgreso = view.findViewById(R.id.pb_progreso);
        tvProgreso = view.findViewById(R.id.tv_progreso);
        btnGuardar = view.findViewById(R.id.btn_guardar);

        configurarNiveles();
        configurarListeners();

        return view;
    }

    // Puebla la lista de opciones para el nivel de experiencia
    private void configurarNiveles() {
        String[] niveles = {"Principiante", "Intermedio", "Avanzado", "Experto"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, niveles);
        spNivel.setAdapter(adapter);

        spNivel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nivelSeleccionado = true;
                actualizarProgreso();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                nivelSeleccionado = false;
                actualizarProgreso();
            }
        });
    }

    // Configura los listeners del rating, comentarios y boton guardar
    private void configurarListeners() {

        // Listener del RatingBar que obtiene la cantidad de estrellas seleccionadas
        rbValoracion.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> actualizarProgreso());

        // Escucha los cambios en el campo de comentarios para el progreso en vivo
        etComentarios.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarProgreso();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnGuardar.setOnClickListener(v -> guardarValoracion());
    }

    // Calcula el avance del ProgressBar segun los campos completados
    private void actualizarProgreso() {
        int camposCompletados = 0;
        int totalCampos = 3;

        if (nivelSeleccionado) camposCompletados++;
        if (rbValoracion.getRating() > 0) camposCompletados++;
        if (!etComentarios.getText().toString().trim().isEmpty()) camposCompletados++;

        int porcentaje = (camposCompletados * 100) / totalCampos;
        pbProgreso.setProgress(porcentaje);
        tvProgreso.setText("Progreso: " + porcentaje + "%");
    }

    // Recopila la valoracion y el nivel de experiencia del integrante
    private void guardarValoracion() {
        String nivel = spNivel.getSelectedItem() != null ? spNivel.getSelectedItem().toString() : "";
        float estrellas = rbValoracion.getRating();
        String comentario = etComentarios.getText().toString();

        // Aqui se integraria con el modelo Integrante ya definido por el equipo
        // para guardar el nivel, la valoración y el comentario del integrante
        String resumen = "Guardado: " + nivel + ", " + (int) estrellas + " estrellas, \"" + comentario + "\"";
        Toast.makeText(requireContext(), resumen, Toast.LENGTH_SHORT).show();

        etComentarios.setText("");
        rbValoracion.setRating(0);
        spNivel.setSelection(0);
        actualizarProgreso();
    }
}