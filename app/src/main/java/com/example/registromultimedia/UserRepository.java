package com.example.registromultimedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Cuentas registradas mientras la app está en memoria.
 *
 * <p>Sigue siendo un almacén temporal (al cerrar el proceso se pierde), pero
 * ahora la lista está encapsulada y las contraseñas se guardan como hash
 * SHA-256 con sal en lugar de en texto plano.</p>
 *
 * <p>Para una app real esto debería reemplazarse por Room o un backend, y el
 * hash por un algoritmo con coste configurable como bcrypt o Argon2.</p>
 */
public final class UserRepository {

    /** Cuenta de demostración disponible siempre: admin / 1234. */
    private static final String USUARIO_DEMO = "admin";
    private static final String PASSWORD_DEMO = "1234";

    private static final List<User> USUARIOS = Collections.synchronizedList(new ArrayList<>());
    private static final SecureRandom ALEATORIO = new SecureRandom();

    static {
        registrar(USUARIO_DEMO, PASSWORD_DEMO);
    }

    private UserRepository() {
        // Clase de utilidad: no se instancia.
    }

    /** Copia de solo lectura, para que nadie modifique la lista por fuera. */
    @NonNull
    public static List<User> getUsuarios() {
        synchronized (USUARIOS) {
            return Collections.unmodifiableList(new ArrayList<>(USUARIOS));
        }
    }

    public static boolean existe(@Nullable String correo) {
        return buscar(correo) != null;
    }

    /**
     * Da de alta una cuenta.
     *
     * @return {@code false} si ya existía otra cuenta con ese correo.
     */
    public static boolean registrar(@Nullable String correo, @Nullable String password) {
        String normalizado = normalizar(correo);

        if (normalizado.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        if (existe(normalizado)) {
            return false;
        }

        String sal = generarSal();
        USUARIOS.add(new User(normalizado, sal, hashear(password, sal)));
        return true;
    }

    /**
     * Comprueba unas credenciales.
     *
     * @return el usuario autenticado, o {@code null} si no coinciden.
     */
    @Nullable
    public static User autenticar(@Nullable String correo, @Nullable String password) {
        User usuario = buscar(correo);

        if (usuario == null || password == null) {
            return null;
        }

        String calculado = hashear(password, usuario.getSal());
        return iguales(calculado, usuario.getHashPassword()) ? usuario : null;
    }

    @Nullable
    private static User buscar(@Nullable String correo) {
        String normalizado = normalizar(correo);

        if (normalizado.isEmpty()) {
            return null;
        }

        synchronized (USUARIOS) {
            for (User usuario : USUARIOS) {
                if (usuario.getCorreo().equals(normalizado)) {
                    return usuario;
                }
            }
        }

        return null;
    }

    /** El correo no distingue mayúsculas ni espacios al principio o al final. */
    @NonNull
    private static String normalizar(@Nullable String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }

    @NonNull
    private static String generarSal() {
        byte[] bytes = new byte[16];
        ALEATORIO.nextBytes(bytes);
        return aHexadecimal(bytes);
    }

    @NonNull
    private static String hashear(@NonNull String password, @NonNull String sal) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(sal.getBytes(StandardCharsets.UTF_8));
            return aHexadecimal(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 está garantizado en todas las versiones de Android.
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    /** Comparación en tiempo constante: no filtra información por el tiempo de respuesta. */
    private static boolean iguales(@NonNull String a, @NonNull String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }

    @NonNull
    private static String aHexadecimal(@NonNull byte[] bytes) {
        StringBuilder texto = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            texto.append(Character.forDigit((b >> 4) & 0xF, 16));
            texto.append(Character.forDigit(b & 0xF, 16));
        }

        return texto.toString();
    }
}
