package com.example.registromultimedia;

import android.app.AlertDialog;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class activity_reproductor_audio extends AppCompatActivity {

    private ImageView btnVolverGrabadora;
    private RecyclerView rvAudios;
    private TextView txtSinAudios;
    private TextView txtNombreSeleccionado, txtFechaSeleccionado, txtTiempoPlayer;
    private SeekBar seekPlayer;
    private ImageButton imgbtnPlayPausePlayer, imgbtnCortar, imgbtnEliminar;

    private final List<AudioItem> audios = new ArrayList<>();
    private AudioAdapter adapter;

    private MediaPlayer reproductor;
    private AudioItem audioSeleccionado;
    private int posicionSeleccionada = -1;

    private final Handler handler = new Handler();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor_audio);

        btnVolverGrabadora = findViewById(R.id.btnVolverGrabadora);
        rvAudios = findViewById(R.id.rvAudios);
        txtSinAudios = findViewById(R.id.txtSinAudios);
        txtNombreSeleccionado = findViewById(R.id.txtNombreSeleccionado);
        txtFechaSeleccionado = findViewById(R.id.txtFechaSeleccionado);
        txtTiempoPlayer = findViewById(R.id.txtTiempoPlayer);
        seekPlayer = findViewById(R.id.seekPlayer);
        imgbtnPlayPausePlayer = findViewById(R.id.imgbtnPlayPausePlayer);
        imgbtnCortar = findViewById(R.id.imgbtnCortar);
        imgbtnEliminar = findViewById(R.id.imgbtnEliminar);

        btnVolverGrabadora.setOnClickListener(v -> finish());

        rvAudios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudioAdapter(audios, this::seleccionarAudio);
        rvAudios.setAdapter(adapter);

        imgbtnPlayPausePlayer.setOnClickListener(v -> alternarReproduccion());
        imgbtnCortar.setOnClickListener(v -> mostrarDialogoCortar());
        imgbtnEliminar.setOnClickListener(v -> confirmarEliminar());

        seekPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean usuario) {
                if (usuario && reproductor != null) {
                    reproductor.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        cargarListaAudios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaAudios();
    }

    private void cargarListaAudios() {

        File carpeta = Almacenamiento.carpetaGrabaciones(this);
        File[] archivos = carpeta.exists() ? carpeta.listFiles() : null;

        String rutaSeleccionadaAnterior = audioSeleccionado != null
                ? audioSeleccionado.getArchivo().getAbsolutePath()
                : null;

        audios.clear();
        posicionSeleccionada = -1;

        if (archivos != null) {

            Arrays.sort(archivos, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

            SimpleDateFormat formato =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("es", "ES"));

            for (File archivo : archivos) {

                if (!archivo.getName().endsWith(".m4a")) continue;

                String fecha = formato.format(new Date(archivo.lastModified()));

                AudioItem item = new AudioItem(archivo);
                item.setNombre("Grabación " + fecha);
                item.setFecha(fecha);
                item.setDuracionMs(obtenerDuracion(archivo));

                audios.add(item);

                if (rutaSeleccionadaAnterior != null
                        && archivo.getAbsolutePath().equals(rutaSeleccionadaAnterior)) {
                    posicionSeleccionada = audios.size() - 1;
                }
            }
        }

        adapter.setPosicionSeleccionada(posicionSeleccionada);
        adapter.notifyDataSetChanged();

        txtSinAudios.setVisibility(audios.isEmpty() ? View.VISIBLE : View.GONE);
        rvAudios.setVisibility(audios.isEmpty() ? View.GONE : View.VISIBLE);

        if (posicionSeleccionada != -1) {

            audioSeleccionado = audios.get(posicionSeleccionada);

            txtNombreSeleccionado.setText(audioSeleccionado.getNombre());
            txtFechaSeleccionado.setText(audioSeleccionado.getFecha());

            imgbtnPlayPausePlayer.setEnabled(true);
            imgbtnCortar.setEnabled(true);
            imgbtnEliminar.setEnabled(true);
            seekPlayer.setEnabled(true);

            prepararReproductor();

        } else {

            audioSeleccionado = null;
            limpiarReproductor();
        }
    }

    private long obtenerDuracion(File archivo) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {

            retriever.setDataSource(archivo.getAbsolutePath());

            String duracion = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
            );

            return duracion != null ? Long.parseLong(duracion) : 0;

        } catch (Exception e) {

            return 0;

        } finally {

            try {
                retriever.release();
            } catch (Exception ignored) {
            }
        }
    }

    private void seleccionarAudio(AudioItem item, int posicion) {

        detenerReproduccion();

        audioSeleccionado = item;
        posicionSeleccionada = posicion;

        txtNombreSeleccionado.setText(item.getNombre());
        txtFechaSeleccionado.setText(item.getFecha());

        imgbtnPlayPausePlayer.setEnabled(true);
        imgbtnCortar.setEnabled(true);
        imgbtnEliminar.setEnabled(true);
        seekPlayer.setEnabled(true);

        prepararReproductor();
        iniciarReproduccion();
    }

    private void prepararReproductor() {

        if (audioSeleccionado == null) return;

        try {

            if (reproductor != null) {
                reproductor.release();
                reproductor = null;
            }

            reproductor = new MediaPlayer();
            reproductor.setDataSource(audioSeleccionado.getArchivo().getAbsolutePath());
            reproductor.prepare();

            seekPlayer.setMax(reproductor.getDuration());
            seekPlayer.setProgress(0);

            txtTiempoPlayer.setText(
                    "00:00 / " + formatoTiempo(reproductor.getDuration())
            );

            reproductor.setOnCompletionListener(mp -> {

                seekPlayer.setProgress(0);
                imgbtnPlayPausePlayer.setImageResource(android.R.drawable.ic_media_play);
                handler.removeCallbacks(actualizarTiempoPlayer);
            });

        } catch (Exception e) {

            Toast.makeText(this, "Error al cargar el audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarReproduccion() {

        if (reproductor == null) return;

        reproductor.start();
        imgbtnPlayPausePlayer.setImageResource(android.R.drawable.ic_media_pause);
        handler.post(actualizarTiempoPlayer);
    }

    private void alternarReproduccion() {

        if (reproductor == null) {
            prepararReproductor();
            iniciarReproduccion();
            return;
        }

        if (reproductor.isPlaying()) {

            reproductor.pause();
            imgbtnPlayPausePlayer.setImageResource(android.R.drawable.ic_media_play);

        } else {

            reproductor.start();
            imgbtnPlayPausePlayer.setImageResource(android.R.drawable.ic_media_pause);
            handler.post(actualizarTiempoPlayer);
        }
    }

    private void detenerReproduccion() {

        handler.removeCallbacks(actualizarTiempoPlayer);

        if (reproductor != null) {

            try {
                if (reproductor.isPlaying()) reproductor.stop();
            } catch (Exception ignored) {
            }

            reproductor.release();
            reproductor = null;
        }

        imgbtnPlayPausePlayer.setImageResource(android.R.drawable.ic_media_play);
    }

    private void limpiarReproductor() {

        detenerReproduccion();

        txtNombreSeleccionado.setText("Selecciona un audio para reproducir");
        txtFechaSeleccionado.setText("");
        txtTiempoPlayer.setText("00:00 / 00:00");
        seekPlayer.setProgress(0);
        seekPlayer.setEnabled(false);

        imgbtnPlayPausePlayer.setEnabled(false);
        imgbtnCortar.setEnabled(false);
        imgbtnEliminar.setEnabled(false);
    }

    private final Runnable actualizarTiempoPlayer = new Runnable() {

        @Override
        public void run() {

            if (reproductor != null && reproductor.isPlaying()) {

                int actual = reproductor.getCurrentPosition();
                int total = reproductor.getDuration();

                seekPlayer.setProgress(actual);
                txtTiempoPlayer.setText(formatoTiempo(actual) + " / " + formatoTiempo(total));

                handler.postDelayed(this, 500);
            }
        }
    };

    private String formatoTiempo(int milisegundos) {
        int segundos = milisegundos / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d", segundos / 60, segundos % 60);
    }

    private void mostrarDialogoCortar() {

        if (audioSeleccionado == null) return;

        View vista = LayoutInflater.from(this).inflate(R.layout.dialog_recortar, null);

        TextView txtInicio = vista.findViewById(R.id.txtRangoInicio);
        TextView txtFin = vista.findViewById(R.id.txtRangoFin);
        SeekBar seekInicio = vista.findViewById(R.id.seekInicio);
        SeekBar seekFin = vista.findViewById(R.id.seekFin);

        int duracionTotal = (int) audioSeleccionado.getDuracionMs();

        if (duracionTotal <= 1000) {
            Toast.makeText(this, "El audio es demasiado corto para cortar", Toast.LENGTH_SHORT).show();
            return;
        }

        seekInicio.setMax(duracionTotal);
        seekFin.setMax(duracionTotal);
        seekFin.setProgress(duracionTotal);

        txtInicio.setText("Inicio: 00:00");
        txtFin.setText("Fin: " + formatoTiempo(duracionTotal));

        seekInicio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean usuario) {
                txtInicio.setText("Inicio: " + formatoTiempo(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {}

            @Override
            public void onStopTrackingTouch(SeekBar sb) {}
        });

        seekFin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean usuario) {
                txtFin.setText("Fin: " + formatoTiempo(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {}

            @Override
            public void onStopTrackingTouch(SeekBar sb) {}
        });

        new AlertDialog.Builder(this)
                .setTitle("Cortar audio")
                .setMessage("Elige el fragmento que quieres conservar")
                .setView(vista)
                .setPositiveButton("Aplicar corte", (dialog, which) -> {

                    int inicio = seekInicio.getProgress();
                    int fin = seekFin.getProgress();

                    if (fin - inicio < 1000) {
                        Toast.makeText(
                                this,
                                "El fragmento debe durar al menos 1 segundo",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    aplicarCorte(inicio, fin);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aplicarCorte(int inicioMs, int finMs) {

        if (audioSeleccionado == null) return;

        detenerReproduccion();

        File original = audioSeleccionado.getArchivo();
        File temporal = new File(getCacheDir(), "corte_temporal.m4a");

        if (temporal.exists()) temporal.delete();

        Toast.makeText(this, "Cortando audio...", Toast.LENGTH_SHORT).show();

        executor.execute(() -> {

            boolean exito = AudioTrimmer.cortarAudio(
                    original.getAbsolutePath(),
                    temporal.getAbsolutePath(),
                    inicioMs,
                    finMs
            );

            runOnUiThread(() -> {

                if (exito && temporal.exists()) {

                    if (original.delete() && temporal.renameTo(original)) {

                        Toast.makeText(this, "Audio cortado correctamente", Toast.LENGTH_SHORT).show();
                        cargarListaAudios();

                    } else {

                        Toast.makeText(
                                this,
                                "No se pudo reemplazar el audio original",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {

                    Toast.makeText(this, "No se pudo cortar el audio", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void confirmarEliminar() {

        if (audioSeleccionado == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Eliminar audio")
                .setMessage(
                        "¿Seguro que quieres eliminar \"" + audioSeleccionado.getNombre()
                                + "\"? Esta acción no se puede deshacer."
                )
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarAudioSeleccionado())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarAudioSeleccionado() {

        if (audioSeleccionado == null) return;

        detenerReproduccion();

        boolean borrado = audioSeleccionado.getArchivo().delete();

        Toast.makeText(
                this,
                borrado ? "Audio eliminado" : "No se pudo eliminar el audio",
                Toast.LENGTH_SHORT
        ).show();

        audioSeleccionado = null;
        cargarListaAudios();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detenerReproduccion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(actualizarTiempoPlayer);
        detenerReproduccion();
        executor.shutdown();
    }
}
