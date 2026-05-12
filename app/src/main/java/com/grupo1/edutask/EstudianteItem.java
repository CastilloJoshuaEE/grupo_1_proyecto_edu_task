package com.grupo1.edutask;

public class EstudianteItem {
    private int id;
    private String nombre;
    private String correo;
    private String cedula;
    private String rol;

    public EstudianteItem(int id, String nombre, String correo, String cedula, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.cedula = cedula;
        this.rol = rol;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getCedula() { return cedula; }
    public String getRol() { return rol; }
}