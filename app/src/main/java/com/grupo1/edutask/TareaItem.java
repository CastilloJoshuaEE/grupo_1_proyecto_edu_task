package com.grupo1.edutask;

public class TareaItem {
    private int id;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String prioridad;
    private boolean completada;
    private int categoriaId;
    private String categoriaNombre;

    public TareaItem(int id, String titulo, String descripcion, String fecha,
                     String prioridad, boolean completada, int categoriaId, String categoriaNombre) {
        this.id             = id;
        this.titulo         = titulo;
        this.descripcion    = descripcion;
        this.fecha          = fecha;
        this.prioridad      = prioridad;
        this.completada     = completada;
        this.categoriaId    = categoriaId;
        this.categoriaNombre = categoriaNombre;
    }

    public int getId()              { return id; }
    public String getTitulo()       { return titulo; }
    public String getDescripcion()  { return descripcion; }
    public String getFecha()        { return fecha; }
    public String getPrioridad()    { return prioridad; }
    public boolean isCompletada()   { return completada; }
    public int getCategoriaId()     { return categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
}