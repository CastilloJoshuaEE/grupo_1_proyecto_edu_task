package com.grupo1.edutask;

public class CategoriaItem {
    private int id;
    private String nombre;
    private String color;
    private int estudianteId;
    // Constructor vacío
    //public CategoriaItem() {
    //}

    // Constructor con parámetros
    public CategoriaItem(int id, String nombre,String color, int estudianteId) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.estudianteId = estudianteId;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
