package com.example.registromultimedia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnVolverGrabadora;
    private Button btnAgregar;
    private Button btnRegister;
    private EditText etNombre, etComentarios;
    private Spinner spRol, spNivel;
    private RatingBar rbValoracion;
    private ProgressBar pbProgreso;
    private TextView tvProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vinculación de vistas usando findViewById
        btnVolverGrabadora = findViewById(R.id.btnVolverGrabadora);
        btnAgregar = findViewById(R.id.btn_agregar);
        btnRegister = findViewById(R.id.btnRegister);
        etNombre = findViewById(R.id.et_nombre);
        etComentarios = findViewById(R.id.et_comentarios);
        spRol = findViewById(R.id.sp_rol);
        spNivel = findViewById(R.id.sp_nivel);
        rbValoracion = findViewById(R.id.rb_valoracion);
        pbProgreso = findViewById(R.id.pb_progreso);
        tvProgreso = findViewById(R.id.tv_progreso);

        // Evento para el botón Volver a Grabadora
        btnVolverGrabadora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la pantalla actual y vuelve a la anterior
            }
        });

        // Evento para Agregar Integrante
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString().trim();
                if (nombre.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor ingresa un nombre", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Integrante agregado: " + nombre, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}