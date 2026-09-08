package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editUser, editPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Conectar componentes de la vista
        editUser = findViewById(R.id.editUser);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Acción al hacer clic en el botón de ingresar
        btnLogin.setOnClickListener(v -> validarCredenciales());
    }

    private void validarCredenciales() {
        String usuario = editUser.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // 1. Validar que los campos no estén vacíos
        if (usuario.isEmpty()) {
            editUser.setError("Por favor ingrese su usuario");
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Por favor ingrese su contraseña");
            return;
        }

        // 2. Lógica de autenticación (puedes cambiar estos datos por los que necesites)
        if (usuario.equals("admin") && password.equals("1234")) {
            Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();

            // 3. Redirigir a la pantalla principal (MainActivity)
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            // Cerrar LoginActivity para que al presionar "atrás" la app no regrese al login
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}