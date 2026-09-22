package com.example.registromultimedia;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Dibuja el listado de integrantes y abre la ficha al tocar una fila. */
public class IntegranteAdapter extends RecyclerView.Adapter<IntegranteAdapter.ViewHolder> {

    private final List<Integrante> integrantes = new ArrayList<>();

    public IntegranteAdapter(@NonNull List<Integrante> integrantes) {
        this.integrantes.addAll(integrantes);
    }

    /** Sustituye el contenido de la lista (por ejemplo, tras agregar a alguien). */
    public void actualizar(@NonNull List<Integrante> nuevos) {
        integrantes.clear();
        integrantes.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_integrante, parent, false);

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Integrante integrante = integrantes.get(position);

        holder.txtNombre.setText(integrante.getNombre());
        holder.txtRol.setText(integrante.getRol());
        holder.ratingBar.setRating(integrante.getValoracion());

        // Quien no tiene foto propia recibe el avatar genérico. Antes el
        // constructor corto asignaba la foto de Lucas a todo el mundo.
        holder.imgFoto.setImageResource(
                integrante.tieneFoto() ? integrante.getFotoResId() : R.drawable.ic_avatar_generico);

        holder.itemView.setOnClickListener(v -> abrirDetalle(v.getContext(), integrante));
    }

    @Override
    public int getItemCount() {
        return integrantes.size();
    }

    private void abrirDetalle(@NonNull Context contexto, @NonNull Integrante integrante) {
        Intent intent = new Intent(contexto, DetalleIntegranteActivity.class);

        intent.putExtra(DetalleIntegranteActivity.EXTRA_NOMBRE, integrante.getNombre());
        intent.putExtra(DetalleIntegranteActivity.EXTRA_ROL, integrante.getRol());
        intent.putExtra(DetalleIntegranteActivity.EXTRA_TECNOLOGIAS, integrante.getTecnologias());
        intent.putExtra(DetalleIntegranteActivity.EXTRA_JORNADA, integrante.getJornada());
        intent.putExtra(DetalleIntegranteActivity.EXTRA_FOTO, integrante.getFotoResId());
        intent.putExtra(DetalleIntegranteActivity.EXTRA_VALORACION, integrante.getValoracion());

        contexto.startActivity(intent);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView txtNombre;
        final TextView txtRol;
        final RatingBar ratingBar;
        final ImageView imgFoto;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.tvNombre);
            txtRol = itemView.findViewById(R.id.tvRol);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgFoto = itemView.findViewById(R.id.imgFoto);
        }
    }
}
