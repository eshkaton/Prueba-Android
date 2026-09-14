package com.example.registromultimedia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegistroActivity extends AppCompatActivity {

    // creado por Joaquin
    private ImageView imgAvatar;
    private TextInputEditText etCorreo;
    private TextInputEditText etPassword;
    private Button btnRegistrar;
    private TextView tvVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // enlaces
        imgAvatar = findViewById(R.id.registro_img_avatar);
        etCorreo = findViewById(R.id.registro_et_correo);
        etPassword = findViewById(R.id.registro_et_password);

        // enlazar btnRegistrar y tvVolver con sus IDs

        // crear el evento click del boton que llame a validarDatos()
    }

    // validación
    private void validarDatos() {
        // aqui va validar campos
    }

    // registro
    private void registrarUsuario() {
        // aqui va volver al login
    }
}