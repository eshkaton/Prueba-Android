package com.example.registromultimedia;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView txtBackToLogin;
    private Button btnConfirmRegister;
    private EditText editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Conectar componentes con el XML (Implementado por Ricardo Moreno)
        btnBack = findViewById(R.id.btnBack);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);
        btnConfirmRegister = findViewById(R.id.btnConfirmRegister);
        editEmail = findViewById(R.id.editRegisterEmail);
        editPassword = findViewById(R.id.editRegisterPassword);

        // Botón de retroceso en la barra superior
        btnBack.setOnClickListener(v -> finish());

        // Enlace de texto "Volver al inicio"
        txtBackToLogin.setOnClickListener(v -> finish());

        // Lógica del botón Registrar
        btnConfirmRegister.setOnClickListener(v -> registrarUsuario());
    }

    // =========================================================================
    // Desarrollado por: Seppel Krahl
    // =========================================================================

    /**
     * saca el texto del correo y contraseña y verifica que no estén vacios
     * muestra una alerta de error si falta algún campo
     */
    private boolean validarDatos() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("por favor ingrese su correo");
            Toast.makeText(this, "por favor ingrese su correo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editPassword.setError("por favor ingrese su contraseña");
            Toast.makeText(this, "por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registrarUsuario() {
        // validación de datos antes de hacer el registro
        if (!validarDatos()) {
            return;
        }

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // guardamos en el arraylist (implementado por Ricardo Moreno)
        UserRepository.userList.add(new User(email, password));

        Toast.makeText(this, "¡cuenta creada con exito!", Toast.LENGTH_SHORT).show();
        finish(); // cierra la actividad y regresa al Login
    }
}
// Creado por Ricardo Moreno y Seppel Krahl
