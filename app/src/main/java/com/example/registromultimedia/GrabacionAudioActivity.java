package com.example.registromultimedia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Grabadora de notas de voz.
 *
 * <p>Se llamaba {@code activity_grabacion_audio}: un nombre de fichero usado
 * como nombre de clase. Al renombrarla se corrigieron además varios problemas
 * de ciclo de vida:</p>
 *
 * <ul>
 *     <li>si {@code stop()} fallaba, la grabadora quedaba sin liberar y el
 *     botón de grabar deshabilitado para siempre;</li>
 *     <li>al salir de la pantalla la grabación seguía en marcha;</li>
 *     <li>tras una segunda grabación el reproductor seguía apuntando a la
 *     primera, porque solo se creaba cuando era {@code null}.</li>
 * </ul>
 */
public class GrabacionAudioActivity extends AppCompatActivity {

    /** Cada cuánto se refresca el contador de reproducción. */
    private static final long REFRESCO_MS = 250L;

    private ImageButton btnGrabar, btnDetener, btnGuardar, btnPlay;
    private MaterialButton btnReproducir;
    private SeekBar seekBar;
    private TextView txtTiempo, txtEstado, txtEstadoDetalle;

    @Nullable
    private MediaRecorder grabador;
    @Nullable
    private MediaPlayer reproductor;

    /** Fichero de la grabación actual; en caché hasta que se guarda. */
    @Nullable
    private File audioActual;

    private boolean grabando = false;
    private boolean audioGuardado = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String> permisoMicrofono =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    concedido -> {
                        if (concedido) {
                            iniciarGrabacion();
                        } else {
                            avisar(R.string.permiso_microfono);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabacion_audio);

        MaterialToolbar toolbar = findViewById(R.id.toolbarGrabacion);
        btnGrabar = findViewById(R.id.imgbtnGrabar);
        btnDetener = findViewById(R.id.imgbtnDetener);
        btnGuardar = findViewById(R.id.imgbtnGuardar);
        btnPlay = findViewById(R.id.imgbtnReproducir);
        btnReproducir = findViewById(R.id.btnReproducirAudio);
        seekBar = findViewById(R.id.seekReproductor);
        txtTiempo = findViewById(R.id.txtTiempo);
        txtEstado = findViewById(R.id.txtEstado);
        txtEstadoDetalle = findViewById(R.id.txtEstadoDetalle);

        toolbar.setNavigationOnClickListener(v -> finish());

        btnGrabar.setOnClickListener(v -> comprobarPermiso());
        btnDetener.setOnClickListener(v -> detenerGrabacion());
        btnGuardar.setOnClickListener(v -> guardarAudio());
        btnPlay.setOnClickListener(v -> alternarReproduccion());
        btnReproducir.setOnClickListener(v -> alternarReproduccion());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar barra, int progreso, boolean deUsuario) {
                if (deUsuario && reproductor != null) {
                    reproductor.seekTo(progreso);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar barra) { }

            @Override
            public void onStopTrackingTouch(SeekBar barra) { }
        });

        actualizarBotones();
    }

    // ==================== PERMISOS ====================

    private void comprobarPermiso() {
        boolean concedido = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;

        if (concedido) {
            iniciarGrabacion();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            avisar(R.string.permiso_microfono);
            permisoMicrofono.launch(Manifest.permission.RECORD_AUDIO);
        } else {
            permisoMicrofono.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    // ==================== GRABACIÓN ====================

    private void iniciarGrabacion() {
        // Una grabación nueva invalida al reproductor: sin esto seguía
        // sonando la grabación anterior.
        liberarReproductor();

        File destino = new File(getCacheDir(), "grabacion_temporal" + Almacenamiento.EXTENSION);

        try {
            grabador = new MediaRecorder(this);
            grabador.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabador.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            grabador.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            grabador.setAudioEncodingBitRate(128_000);
            grabador.setAudioSamplingRate(44_100);
            grabador.setOutputFile(destino.getAbsolutePath());

            grabador.prepare();
            grabador.start();

            audioActual = destino;
            audioGuardado = false;
            grabando = true;

            actualizarBotones();
            mostrarEstado(R.string.grabacion_en_curso, R.string.grabacion_en_curso_detalle);

        } catch (IOException | RuntimeException e) {
            // IllegalStateException también entra aquí: es una RuntimeException.
            // Sin este release la grabadora quedaba retenida tras un fallo.
            liberarGrabador();
            grabando = false;
            audioActual = null;

            actualizarBotones();
            mostrarEstado(R.string.grabacion_listo, R.string.grabacion_listo_detalle);
            avisar(R.string.error_grabar);
        }
    }

    private void detenerGrabacion() {
        if (grabador == null) {
            return;
        }

        boolean correcta = true;

        try {
            grabador.stop();
        } catch (RuntimeException e) {
            // stop() lanza excepción si la grabación fue demasiado corta:
            // el fichero queda inservible y hay que descartarlo.
            correcta = false;
        } finally {
            liberarGrabador();
            grabando = false;
        }

        if (!correcta) {
            borrarSiExiste(audioActual);
            audioActual = null;

            actualizarBotones();
            mostrarEstado(R.string.grabacion_listo, R.string.grabacion_listo_detalle);
            avisar(R.string.error_grabacion_corta);
            return;
        }

        actualizarBotones();
        mostrarEstado(R.string.grabacion_terminada, R.string.grabacion_terminada_detalle);
        cargarDuracion();
    }

    private void guardarAudio() {
        if (audioActual == null || audioGuardado) {
            return;
        }

        File destino = Almacenamiento.nuevoArchivo(this);

        try (FileInputStream entrada = new FileInputStream(audioActual);
             FileOutputStream salida = new FileOutputStream(destino)) {

            byte[] buffer = new byte[8192];
            int leidos;

            while ((leidos = entrada.read(buffer)) > 0) {
                salida.write(buffer, 0, leidos);
            }

        } catch (IOException e) {
            borrarSiExiste(destino);
            avisar(R.string.error_guardar);
            return;
        }

        // El reproductor apuntaba al fichero de caché: se recrea sobre el
        // definitivo para que siga funcionando tras guardar.
        liberarReproductor();
        audioActual = destino;
        audioGuardado = true;

        actualizarBotones();
        avisar(R.string.exito_guardar);
    }

    // ==================== REPRODUCCIÓN ====================

    private void alternarReproduccion() {
        if (audioActual == null || grabando) {
            return;
        }

        if (reproductor == null && !prepararReproductor()) {
            return;
        }

        if (reproductor == null) {
            return;
        }

        if (reproductor.isPlaying()) {
            reproductor.pause();
            handler.removeCallbacks(actualizarTiempo);
            mostrarIconoReproduccion(false);
        } else {
            reproductor.start();
            handler.post(actualizarTiempo);
            mostrarIconoReproduccion(true);
        }
    }

    private boolean prepararReproductor() {
        if (audioActual == null) {
            return false;
        }

        try {
            MediaPlayer nuevo = new MediaPlayer();
            nuevo.setDataSource(audioActual.getAbsolutePath());
            nuevo.prepare();

            nuevo.setOnCompletionListener(mp -> {
                seekBar.setProgress(0);
                handler.removeCallbacks(actualizarTiempo);
                mostrarIconoReproduccion(false);
                txtTiempo.setText(getString(
                        R.string.tiempo_formato, formatoTiempo(0), formatoTiempo(mp.getDuration())));
            });

            reproductor = nuevo;
            seekBar.setMax(nuevo.getDuration());
            return true;

        } catch (IOException | IllegalStateException e) {
            liberarReproductor();
            avisar(R.string.error_reproducir);
            return false;
        }
    }

    private void mostrarIconoReproduccion(boolean sonando) {
        btnPlay.setImageResource(sonando ? R.drawable.ic_pausa : R.drawable.ic_reproducir);
        btnReproducir.setText(sonando ? R.string.accion_pausar : R.string.accion_reproducir);
        btnReproducir.setIconResource(sonando ? R.drawable.ic_pausa : R.drawable.ic_reproducir);
    }

    private final Runnable actualizarTiempo = new Runnable() {
        @Override
        public void run() {
            MediaPlayer actual = reproductor;

            if (actual == null || !actual.isPlaying()) {
                return;
            }

            seekBar.setProgress(actual.getCurrentPosition());
            txtTiempo.setText(getString(
                    R.string.tiempo_formato,
                    formatoTiempo(actual.getCurrentPosition()),
                    formatoTiempo(actual.getDuration())));

            handler.postDelayed(this, REFRESCO_MS);
        }
    };

    private void cargarDuracion() {
        if (audioActual == null) {
            return;
        }

        MediaPlayer sonda = MediaPlayer.create(this, Uri.fromFile(audioActual));

        if (sonda == null) {
            return;
        }

        seekBar.setMax(sonda.getDuration());
        seekBar.setProgress(0);
        txtTiempo.setText(getString(
                R.string.tiempo_formato, formatoTiempo(0), formatoTiempo(sonda.getDuration())));

        sonda.release();
    }

    // ==================== ESTADO DE LA INTERFAZ ====================

    private void actualizarBotones() {
        boolean hayAudio = audioActual != null;

        btnGrabar.setEnabled(!grabando);
        btnDetener.setEnabled(grabando);
        btnGuardar.setEnabled(hayAudio && !grabando && !audioGuardado);
        btnPlay.setEnabled(hayAudio && !grabando);
        btnReproducir.setEnabled(hayAudio && !grabando);

        // Un botón circular deshabilitado se atenúa para que se note.
        btnGrabar.setAlpha(btnGrabar.isEnabled() ? 1f : 0.4f);
        btnDetener.setAlpha(btnDetener.isEnabled() ? 1f : 0.4f);
        btnGuardar.setAlpha(btnGuardar.isEnabled() ? 1f : 0.4f);
        btnPlay.setAlpha(btnPlay.isEnabled() ? 1f : 0.4f);
    }

    private void mostrarEstado(@StringRes int titulo, @StringRes int detalle) {
        txtEstado.setText(titulo);
        txtEstadoDetalle.setText(detalle);
    }

    // ==================== CICLO DE VIDA ====================

    @Override
    protected void onPause() {
        super.onPause();

        // Salir de la pantalla cierra la grabación en curso: antes seguía
        // capturando audio en segundo plano.
        if (grabando) {
            detenerGrabacion();
        }

        if (reproductor != null && reproductor.isPlaying()) {
            reproductor.pause();
            handler.removeCallbacks(actualizarTiempo);
            mostrarIconoReproduccion(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(actualizarTiempo);
        liberarReproductor();
        liberarGrabador();
    }

    private void liberarGrabador() {
        if (grabador == null) {
            return;
        }

        try {
            grabador.release();
        } catch (RuntimeException ignorada) {
            // Nada que hacer: el objeto se descarta igualmente.
        }

        grabador = null;
    }

    private void liberarReproductor() {
        handler.removeCallbacks(actualizarTiempo);

        if (reproductor == null) {
            return;
        }

        try {
            reproductor.release();
        } catch (RuntimeException ignorada) {
            // Nada que hacer: el objeto se descarta igualmente.
        }

        reproductor = null;
        seekBar.setProgress(0);
        mostrarIconoReproduccion(false);
    }

    private void borrarSiExiste(@Nullable File archivo) {
        if (archivo != null && archivo.exists() && !archivo.delete()) {
            // Un temporal que no se puede borrar no impide seguir usando la app.
            archivo.deleteOnExit();
        }
    }

    private void avisar(@StringRes int mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private String formatoTiempo(int milisegundos) {
        int segundos = Math.max(0, milisegundos) / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d", segundos / 60, segundos % 60);
    }
}
