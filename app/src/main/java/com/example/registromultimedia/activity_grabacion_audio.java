package com.example.registromultimedia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class activity_grabacion_audio extends AppCompatActivity {

    private ImageButton btnGrabar, btnDetener, btnGuardar, btnPlay;
    private ImageView btnVolver;
    private Button btnReproducir;
    private SeekBar seekBar;
    private TextView txtTiempo;

    private MediaRecorder grabador;
    private MediaPlayer reproductor;

    private File audioTemporal;
    private File audioActual;

    private final Handler handler = new Handler();

    private final ActivityResultLauncher<String> permisoMicrofono =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    permitido -> {
                        if (permitido) iniciarGrabacion();
                        else Toast.makeText(
                                this,
                                "Debes permitir el micrófono",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabacion_audio);

        btnVolver = findViewById(R.id.btnVolver);
        btnGrabar = findViewById(R.id.imgbtnGrabar);
        btnDetener = findViewById(R.id.imgbtnDetener);
        btnGuardar = findViewById(R.id.imgbtnGuardar);
        btnPlay = findViewById(R.id.imgbtnReproducir);
        btnReproducir = findViewById(R.id.btnReproducirAudio);
        seekBar = findViewById(R.id.seekReproductor);
        txtTiempo = findViewById(R.id.txtTiempo);

        btnDetener.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnPlay.setEnabled(false);
        btnReproducir.setEnabled(false);

        btnVolver.setOnClickListener(v -> finish());

        btnGrabar.setOnClickListener(v -> comprobarPermiso());

        btnDetener.setOnClickListener(v -> detenerGrabacion());

        btnGuardar.setOnClickListener(v -> guardarAudio());

        btnPlay.setOnClickListener(v -> reproducir());

        btnReproducir.setOnClickListener(v -> reproducir());

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean usuario
                    ) {
                        if (usuario && reproductor != null) {
                            reproductor.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                }
        );
    }

    private void comprobarPermiso() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED) {

            iniciarGrabacion();

        } else {

            permisoMicrofono.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void iniciarGrabacion() {

        audioTemporal = new File(
                getCacheDir(),
                "grabacion.m4a"
        );

        try {

            grabador = new MediaRecorder(this);

            grabador.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabador.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            grabador.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            grabador.setOutputFile(audioTemporal.getAbsolutePath());

            grabador.prepare();
            grabador.start();

            btnGrabar.setEnabled(false);
            btnDetener.setEnabled(true);
            btnGuardar.setEnabled(false);

            Toast.makeText(
                    this,
                    "Grabando...",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Error al grabar",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void detenerGrabacion() {

        if (grabador == null) return;

        try {

            grabador.stop();
            grabador.release();
            grabador = null;

            audioActual = audioTemporal;

            btnGrabar.setEnabled(true);
            btnDetener.setEnabled(false);
            btnGuardar.setEnabled(true);
            btnPlay.setEnabled(true);
            btnReproducir.setEnabled(true);

            cargarDuracion();

            Toast.makeText(
                    this,
                    "Grabación detenida",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (RuntimeException e) {

            Toast.makeText(
                    this,
                    "La grabación fue demasiado corta",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void guardarAudio() {

        if (audioActual == null) return;

        File carpeta = new File(
                getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "Grabaciones"
        );

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        File archivo = new File(
                carpeta,
                "audio_" + System.currentTimeMillis() + ".m4a"
        );

        try (
                FileInputStream entrada =
                        new FileInputStream(audioActual);

                FileOutputStream salida =
                        new FileOutputStream(archivo)
        ) {

            byte[] buffer = new byte[1024];
            int cantidad;

            while ((cantidad = entrada.read(buffer)) > 0) {
                salida.write(buffer, 0, cantidad);
            }

            audioActual = archivo;
            btnGuardar.setEnabled(false);

            Toast.makeText(
                    this,
                    "Audio guardado",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Error al guardar",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void reproducir() {

        if (audioActual == null) return;

        try {

            if (reproductor == null) {

                reproductor = new MediaPlayer();
                reproductor.setDataSource(audioActual.getAbsolutePath());
                reproductor.prepare();

                seekBar.setMax(reproductor.getDuration());

                reproductor.setOnCompletionListener(mp -> {

                    seekBar.setProgress(0);

                    btnPlay.setImageResource(
                            android.R.drawable.ic_media_play
                    );

                    btnReproducir.setText("Reproducir audio");

                    handler.removeCallbacks(actualizarTiempo);
                });
            }

            if (reproductor.isPlaying()) {

                reproductor.pause();

                btnPlay.setImageResource(
                        android.R.drawable.ic_media_play
                );

                btnReproducir.setText("Reproducir audio");

            } else {

                reproductor.start();

                btnPlay.setImageResource(
                        android.R.drawable.ic_media_pause
                );

                btnReproducir.setText("Pausar audio");

                handler.post(actualizarTiempo);
            }

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Error al reproducir",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private final Runnable actualizarTiempo = new Runnable() {

        @Override
        public void run() {

            if (reproductor != null && reproductor.isPlaying()) {

                int actual = reproductor.getCurrentPosition();
                int total = reproductor.getDuration();

                seekBar.setProgress(actual);

                txtTiempo.setText(
                        formatoTiempo(actual)
                                + " / "
                                + formatoTiempo(total)
                );

                handler.postDelayed(this, 500);
            }
        }
    };

    private void cargarDuracion() {

        MediaPlayer audio = MediaPlayer.create(
                this,
                android.net.Uri.fromFile(audioActual)
        );

        if (audio != null) {

            seekBar.setMax(audio.getDuration());

            txtTiempo.setText(
                    "00:00 / " +
                            formatoTiempo(audio.getDuration())
            );

            audio.release();
        }
    }

    private String formatoTiempo(int milisegundos) {

        int segundos = milisegundos / 1000;

        return String.format(
                "%02d:%02d",
                segundos / 60,
                segundos % 60
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(actualizarTiempo);

        if (reproductor != null) {
            reproductor.release();
        }

        if (grabador != null) {
            grabador.release();
        }
    }
}