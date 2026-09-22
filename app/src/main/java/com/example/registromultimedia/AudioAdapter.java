package com.example.registromultimedia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/** Dibuja la lista de grabaciones guardadas. */
public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    public interface OnAudioClickListener {
        void onAudioClick(@NonNull AudioItem item, int posicion);
    }

    /** Ninguna fila seleccionada. */
    public static final int SIN_SELECCION = -1;

    private final List<AudioItem> audios;
    private final OnAudioClickListener listener;
    private int posicionSeleccionada = SIN_SELECCION;

    public AudioAdapter(@NonNull List<AudioItem> audios, @NonNull OnAudioClickListener listener) {
        this.audios = audios;
        this.listener = listener;
    }

    /**
     * Cambia la fila resaltada y repinta solo las dos afectadas.
     *
     * <p>Antes se llamaba a {@code notifyItemChanged(-1)} al seleccionar la
     * primera grabación, porque no se comprobaba que hubiera una selección
     * anterior.</p>
     */
    public void setPosicionSeleccionada(int posicion) {
        int anterior = posicionSeleccionada;
        posicionSeleccionada = posicion;

        if (anterior != SIN_SELECCION) {
            notifyItemChanged(anterior);
        }
        if (posicion != SIN_SELECCION) {
            notifyItemChanged(posicion);
        }
    }

    /**
     * Fija la fila resaltada sin notificar cambios.
     *
     * <p>Para usar junto a {@code notifyDataSetChanged()}, que ya repinta
     * todas las filas.</p>
     */
    public void fijarSeleccion(int posicion) {
        posicionSeleccionada = posicion;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audio, parent, false);

        return new AudioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioItem item = audios.get(position);
        View fila = holder.itemView;

        holder.txtNombre.setText(item.getNombre());
        holder.txtDetalle.setText(fila.getContext().getString(
                R.string.reproductor_detalle, item.getFecha(), formatoTiempo(item.getDuracionMs())));

        // El resaltado se hace con el estado «activated» del selector del
        // layout. Reemplazar el background en tiempo de ejecución, como se
        // hacía antes, borraba el padding de las filas recicladas.
        fila.setActivated(position == posicionSeleccionada);

        fila.setOnClickListener(v -> {
            int posicionActual = holder.getBindingAdapterPosition();

            if (posicionActual == RecyclerView.NO_POSITION) {
                return;
            }

            setPosicionSeleccionada(posicionActual);
            listener.onAudioClick(audios.get(posicionActual), posicionActual);
        });
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    private String formatoTiempo(long milisegundos) {
        int segundos = (int) (milisegundos / 1000);
        return String.format(Locale.getDefault(), "%02d:%02d", segundos / 60, segundos % 60);
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {

        final TextView txtNombre;
        final TextView txtDetalle;

        AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreAudio);
            txtDetalle = itemView.findViewById(R.id.txtDetalleAudio);
        }
    }
}
