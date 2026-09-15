package com.example.registromultimedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;

// Clase para controlar el diseño de la interfaz de registro en el sistema
// Estructurado por Fernando Zamorano y Rodrigo Toledo
public class FormularioFragment extends Fragment {

    private ImageView ivAvatar;
    private EditText etNombre;
    private Spinner spRol;
    private CheckBox cbJava, cbAndroid, cbWeb, cbIa, cbDiseno, cbOtros;
    private RadioGroup rgGenero;
    private Button btnAgregar;

    // Inicializa la vista y enlazalos componentes
    // Metodo hecho por Rodrigo Toledou
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_formulario, container, false);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        etNombre = view.findViewById(R.id.et_nombre);
        spRol = view.findViewById(R.id.sp_rol);
        cbJava = view.findViewById(R.id.cb_java);
        cbAndroid = view.findViewById(R.id.cb_android);
        cbWeb = view.findViewById(R.id.cb_web);
        cbIa = view.findViewById(R.id.cb_ia);
        cbDiseno = view.findViewById(R.id.cb_diseno);
        cbOtros = view.findViewById(R.id.cb_otros);
        rgGenero = view.findViewById(R.id.rg_genero);
        btnAgregar = view.findViewById(R.id.btn_agregar);

        configurarRoles();
        configurarAccionAgregar();

        return view;
    }

    // PObla la lista de opciones para los cargos
    // cReado por Fernando Zamorano
    private void configurarRoles() {
        String[] roles = {"Desarrollador", "Diseñador", "Analista", "Tester"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles);
        spRol.setAdapter(adapter);
    }

    // Recopila datos y genera la nueva entidad en el sistema
    // Validaciones y captura de datos hechas por Rodrigo Toledou
    private void configurarAccionAgregar() {
        btnAgregar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String rol = spRol.getSelectedItem().toString();

            if (nombre.isEmpty()) {
                nombre = "Diego Gonzalez";
            }

            StringBuilder tecnologias = new StringBuilder();
            if (cbJava.isChecked()) tecnologias.append("Java ");
            if (cbAndroid.isChecked()) tecnologias.append("Android ");
            if (cbWeb.isChecked()) tecnologias.append("Web ");
            if (cbIa.isChecked()) tecnologias.append("IA ");
            if (cbDiseno.isChecked()) tecnologias.append("Diseño ");
            if (cbOtros.isChecked()) tecnologias.append("Otros ");

            String genero = "";
            int selectedId = rgGenero.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_masculino) {
                genero = "Masculino";
            } else if (selectedId == R.id.rb_femenino) {
                genero = "Femenino";
            } else if (selectedId == R.id.rb_otro) {
                genero = "Otro";
            }

            // Genera el estudiante utilizand el modelo del otro grupo
            // Hecho por Fernando Zamorano
            Estudiante nuevoEstudiante = new Estudiante(nombre, rol);

            etNombre.setText("");
            cbJava.setChecked(false);
            cbAndroid.setChecked(false);
            cbWeb.setChecked(false);
            cbIa.setChecked(false);
            cbDiseno.setChecked(false);
            cbOtros.setChecked(false);
            rgGenero.clearCheck();
        });
    }
}