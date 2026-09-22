package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends BaseDrawerActivity {

    @Override
        protected int idPropio() {
                    return R.id.menu_inicio;
        }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);
                    configurarMenuLateral();

            IntegranteRepository.cargarDatosIniciales();

            TextView tvConteo = findViewById(R.id.tv_conteo_integrantes);
                    tvConteo.setText(String.valueOf(IntegranteRepository.lista.size()));

            findViewById(R.id.card_agregar).setOnClickListener(v ->
                                                                               startActivity(new Intent(this, AgregarIntegranteActivity.class)));

            findViewById(R.id.card_lista).setOnClickListener(v ->
                                                                             startActivity(new Intent(this, ListaIntegrantesActivity.class)));
        }

    @Override
        protected void onResume() {
                    super.onResume();
                    TextView tvConteo = findViewById(R.id.tv_conteo_integrantes);
                    if (tvConteo != null) {
                                    tvConteo.setText(String.valueOf(IntegranteRepository.lista.size()));
                    }
        }
}
