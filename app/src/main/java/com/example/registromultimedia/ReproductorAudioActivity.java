package com.example.registromultimedia;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Biblioteca de grabaciones: lista, reproduce, recorta y elimina audios.
 *
 * <p>Se llamaba {@code activity_reproductor_audio}. Además del renombrado,
 * el listado ya no se construye en el hilo principal: leer la duración de
 * cada fichero con {@link MediaMetadataRetriever} bloqueaba la interfaz y
 * podía provocar un ANR con varias grabaciones.</p>
 */
public class ReproductorAudioActivity extends AppCompatActivity {

    /** Cada cuánto se refresca el contador de reproducción. */
    private static final long REFRESCO_MS = 250L;

    /** Duración mínima que debe conservar un recorte. */
    private static final int MINIMO_RECORTE_MS = 1000;

    private RecyclerView rvAudios;
    private View lyVacio;
    private MaterialCardView lyReproductor;
    private TextView txtNombreSeleccionado, txtFechaSeleccionado, txtTiempoPlayer;
    private SeekBar seekPlayer;
    private ImageButton imgbtnPlayPausePlayer, imgbtnCortar, imgbtnEliminar;

    private final List<AudioItem> audios = new ArrayList<>();
    private AudioAdapter adapter;

    @Nullable
    private MediaPlayer reproductor;
    @Nullable
    private AudioItem audioSeleccionado;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor_audio);

        MaterialToolbar toolbar = findViewById(R.id.toolbarReproductor);
        rvAudios = findViewById(R.id.rvAudios);
        lyVacio = findViewById(R.id.lyVacio);
        lyReproductor = findViewById(R.id.lyReproductorActual);
        txtNombreSeleccionado = findViewById(R.id.txtNombreSeleccionado);
        txtFechaSeleccionado = findViewById(R.id.txtFechaSeleccionado);
        txtTiempoPlayer = findViewById(R.id.txtTiempoPlayer);
        seekPlayer = findViewById(R.id.seekPlayer);
        imgbtnPlayPausePlayer = findViewById(R.id.imgbtnPlayPausePlayer);
        imgbtnCortar = findViewById(R.id.imgbtnCortar);
        imgbtnEliminar = findViewById(R.id.imgbtnEliminar);

        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new AudioAdapter(audios, (item, posicion) -> seleccionarAudio(item));
        rvAudios.setLayoutManager(new LinearLayoutManager(this));
        rvAudios.setAdapter(adapter);
        rvAudios.setHasFixedSize(true);

        imgbtnPlayPausePlayer.setOnClickListener(v -> alternarReproduccion());
        imgbtnCortar.setOnClickListener(v -> mostrarDialogoCortar());
        imgbtnEliminar.setOnClickListener(v -> confirmarEliminar());

        seekPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        limpiarReproductor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaAudios();
    }

    // ==================== LISTADO ====================

    /**
     * Relee la carpeta de grabaciones en segundo plano.
     *
     * <p>El trabajo de disco y de metadatos se hace en el executor; solo se
     * vuelve al hilo principal para pintar.</p>
     */
    private void cargarListaAudios() {
        String rutaAnterior = audioSeleccionado != null
                ? audioSeleccionado.getArchivo().getAbsolutePath()
                : null;

        File carpeta = Almacenamiento.carpetaGrabaciones(this);

        executor.execute(() -> {
            List<AudioItem> encontrados = leerGrabaciones(carpeta);

            if (isFinishing() || isDestroyed()) {
                return;
            }

            runOnUiThread(() -> mostrarLista(encontrados, rutaAnterior));
        });
    }

    @NonNull
    private List<AudioItem> leerGrabaciones(@NonNull File carpeta) {
        List<AudioItem> encontrados = new ArrayList<>();
        File[] archivos = carpeta.isDirectory() ? carpeta.listFiles() : null;

        if (archivos == null) {
            return encontrados;
        }

        // Más recientes primero.
        Arrays.sort(archivos, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        SimpleDateFormat formato =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-ES"));

        for (File archivo : archivos) {
            if (!archivo.getName().endsWith(Almacenamiento.EXTENSION)) {
                continue;
            }

            String fecha = formato.format(new Date(archivo.lastModified()));

            AudioItem item = new AudioItem(archivo);
            item.setNombre(getString(R.string.reproductor_nombre_item, fecha));
            item.setFecha(fecha);
            item.setDuracionMs(obtenerDuracion(archivo));

            encontrados.add(item);
        }

        return encontrados;
    }

    private void mostrarLista(@NonNull List<AudioItem> encontrados, @Nullable String rutaAnterior) {
        audios.clear();
        audios.addAll(encontrados);

        int posicionSeleccionada = AudioAdapter.SIN_SELECCION;

        if (rutaAnterior != null) {
            for (int i = 0; i < audios.size(); i++) {
                if (audios.get(i).getArchivo().getAbsolutePath().equals(rutaAnterior)) {
                    posicionSeleccionada = i;
                    break;
                }
            }
        }

        // El orden importa: se fija la selección y luego se repinta todo.
        // Notificar la posición anterior por separado podía apuntar fuera de
        // rango cuando la lista encogía (por ejemplo, al borrar un audio).
        adapter.fijarSeleccion(posicionSeleccionada);
        adapter.notifyDataSetChanged();

        boolean vacio = audios.isEmpty();
        lyVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        rvAudios.setVisibility(vacio ? View.GONE : View.VISIBLE);
        lyReproductor.setVisibility(vacio ? View.GONE : View.VISIBLE);

        if (posicionSeleccionada == AudioAdapter.SIN_SELECCION) {
            audioSeleccionado = null;
            limpiarReproductor();
            return;
        }

        audioSeleccionado = audios.get(posicionSeleccionada);
        mostrarDatosSeleccionado();
        prepararReproductor();
    }

    private long obtenerDuracion(@NonNull File archivo) {
        MediaMetadataRetriever lector = new MediaMetadataRetriever();

        try {
            lector.setDataSource(archivo.getAbsolutePath());
            String duracion = lector.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return duracion != null ? Long.parseLong(duracion) : 0L;

        } catch (RuntimeException e) {
            return 0L;

        } finally {
            try {
                lector.release();
            } catch (Exception ignorada) {
                // El lector se descarta igualmente.
            }
        }
    }

    private void seleccionarAudio(@NonNull AudioItem item) {
        detenerReproduccion();

        audioSeleccionado = item;
        mostrarDatosSeleccionado();

        if (prepararReproductor()) {
            iniciarReproduccion();
        }
    }

    private void mostrarDatosSeleccionado() {
        if (audioSeleccionado == null) {
            return;
        }

        txtNombreSeleccionado.setText(audioSeleccionado.getNombre());
        txtFechaSeleccionado.setText(audioSeleccionado.getFecha());

        imgbtnPlayPausePlayer.setEnabled(true);
        imgbtnCortar.setEnabled(true);
        imgbtnEliminar.setEnabled(true);
        seekPlayer.setEnabled(true);

        actualizarOpacidadBotones();
    }

    // ==================== REPRODUCCIÓN ====================

    private boolean prepararReproductor() {
        if (audioSeleccionado == null) {
            return false;
        }

        liberarReproductor();

        try {
            MediaPlayer nuevo = new MediaPlayer();
            nuevo.setDataSource(audioSeleccionado.getArchivo().getAbsolutePath());
            nuevo.prepare();

            nuevo.setOnCompletionListener(mp -> {
                seekPlayer.setProgress(0);
                handler.removeCallbacks(actualizarTiempoPlayer);
                mostrarIconoReproduccion(false);
            });

            reproductor = nuevo;

            seekPlayer.setMax(nuevo.getDuration());
            seekPlayer.setProgress(0);
            txtTiempoPlayer.setText(getString(
                    R.string.tiempo_formato, formatoTiempo(0), formatoTiempo(nuevo.getDuration())));

            return true;

        } catch (Exception e) {
            liberarReproductor();
            avisar(R.string.error_reproducir);
            return false;
        }
    }

    private void iniciarReproduccion() {
        if (reproductor == null) {
            return;
        }

        reproductor.start();
        handler.post(actualizarTiempoPlayer);
        mostrarIconoReproduccion(true);
    }

    private void alternarReproduccion() {
        if (reproductor == null && !prepararReproductor()) {
            return;
        }

        if (reproductor == null) {
            return;
        }

        if (reproductor.isPlaying()) {
            reproductor.pause();
            handler.removeCallbacks(actualizarTiempoPlayer);
            mostrarIconoReproduccion(false);
        } else {
            iniciarReproduccion();
        }
    }

    private void detenerReproduccion() {
        handler.removeCallbacks(actualizarTiempoPlayer);
        liberarReproductor();
        mostrarIconoReproduccion(false);
    }

    private void limpiarReproductor() {
        detenerReproduccion();

        audioSeleccionado = null;

        txtNombreSeleccionado.setText(R.string.reproductor_selecciona);
        txtFechaSeleccionado.setText("");
        txtTiempoPlayer.setText(R.string.reproductor_tiempo_inicial);

        seekPlayer.setProgress(0);
        seekPlayer.setEnabled(false);

        imgbtnPlayPausePlayer.setEnabled(false);
        imgbtnCortar.setEnabled(false);
        imgbtnEliminar.setEnabled(false);

        actualizarOpacidadBotones();
    }

    private void mostrarIconoReproduccion(boolean sonando) {
        imgbtnPlayPausePlayer.setImageResource(
                sonando ? R.drawable.ic_pausa : R.drawable.ic_reproducir);
    }

    private void actualizarOpacidadBotones() {
        imgbtnPlayPausePlayer.setAlpha(imgbtnPlayPausePlayer.isEnabled() ? 1f : 0.4f);
        imgbtnCortar.setAlpha(imgbtnCortar.isEnabled() ? 1f : 0.4f);
        imgbtnEliminar.setAlpha(imgbtnEliminar.isEnabled() ? 1f : 0.4f);
    }

    private final Runnable actualizarTiempoPlayer = new Runnable() {
        @Override
        public void run() {
            MediaPlayer actual = reproductor;

            if (actual == null || !actual.isPlaying()) {
                return;
            }

            seekPlayer.setProgress(actual.getCurrentPosition());
            txtTiempoPlayer.setText(getString(
                    R.string.tiempo_formato,
                    formatoTiempo(actual.getCurrentPosition()),
                    formatoTiempo(actual.getDuration())));

            handler.postDelayed(this, REFRESCO_MS);
        }
    };

    // ==================== RECORTE ====================

    private void mostrarDialogoCortar() {
        if (audioSeleccionado == null) {
            return;
        }

        int duracionTotal = (int) audioSeleccionado.getDuracionMs();

        if (duracionTotal <= MINIMO_RECORTE_MS) {
            avisar(R.string.error_audio_corto);
            return;
        }

        View vista = LayoutInflater.from(this).inflate(R.layout.dialog_recortar, null);

        TextView txtInicio = vista.findViewById(R.id.txtRangoInicio);
        TextView txtFin = vista.findViewById(R.id.txtRangoFin);
        SeekBar seekInicio = vista.findViewById(R.id.seekInicio);
        SeekBar seekFin = vista.findViewById(R.id.seekFin);

        seekInicio.setMax(duracionTotal);
        seekFin.setMax(duracionTotal);
        seekFin.setProgress(duracionTotal);

        txtInicio.setText(getString(R.string.cortar_inicio, formatoTiempo(0)));
        txtFin.setText(getString(R.string.cortar_fin, formatoTiempo(duracionTotal)));

        seekInicio.setOnSeekBarChangeListener(new CambioSeek(
                progreso -> txtInicio.setText(getString(R.string.cortar_inicio, formatoTiempo(progreso)))));

        seekFin.setOnSeekBarChangeListener(new CambioSeek(
                progreso -> txtFin.setText(getString(R.string.cortar_fin, formatoTiempo(progreso)))));

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.cortar_titulo)
                .setMessage(R.string.cortar_mensaje)
                .setView(vista)
                .setPositiveButton(R.string.cortar_aplicar, (dialogo, cual) -> {
                    int inicio = seekInicio.getProgress();
                    int fin = seekFin.getProgress();

                    if (fin - inicio < MINIMO_RECORTE_MS) {
                        avisar(R.string.error_fragmento_corto);
                        return;
                    }

                    aplicarCorte(inicio, fin);
                })
                .setNegativeButton(R.string.accion_cancelar, null)
                .show();
    }

    private void aplicarCorte(int inicioMs, int finMs) {
        if (audioSeleccionado == null) {
            return;
        }

        detenerReproduccion();

        File original = audioSeleccionado.getArchivo();
        File temporal = new File(getCacheDir(), "corte_temporal" + Almacenamiento.EXTENSION);

        if (temporal.exists() && !temporal.delete()) {
            avisar(R.string.error_cortar);
            return;
        }

        avisar(R.string.cortar_en_curso);

        executor.execute(() -> {
            boolean exito = AudioTrimmer.cortarAudio(
                    original.getAbsolutePath(), temporal.getAbsolutePath(), inicioMs, finMs);

            // Se copia el recorte sobre el original en lugar de borrar y
            // renombrar: si el renombrado fallaba, la versión anterior
            // borraba el audio original sin dejar nada en su lugar.
            boolean reemplazado = exito
                    && temporal.exists()
                    && AudioTrimmer.copiar(temporal, original);

            if (temporal.exists() && !temporal.delete()) {
                temporal.deleteOnExit();
            }

            if (isFinishing() || isDestroyed()) {
                return;
            }

            runOnUiThread(() -> {
                avisar(reemplazado ? R.string.cortar_exito : R.string.error_cortar);
                cargarListaAudios();
            });
        });
    }

    // ==================== BORRADO ====================

    private void confirmarEliminar() {
        if (audioSeleccionado == null) {
            return;
        }

        String nombre = audioSeleccionado.getNombre();

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.eliminar_titulo)
                .setMessage(getString(R.string.eliminar_mensaje, nombre))
                .setPositiveButton(R.string.accion_eliminar, (dialogo, cual) -> eliminarSeleccionado())
                .setNegativeButton(R.string.accion_cancelar, null)
                .show();
    }

    private void eliminarSeleccionado() {
        if (audioSeleccionado == null) {
            return;
        }

        File archivo = audioSeleccionado.getArchivo();
        detenerReproduccion();

        boolean borrado = archivo.delete();
        avisar(borrado ? R.string.eliminar_exito : R.string.error_eliminar);

        audioSeleccionado = null;
        cargarListaAudios();
    }

    // ==================== CICLO DE VIDA ====================

    @Override
    protected void onPause() {
        super.onPause();

        if (reproductor != null && reproductor.isPlaying()) {
            reproductor.pause();
            handler.removeCallbacks(actualizarTiempoPlayer);
            mostrarIconoReproduccion(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(actualizarTiempoPlayer);
        liberarReproductor();
        executor.shutdownNow();
    }

    private void liberarReproductor() {
        if (reproductor == null) {
            return;
        }

        try {
            if (reproductor.isPlaying()) {
                reproductor.stop();
            }
        } catch (IllegalStateException ignorada) {
            // El reproductor ya no estaba en un estado válido.
        }

        try {
            reproductor.release();
        } catch (RuntimeException ignorada) {
            // El objeto se descarta igualmente.
        }

        reproductor = null;
    }

    private void avisar(@StringRes int mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private String formatoTiempo(int milisegundos) {
        int segundos = Math.max(0, milisegundos) / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d", segundos / 60, segundos % 60);
    }

    /** Pequeño adaptador para no repetir los tres métodos de SeekBar. */
    private static class CambioSeek implements SeekBar.OnSeekBarChangeListener {

        interface AlCambiar {
            void enProgreso(int progreso);
        }

        private final AlCambiar accion;

        CambioSeek(@NonNull AlCambiar accion) {
            this.accion = accion;
        }

        @Override
        public void onProgressChanged(SeekBar barra, int progreso, boolean deUsuario) {
            accion.enProgreso(progreso);
        }

        @Override
        public void onStartTrackingTouch(SeekBar barra) { }

        @Override
        public void onStopTrackingTouch(SeekBar barra) { }
    }
}
