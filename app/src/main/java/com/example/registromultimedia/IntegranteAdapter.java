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

public class IntegranteAdapter
          extends RecyclerView.Adapter<IntegranteAdapter.ViewHolder> {

    private ArrayList<Integrante> listaIntegrantes;

    public IntegranteAdapter(ArrayList<Integrante> listaIntegrantes) {
              this.listaIntegrantes = listaIntegrantes;
    }


    @NonNull
                @Override
                public ViewHolder onCreateViewHolder(
                              @NonNull ViewGroup parent,
                              int viewType) {

                    View vista = LayoutInflater
                                      .from(parent.getContext())
                                      .inflate(
                                                                R.layout.item_integrante,
                                                                parent,
                                                                false
                                                        );
                          return new ViewHolder(vista);
                }

    @Override
                public void onBindViewHolder(
                              @NonNull ViewHolder holder,
                              int position) {

                    Integrante integrante =
                                      listaIntegrantes.get(position);
                          holder.txtNombre.setText(
                                            integrante.getNombre()
                                    );
                          holder.txtRol.setText(
                                            integrante.getRol()
                                    );
                          holder.ratingBar.setRating(
                                            integrante.getValoracion()
                                    );
                          holder.imgFoto.setImageResource(
                                            integrante.getFotoResId()
                                    );
                          holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                                          Context context = v.getContext();
                                                          Intent intent = new Intent(context, DetalleIntegranteActivity.class);


                                            intent.putExtra("nombre_integrante", integrante.getNombre());
                                                          intent.putExtra("rol_integrante", integrante.getRol());
                                                          intent.putExtra("tecnologias_integrante", integrante.getTecnologias());
                                                          intent.putExtra("jornada_integrante", integrante.getJornada());
                                                          intent.putExtra("foto_integrante", integrante.getFotoResId());
                                                          intent.putExtra("valoracion_integrante", integrante.getValoracion());

                                            context.startActivity(intent);
                                        }
                          });
                }

    @Override
                public int getItemCount() {
                          return listaIntegrantes.size();
                }



    public static class ViewHolder
                  extends RecyclerView.ViewHolder {
                            TextView txtNombre;
                            TextView txtRol;
                            RatingBar ratingBar;
                            ImageView imgFoto;

                    public ViewHolder(
                                      @NonNull View itemView) {
                                  super(itemView);

                                txtNombre =
                                                      itemView.findViewById(
                                                                                    R.id.tvNombre
                                                                            );
                                  txtRol =
                                                        itemView.findViewById(
                                                                                      R.id.tvRol
                                                                              );
                                  ratingBar =
                                                        itemView.findViewById(
                                                                                      R.id.ratingBar
                                                                              );
                                  imgFoto =
                                                        itemView.findViewById(
                                                                                      R.id.imgFoto
                                                                              );
                    }
                  }
          }
