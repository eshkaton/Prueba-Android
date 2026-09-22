package com.example.registromultimedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.TextView;

/**
 * Primera pantalla del flujo.
 *
 * <p>Al validar las credenciales se abre {@link WelcomeActivity}, que es la
 * que hospeda el resto de vistas a través de su barra de menú.</p>
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsuario;
    private TextInputLayout tilPassword;
    private TextInputEditText editUser;
    private TextInputEditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilUsuario = findViewById(R.id.tilUsuario);
        tilPassword = findViewById(R.id.tilPassword);
        editUser = findViewById(R.id.editUser);
        editPassword = findViewById(R.id.editPassword);

        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        TextView txtGoRegister = findViewById(R.id.txtGoRegister);

        btnLogin.setOnClickListener(v -> validarCredenciales());

        // Los dos caminos hacia el registro: el botón y el enlace del pie.
        // El enlace del pie existía en el diseño pero no tenía listener, así
        // que hasta ahora no hacía nada al pulsarlo.
        btnRegister.setOnClickListener(v -> abrirRegistro());
        txtGoRegister.setOnClickListener(v -> abrirRegistro());

        // Pulsar «Listo» en el teclado equivale a pulsar «Iniciar sesión».
        editPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validarCredenciales();
                return true;
            }
            return false;
        });

        limpiarErroresAlEscribir();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Al volver del registro, la contraseña no debe quedar en pantalla.
        if (editPassword.getText() != null && editPassword.getText().length() > 0) {
            editPassword.setText("");
        }
    }

    private void limpiarErroresAlEscribir() {
        editUser.setOnFocusChangeListener((v, tieneFoco) -> {
            if (tieneFoco) tilUsuario.setError(null);
        });

        editPassword.setOnFocusChangeListener((v, tieneFoco) -> {
            if (tieneFoco) tilPassword.setError(null);
        });
    }

    private void abrirRegistro() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void validarCredenciales() {
        String usuario = texto(editUser);
        String password = texto(editPassword);

        tilUsuario.setError(null);
        tilPassword.setError(null);

        if (usuario.isEmpty()) {
            tilUsuario.setError(getString(R.string.error_usuario_vacio));
            editUser.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_password_vacio));
            editPassword.requestFocus();
            return;
        }

        User autenticado = UserRepository.autenticar(usuario, password);

        if (autenticado == null) {
            // Un único mensaje para usuario o contraseña incorrectos: así no
            // se revela cuáles correos están registrados.
            tilPassword.setError(getString(R.string.error_credenciales));
            return;
        }

        SesionManager.iniciarSesion(autenticado);
        Toast.makeText(this, R.string.exito_sesion, Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    private String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
