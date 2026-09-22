package com.example.registromultimedia;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

/** Ficha de un integrante, abierta al tocar una fila del listado. */
public class DetalleIntegranteActivity extends AppCompatActivity {

    // Claves del Intent en un solo sitio: antes estaban escritas a mano en
    // el adaptador y en esta pantalla, y un cambio en una podía no llegar
    // a la otra.
    public static final String EXTRA_NOMBRE = "nombre_integrante";
    public static final String EXTRA_ROL = "rol_integrante";
    public static final String EXTRA_TECNOLOGIAS = "tecnologias_integrante";
    public static final String EXTRA_JORNADA = "jornada_integrante";
    public static final String EXTRA_FOTO = "foto_integrante";
    public static final String EXTRA_VALORACION = "valoracion_integrante";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_integrante);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetalle);
        TextView tvNombre = findViewById(R.id.tvNombreDetalle);
        TextView tvRol = findViewById(R.id.tvRolDetalle);
        TextView tvTecnologias = findViewById(R.id.tvTecnologiasDetalle);
        TextView tvJornada = findViewById(R.id.tvJornadaDetalle);
        ShapeableImageView imgFoto = findViewById(R.id.imgFotoDetalle);
        RatingBar ratingBar = findViewById(R.id.ratingBarDetalle);
        MaterialButton btnVolver = findViewById(R.id.btnVolver);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnVolver.setOnClickListener(v -> finish());

        int fotoResId = getIntent().getIntExtra(EXTRA_FOTO, Integrante.SIN_FOTO);
        float valoracion = getIntent().getFloatExtra(EXTRA_VALORACION, 0f);

        // Las etiquetas «Nombre:», «Rol:»… ya están en el layout, así que
        // aquí solo se escribe el valor.
        tvNombre.setText(textoODefecto(getIntent().getStringExtra(EXTRA_NOMBRE)));
        tvRol.setText(textoODefecto(getIntent().getStringExtra(EXTRA_ROL)));
        tvTecnologias.setText(textoODefecto(getIntent().getStringExtra(EXTRA_TECNOLOGIAS)));
        tvJornada.setText(textoODefecto(getIntent().getStringExtra(EXTRA_JORNADA)));

        imgFoto.setImageResource(
                fotoResId != Integrante.SIN_FOTO ? fotoResId : R.drawable.ic_avatar_generico);

        ratingBar.setRating(valoracion);
    }

    private String textoODefecto(String valor) {
        return valor == null || valor.trim().isEmpty()
                ? getString(R.string.detalle_sin_dato)
                : valor;
    }
}
