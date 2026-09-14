package com.example.registromultimedia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    CheckBox checkJava, checkPython, checkAndroid;
    RadioGroup grupoJornada;
    Spinner spinnerCarreras;
    RatingBar ratingBar;
    ProgressBar progressBar;
    TextView txtProgreso, txtResultado;
    EditText editNombre;
    Button btnMostrar, btnAumentar, btnGrabar, btnDetener, btnReproducir;
    RecyclerView recyclerEstudiantes;

    ArrayList<Estudiante> listaEstudiantes;
    EstudianteAdapter adapter;

    int progreso = 30;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String rutaAudio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Solicitar permisos de audio en tiempo de ejecución
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        // Conectar componentes
        checkJava = findViewById(R.id.checkJava);
        checkPython = findViewById(R.id.checkPython);
        checkAndroid = findViewById(R.id.checkAndroid);
        grupoJornada = findViewById(R.id.grupoJornada);
        spinnerCarreras = findViewById(R.id.spinnerCarreras);
        ratingBar = findViewById(R.id.ratingBar);
        progressBar = findViewById(R.id.progressBar);
        txtProgreso = findViewById(R.id.txtProgreso);
        txtResultado = findViewById(R.id.txtResultado);
        editNombre = findViewById(R.id.editNombre);
        btnMostrar = findViewById(R.id.btnMostrar);
        btnAumentar = findViewById(R.id.btnAumentar);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnDetener = findViewById(R.id.btnDetener);
        btnReproducir = findViewById(R.id.btnReproducir);
        recyclerEstudiantes = findViewById(R.id.recyclerEstudiantes);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                this,
                R.array.carreras_array,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarreras.setAdapter(adapterSpinner);

        // Configurar ruta del archivo de audio (.m4a AAC)
        File directorio = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File archivo = new File(directorio, "presentacion_equipo.m4a");
        rutaAudio = archivo.getAbsolutePath();

        // Listeners de Audio
        btnGrabar.setOnClickListener(v -> iniciarGrabacion());
        btnDetener.setOnClickListener(v -> detenerGrabacion());
        btnReproducir.setOnClickListener(v -> reproducirAudio());

        // Botón ProgressBar
        btnAumentar.setOnClickListener(view -> {
            progreso += 10;
            if (progreso > 100) progreso = 0;
            progressBar.setProgress(progreso);
            txtProgreso.setText("Progreso: " + progreso + "%");
        });

        // Configurar RecyclerView inicial
        cargarEstudiantes();

        // Botón Registrar/Mostrar
        btnMostrar.setOnClickListener(view -> mostrarYAgregarDatos());

        // =========================================================================
        // codigo añadido por: Seppel Krahl
        // tarea: conexión con intent hacia la pantalla de registro
        // =========================================================================
        Button btnIrRegistro = findViewById(R.id.btnRegister); // Verifica que el ID sea correcto según tu XML
        if (btnIrRegistro != null) {
            btnIrRegistro.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
        // =========================================================================
    }

    private void iniciarGrabacion() {
        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(rutaAudio);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Grabando audio...", Toast.LENGTH_SHORT).show();
            btnGrabar.setEnabled(false);
            btnDetener.setEnabled(true);
            btnReproducir.setEnabled(false);
        } catch (IOException e) {
            Toast.makeText(this, "Error al grabar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerGrabacion() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Toast.makeText(this, "Audio guardado correctamente", Toast.LENGTH_SHORT).show();
                btnGrabar.setEnabled(true);
                btnDetener.setEnabled(false);
                btnReproducir.setEnabled(true);
            } catch (Exception e) {
                Toast.makeText(this, "Error al detener", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reproducirAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(rutaAudio);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Reproduciendo audio...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "No hay audio grabado para reproducir", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarYAgregarDatos() {
        String nombre = editNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            editNombre.setError("Debe ingresar un nombre");
            return;
        }

        String carrera = spinnerCarreras.getSelectedItem().toString();
        if (carrera.equals("Seleccione carrera")) {
            Toast.makeText(this, "Seleccione una carrera válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String jornada = "No seleccionada";
        int radioSeleccionado = grupoJornada.getCheckedRadioButtonId();
        if (radioSeleccionado != -1) {
            RadioButton rb = findViewById(radioSeleccionado);
            jornada = rb.getText().toString();
        }

        StringBuilder tecnologias = new StringBuilder();
        if (checkJava.isChecked()) tecnologias.append("Java ");
        if (checkPython.isChecked()) tecnologias.append("Python ");
        if (checkAndroid.isChecked()) tecnologias.append("Android ");
        if (tecnologias.length() == 0) tecnologias.append("Ninguna");

        float calificacion = ratingBar.getRating();

        String resultado = "Nombre: " + nombre +
                "\nCarrera: " + carrera +
                "\nJornada: " + jornada +
                "\nTecnologías: " + tecnologias.toString() +
                "\nCalificación: " + calificacion + " ★";
        txtResultado.setText(resultado);

        // Agregar al RecyclerView dinámicamente
        Estudiante nuevoEstudiante = new Estudiante(nombre, carrera);
        listaEstudiantes.add(nuevoEstudiante);
        adapter.notifyItemInserted(listaEstudiantes.size() - 1);
        recyclerEstudiantes.scrollToPosition(listaEstudiantes.size() - 1);

        editNombre.setText("");
        Toast.makeText(this, "¡Agregado al listado!", Toast.LENGTH_SHORT).show();
    }

    private void cargarEstudiantes() {
        listaEstudiantes = new ArrayList<>();
        listaEstudiantes.add(new Estudiante("Juan Pérez", "Ingeniería en Informática"));
        listaEstudiantes.add(new Estudiante("Ana González", "Analista Programador"));

        recyclerEstudiantes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EstudianteAdapter(listaEstudiantes);
        recyclerEstudiantes.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "Se requiere permiso de micrófono obligatoriamente", Toast.LENGTH_LONG).show();
        }
    }
}
