package com.example.registromultimedia;

import android.os.Bundle;
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
        //Codigo Implementado por Vicente López

        // Conectar componentes con el XML
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

    private void registrarUsuario() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editEmail.setError("Por favor ingrese su correo");
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Por favor ingrese su contraseña");
            return;
        }

        // Guardamos directamente en la ArrayList usando las variables ya creadas arriba
        UserRepository.userList.add(new User(email, password));

        Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
        finish(); // Cierra esta actividad y regresa al Login
    }
}
//Creado por Vicente López