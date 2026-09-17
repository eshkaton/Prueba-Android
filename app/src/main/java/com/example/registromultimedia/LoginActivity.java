package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editUser, editPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Conectar componentes de la vista
        editUser = findViewById(R.id.editUser);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Acción al hacer clic en el botón de ingresar
        btnLogin.setOnClickListener(v -> validarCredenciales());

        // Acción al hacer clic en el botón de registrarse para abrir la otra pantalla
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void validarCredenciales() {
        String usuarioIngresado = editUser.getText().toString().trim();
        String passwordIngresada = editPassword.getText().toString().trim();

        // 1. Validar que los campos no estén vacíos
        if (usuarioIngresado.isEmpty()) {
            editUser.setError("Por favor ingrese su usuario");
            return;
        }

        if (passwordIngresada.isEmpty()) {
            editPassword.setError("Por favor ingrese su contraseña");
            return;
        }

        // 2. Buscar si el usuario existe en el ArrayList temporal
        boolean usuarioEncontrado = false;
        for (User u : UserRepository.userList) {
            if (u.getEmail().equals(usuarioIngresado) && u.getPassword().equals(passwordIngresada)) {
                usuarioEncontrado = true;
                break;
            }
        }

        // 3. Validar si coincide con la lista O con el administrador por defecto
        if (usuarioEncontrado || (usuarioIngresado.equals("admin") && passwordIngresada.equals("1234"))) {
            Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();

            // Redirigir a la pantalla principal (MainActivity)
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            // Cerrar LoginActivity para que al presionar "atrás" no regrese al login
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}