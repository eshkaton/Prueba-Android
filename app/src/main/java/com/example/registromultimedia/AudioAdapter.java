package com.example.registromultimedia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    public interface OnAudioClickListener {
        void onAudioClick(AudioItem item, int posicion);
    }

    private final List<AudioItem> audios;
    private final OnAudioClickListener listener;
    private int posicionSeleccionada = -1;

    public AudioAdapter(List<AudioItem> audios, OnAudioClickListener listener) {
        this.audios = audios;
        this.listener = listener;
    }

    public void setPosicionSeleccionada(int posicion) {
        this.posicionSeleccionada = posicion;
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

        holder.txtNombre.setText(item.getNombre());
        holder.txtDetalle.setText(
                item.getFecha() + "  •  " + formatoTiempo(item.getDuracionMs())
        );

        boolean seleccionado = position == posicionSeleccionada;

        holder.itemView.setBackgroundResource(
                seleccionado
                        ? R.drawable.fondo_item_seleccionado
                        : R.drawable.fondo_item_normal
        );

        holder.txtNombre.setTextColor(
                seleccionado ? 0xFF203A97 : 0xFF000000
        );

        holder.itemView.setOnClickListener(v -> {

            int posicionAnterior = posicionSeleccionada;
            posicionSeleccionada = holder.getAdapterPosition();

            notifyItemChanged(posicionAnterior);
            notifyItemChanged(posicionSeleccionada);

            if (listener != null) {
                listener.onAudioClick(item, posicionSeleccionada);
            }
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
