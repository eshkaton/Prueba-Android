package com.example.registromultimedia;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListaIntegrantesActivity extends BaseDrawerActivity {

    private IntegranteAdapter adapter;
      private RecyclerView recyclerIntegrantes;
      private TextView tvSinIntegrantes;

    @Override
      protected int idPropio() {
                return R.id.menu_lista_integrantes;
      }

    @Override
      protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_lista_integrantes);
                configurarMenuLateral();

          IntegranteRepository.cargarDatosIniciales();

          recyclerIntegrantes = findViewById(R.id.recyclerIntegrantes);
                tvSinIntegrantes = findViewById(R.id.tv_sin_integrantes);

          recyclerIntegrantes.setLayoutManager(new LinearLayoutManager(this));
                adapter = new IntegranteAdapter(IntegranteRepository.lista);
                recyclerIntegrantes.setAdapter(adapter);

          actualizarEstadoVacio();
      }

    @Override
      protected void onResume() {
                super.onResume();
                adapter.notifyDataSetChanged();
                actualizarEstadoVacio();
      }

    private void actualizarEstadoVacio() {
              boolean vacio = IntegranteRepository.lista.isEmpty();
              tvSinIntegrantes.setVisibility(vacio ? View.VISIBLE : View.GONE);
              recyclerIntegrantes.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }
}
