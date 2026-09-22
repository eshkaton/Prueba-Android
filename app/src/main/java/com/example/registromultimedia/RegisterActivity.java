package com.example.registromultimedia;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Alta de cuenta.
 *
 * <p>Se accede desde el login y, al terminar, se vuelve a él. Las validaciones
 * que antes faltaban —formato de correo, longitud mínima, correo duplicado y
 * confirmación de contraseña— se comprueban aquí.</p>
 */
public class RegisterActivity extends AppCompatActivity {

    /** Longitud mínima de contraseña aceptada en el alta. */
    private static final int MINIMO_PASSWORD = 6;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilPassword2;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private TextInputEditText editPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilEmail = findViewById(R.id.tilRegisterEmail);
        tilPassword = findViewById(R.id.tilRegisterPassword);
        tilPassword2 = findViewById(R.id.tilRegisterPassword2);
        editEmail = findViewById(R.id.editRegisterEmail);
        editPassword = findViewById(R.id.editRegisterPassword);
        editPassword2 = findViewById(R.id.editRegisterPassword2);

        ImageView btnBack = findViewById(R.id.btnBack);
        MaterialButton btnConfirmRegister = findViewById(R.id.btnConfirmRegister);
        MaterialButton txtBackToLogin = findViewById(R.id.txtBackToLogin);

        btnBack.setOnClickListener(v -> finish());
        txtBackToLogin.setOnClickListener(v -> finish());
        btnConfirmRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        if (!validarDatos()) {
            return;
        }

        String correo = texto(editEmail);
        String password = texto(editPassword);

        if (!UserRepository.registrar(correo, password)) {
            // Entre la validación y el alta pudo registrarse el mismo correo.
            tilEmail.setError(getString(R.string.error_correo_repetido));
            return;
        }

        Toast.makeText(this, R.string.exito_cuenta_creada, Toast.LENGTH_SHORT).show();
        finish();
    }

    /** Comprueba los tres campos y marca el primero que falle. */
    private boolean validarDatos() {
        String correo = texto(editEmail);
        String password = texto(editPassword);
        String repetida = texto(editPassword2);

        tilEmail.setError(null);
        tilPassword.setError(null);
        tilPassword2.setError(null);

        if (correo.isEmpty()) {
            tilEmail.setError(getString(R.string.error_correo_vacio));
            editEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilEmail.setError(getString(R.string.error_correo_invalido));
            editEmail.requestFocus();
            return false;
        }

        if (UserRepository.existe(correo)) {
            tilEmail.setError(getString(R.string.error_correo_repetido));
            editEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_password_vacio));
            editPassword.requestFocus();
            return false;
        }

        if (password.length() < MINIMO_PASSWORD) {
            tilPassword.setError(getString(R.string.error_password_corta));
            editPassword.requestFocus();
            return false;
        }

        if (!password.equals(repetida)) {
            tilPassword2.setError(getString(R.string.error_password_distinta));
            editPassword2.requestFocus();
            return false;
        }

        return true;
    }

    private String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
