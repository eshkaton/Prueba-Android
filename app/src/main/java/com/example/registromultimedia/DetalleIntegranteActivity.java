package com.example.registromultimedia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleIntegranteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_integrante);


        TextView tvNombre = findViewById(R.id.tvNombreDetalle);
        TextView tvRol = findViewById(R.id.tvRolDetalle);
        TextView tvTecnologias = findViewById(R.id.tvTecnologiasDetalle);
        TextView tvJornada = findViewById(R.id.tvJornadaDetalle);
        ImageView imgFoto = findViewById(R.id.imgFotoDetalle);
        RatingBar ratingBar = findViewById(R.id.ratingBarDetalle);
        Button btnVolver = findViewById(R.id.btnVolver);


        String nombre = getIntent().getStringExtra("nombre_integrante");
        String rol = getIntent().getStringExtra("rol_integrante");
        String tecnologias = getIntent().getStringExtra("tecnologias_integrante");
        String jornada = getIntent().getStringExtra("jornada_integrante");
        int fotoResId = getIntent().getIntExtra("foto_integrante", 0);
        float valoracion = getIntent().getFloatExtra("valoracion_integrante", 0f);
        imgFoto.setImageResource(fotoResId);


        if (nombre != null) {
            tvNombre.setText("Nombre: " + nombre);
        }
        if (rol != null) {
            tvRol.setText("Rol: " + rol);
        }
        if (tecnologias != null) {
            tvTecnologias.setText("Tecnologías: " + tecnologias);
        }
        if (jornada != null) {
            tvJornada.setText("Jornada: " + jornada);
        }
        if (fotoResId != 0) {
            imgFoto.setImageResource(fotoResId);
        }
        if (ratingBar != null) {
            ratingBar.setRating(valoracion);
        }


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
