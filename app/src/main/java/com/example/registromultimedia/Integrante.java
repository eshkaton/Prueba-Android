package com.example.registromultimedia;

public class Integrante{
    private String nombre;
    private String rol;
    private String tecnologias;
    private String jornada;
    private int fotoResId;
    private float valoracion;

    public Integrante(String nombre, String rol, String tecnologias, String jornada, int fotoResId, float valoracion) {
        this.nombre = nombre;
        this.rol = rol;
        this.tecnologias = tecnologias;
        this.jornada = jornada;
        this.fotoResId = fotoResId;
        this.valoracion = valoracion;
    }

    public Integrante(String nombre, String rol, String tecnologias, String jornada, float valoracion) {
        this.nombre = nombre;
        this.rol = rol;
        this.tecnologias = tecnologias;
        this.jornada = jornada;
        this.valoracion = valoracion;
        this.fotoResId = R.drawable.lucas;
    }

    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
    public String getTecnologias() { return tecnologias; }
    public String getJornada() { return jornada; }
    public int getFotoResId() { return fotoResId; }
    public float getValoracion() { return valoracion; }
}