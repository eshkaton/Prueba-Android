package com.example.registromultimedia;

public class Estudiante {
    private String nombre;
    private String carrera;
    public Estudiante(String nombre,String carrera){
        this.nombre = nombre;
        this.carrera = carrera;
    }
    public String getNombre(){
        return nombre;
    }
    public String getCarrera(){
        return carrera;
    }
}
