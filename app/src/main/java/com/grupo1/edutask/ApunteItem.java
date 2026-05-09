package com.grupo1.edutask;

public class ApunteItem {
    private int id;
    private String titulo;
    private String contenido;
    private String fecha;
    private int categoriaId;
    private String categoriaNombre;

    public ApunteItem(int id, String titulo, String contenido, String fecha,
                      int categoriaId, String categoriaNombre) {
        this.id              = id;
        this.titulo          = titulo;
        this.contenido       = contenido;
        this.fecha           = fecha;
        this.categoriaId     = categoriaId;
        this.categoriaNombre = categoriaNombre;
    }

    public int getId()               { return id; }
    public String getTitulo()        { return titulo; }
    public String getContenido()     { return contenido; }
    public String getFecha()         { return fecha; }
    public int getCategoriaId()      { return categoriaId; }
    public String getCategoriaNombre(){ return categoriaNombre; }
}