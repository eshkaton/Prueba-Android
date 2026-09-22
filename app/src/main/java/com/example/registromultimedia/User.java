package com.example.registromultimedia;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Cuenta de acceso a la app.
 *
 * <p>La contraseña nunca se guarda en claro: se almacena el hash SHA-256
 * junto con la sal usada para calcularlo. La comparación la hace
 * {@link UserRepository}.</p>
 */
public class User {

    private final String correo;
    private final String sal;
    private final String hashPassword;

    public User(@NonNull String correo, @NonNull String sal, @NonNull String hashPassword) {
        this.correo = correo;
        this.sal = sal;
        this.hashPassword = hashPassword;
    }

    @NonNull
    public String getCorreo() {
        return correo;
    }

    @NonNull
    public String getSal() {
        return sal;
    }

    @NonNull
    public String getHashPassword() {
        return hashPassword;
    }

    /** Parte del correo anterior a la arroba, para saludar al usuario. */
    @NonNull
    public String getNombreVisible() {
        int arroba = correo.indexOf('@');
        return arroba > 0 ? correo.substring(0, arroba) : correo;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof User)) return false;
        return correo.equals(((User) otro).correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correo);
    }
}
