package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Menú lateral - Grupo 7 punto 2
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;

    // Formulario - Grupo 3
    EditText etNombre;
    Spinner spRol;
    CheckBox cbJava, cbAndroid, cbWeb, cbIa, cbDiseno, cbOtros;
    RadioGroup rgGenero;

    // Progreso y valoración - Grupo 4
    Spinner spNivel;
    RatingBar rbValoracion;
    EditText etComentarios;
    TextView tvProgreso;
    ProgressBar pbProgreso;

    Button btnAgregar, btnRegister;
    RecyclerView recyclerIntegrantes;

    ArrayList<Integrante> listaIntegrantes;
    IntegranteAdapter adapter;

    private boolean nivelSeleccionado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menú lateral
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Registro Multimedia");
        }

        etNombre = findViewById(R.id.et_nombre);
        spRol = findViewById(R.id.sp_rol);
        cbJava = findViewById(R.id.cb_java);
        cbAndroid = findViewById(R.id.cb_android);
        cbWeb = findViewById(R.id.cb_web);
        cbIa = findViewById(R.id.cb_ia);
        cbDiseno = findViewById(R.id.cb_diseno);
        cbOtros = findViewById(R.id.cb_otros);
        rgGenero = findViewById(R.id.rg_genero);

        spNivel = findViewById(R.id.sp_nivel);
        rbValoracion = findViewById(R.id.rb_valoracion);
        etComentarios = findViewById(R.id.et_comentarios);
        tvProgreso = findViewById(R.id.tv_progreso);
        pbProgreso = findViewById(R.id.pb_progreso);

        btnAgregar = findViewById(R.id.btn_agregar);
        btnRegister = findViewById(R.id.btnRegister);
        recyclerIntegrantes = findViewById(R.id.recyclerIntegrantes);

        cargarIntegrantes();
        configurarRoles();
        configurarNiveles();
        configurarListeners();
        configurarMenuLateral();
    }

    private void configurarRoles() {
        String[] roles = {"Desarrollador", "Diseñador", "Analista", "Tester"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spRol.setAdapter(adapterRoles);
    }

    private void configurarNiveles() {
        String[] niveles = {"Principiante", "Intermedio", "Avanzado", "Experto"};
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, niveles);
        spNivel.setAdapter(adapterNiveles);

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

    private void configurarListeners() {
        rbValoracion.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> actualizarProgreso());

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

        btnAgregar.setOnClickListener(v -> agregarIntegrante());

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        }
    }

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

    private void agregarIntegrante() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("Debe ingresar un nombre");
            return;
        }

        String rol = spRol.getSelectedItem() != null ? spRol.getSelectedItem().toString() : "";

        StringBuilder tecnologias = new StringBuilder();
        if (cbJava.isChecked()) tecnologias.append("Java ");
        if (cbAndroid.isChecked()) tecnologias.append("Android ");
        if (cbWeb.isChecked()) tecnologias.append("Web ");
        if (cbIa.isChecked()) tecnologias.append("IA ");
        if (cbDiseno.isChecked()) tecnologias.append("Diseño ");
        if (cbOtros.isChecked()) tecnologias.append("Otros ");
        if (tecnologias.length() == 0) tecnologias.append("Ninguna");

        String genero = "No especificado";
        int seleccionado = rgGenero.getCheckedRadioButtonId();
        if (seleccionado == R.id.rb_masculino) genero = "Masculino";
        else if (seleccionado == R.id.rb_femenino) genero = "Femenino";
        else if (seleccionado == R.id.rb_otro) genero = "Otro";

        float valoracion = rbValoracion.getRating();

        Integrante nuevoIntegrante = new Integrante(nombre, rol, tecnologias.toString(), genero, valoracion);
        listaIntegrantes.add(nuevoIntegrante);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Integrante agregado", Toast.LENGTH_SHORT).show();

        etNombre.setText("");
        cbJava.setChecked(false);
        cbAndroid.setChecked(false);
        cbWeb.setChecked(false);
        cbIa.setChecked(false);
        cbDiseno.setChecked(false);
        cbOtros.setChecked(false);
        rgGenero.clearCheck();
        etComentarios.setText("");
        rbValoracion.setRating(0);
        spNivel.setSelection(0);
        nivelSeleccionado = false;
        actualizarProgreso();
    }

    private void cargarIntegrantes() {
        listaIntegrantes = new ArrayList<>();
        listaIntegrantes.add(new Integrante("Lucas Bilbao", "Desarrollador", "Java, Android", "No especificado", R.drawable.lucas, 5.0f));
        listaIntegrantes.add(new Integrante("Bruno Antio", "Analista", "Python, SQL", "No especificado", R.drawable.bruno, 3.0f));
        listaIntegrantes.add(new Integrante("Anghel Lopez", "Diseñador", "Figma, UX", "No especificado", R.drawable.anghel, 4.0f));

        recyclerIntegrantes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IntegranteAdapter(listaIntegrantes);
        recyclerIntegrantes.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void configurarMenuLateral() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.abrir_menu,
                R.string.cerrar_menu
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_formulario) {
                // Ya estamos en esta pantalla
            } else if (id == R.id.menu_grabar_audio) {
                startActivity(new Intent(MainActivity.this, activity_grabacion_audio.class));
            } else if (id == R.id.menu_reproducir_audio) {
                startActivity(new Intent(MainActivity.this, activity_reproductor_audio.class));
            } else if (id == R.id.menu_cerrar_sesion) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}