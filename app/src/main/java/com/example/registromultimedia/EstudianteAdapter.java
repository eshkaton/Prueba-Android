package com.example.registromultimedia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EstudianteAdapter
        extends RecyclerView.Adapter<EstudianteAdapter.ViewHolder> {

    private ArrayList<Estudiante> listaEstudiantes;

    public EstudianteAdapter(ArrayList<Estudiante> listaEstudiantes) {
        this.listaEstudiantes = listaEstudiantes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View vista = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_estudiante,
                        parent,
                        false
                );

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Estudiante estudiante =
                listaEstudiantes.get(position);

        holder.txtNombre.setText(
                estudiante.getNombre()
        );

        holder.txtCarrera.setText(
                estudiante.getCarrera()
        );

        holder.imgEstudiante.setImageResource(
                R.mipmap.ic_launcher
        );
    }

    @Override
    public int getItemCount() {
        return listaEstudiantes.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtCarrera;

        ImageView imgEstudiante;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtNombre =
                    itemView.findViewById(
                            R.id.txtNombre
                    );

            txtCarrera =
                    itemView.findViewById(
                            R.id.txtCarrera
                    );

            imgEstudiante =
                    itemView.findViewById(
                            R.id.imgEstudiante
                    );
        }
    }
}