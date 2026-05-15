package com.grupo1.edutask;

public class RecordatorioItem {
    private int id;
    private String mensaje;
    private String fecha;
    private String hora;
    private int estudianteId;

    public RecordatorioItem(int id, String mensaje, String fecha, String hora, int estudianteId) {
        this.id           = id;
        this.mensaje      = mensaje;
        this.fecha        = fecha;
        this.hora         = hora;
        this.estudianteId = estudianteId;
    }

    public int getId()           { return id; }
    public String getMensaje()   { return mensaje; }
    public String getFecha()     { return fecha; }
    public String getHora()      { return hora; }
    public int getEstudianteId() { return estudianteId; }
}
