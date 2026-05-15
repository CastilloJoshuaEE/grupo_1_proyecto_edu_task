package com.grupo1.edutask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "edutask.db";
    private static final int DB_VERSION = 2;

    // Tabla Estudiantes
    public static final String TABLE_ESTUDIANTES = "estudiantes";
    public static final String COL_EST_ID       = "id";
    public static final String COL_EST_NOMBRE   = "nombre";
    public static final String COL_EST_CORREO   = "correo";
    public static final String COL_EST_PASSWORD  = "password";
    public static final String COL_EST_CEDULA   = "cedula";
    public static final String COL_EST_ROL = "rol";
    public static final String COL_EST_ACTIVO   = "activo";

    // Tabla Categorías
    public static final String TABLE_CATEGORIAS = "categorias";
    public static final String COL_CAT_ID       = "id";
    public static final String COL_CAT_NOMBRE   = "nombre";
    public static final String COL_CAT_COLOR    = "color";
    public static final String COL_CAT_EST_ID   = "estudiante_id";

    // Tabla Tareas
    public static final String TABLE_TAREAS     = "tareas";
    public static final String COL_TAR_ID       = "id";
    public static final String COL_TAR_TITULO   = "titulo";
    public static final String COL_TAR_DESC     = "descripcion";
    public static final String COL_TAR_FECHA    = "fecha_entrega";
    public static final String COL_TAR_PRIORIDAD = "prioridad";
    public static final String COL_TAR_COMPLETADA = "completada";
    public static final String COL_TAR_CAT_ID   = "categoria_id";
    public static final String COL_TAR_EST_ID   = "estudiante_id";

    // Tabla Apuntes
    public static final String TABLE_APUNTES    = "apuntes";
    public static final String COL_APU_ID       = "id";
    public static final String COL_APU_TITULO   = "titulo";
    public static final String COL_APU_CONTENIDO = "contenido";
    public static final String COL_APU_FECHA    = "fecha";
    public static final String COL_APU_CAT_ID   = "categoria_id";
    public static final String COL_APU_TAR_ID   = "tarea_id";
    public static final String COL_APU_EST_ID   = "estudiante_id";

    // Tabla Recordatorios
    public static final String TABLE_RECORDATORIOS = "recordatorios";
    public static final String COL_REC_ID          = "id";
    public static final String COL_REC_MENSAJE      = "mensaje";
    public static final String COL_REC_FECHA        = "fecha";
    public static final String COL_REC_HORA         = "hora";
    public static final String COL_REC_EST_ID       = "estudiante_id";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ESTUDIANTES + " (" +
                COL_EST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EST_NOMBRE + " TEXT NOT NULL, " +
                COL_EST_CORREO + " TEXT NOT NULL UNIQUE, " +
                COL_EST_PASSWORD + " TEXT NOT NULL, " +
                COL_EST_CEDULA + " TEXT, " +
                COL_EST_ROL + " TEXT DEFAULT 'Estudiante', " +
                COL_EST_ACTIVO + " INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIAS + " (" +
                COL_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CAT_NOMBRE + " TEXT NOT NULL, " +
                COL_CAT_COLOR + " TEXT DEFAULT '#2196F3', " +
                COL_CAT_EST_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_CAT_EST_ID + ") REFERENCES " + TABLE_ESTUDIANTES + "(" + COL_EST_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_TAREAS + " (" +
                COL_TAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TAR_TITULO + " TEXT NOT NULL, " +
                COL_TAR_DESC + " TEXT, " +
                COL_TAR_FECHA + " TEXT, " +
                COL_TAR_PRIORIDAD + " TEXT DEFAULT 'Media', " +
                COL_TAR_COMPLETADA + " INTEGER DEFAULT 0, " +
                COL_TAR_CAT_ID + " INTEGER, " +
                COL_TAR_EST_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_TAR_CAT_ID + ") REFERENCES " + TABLE_CATEGORIAS + "(" + COL_CAT_ID + "), " +
                "FOREIGN KEY(" + COL_TAR_EST_ID + ") REFERENCES " + TABLE_ESTUDIANTES + "(" + COL_EST_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_APUNTES + " (" +
                COL_APU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_APU_TITULO + " TEXT NOT NULL, " +
                COL_APU_CONTENIDO + " TEXT, " +
                COL_APU_FECHA + " TEXT, " +
                COL_APU_CAT_ID + " INTEGER, " +
                COL_APU_TAR_ID + " INTEGER, " +
                COL_APU_EST_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_APU_CAT_ID + ") REFERENCES " + TABLE_CATEGORIAS + "(" + COL_CAT_ID + "), " +
                "FOREIGN KEY(" + COL_APU_TAR_ID + ") REFERENCES " + TABLE_TAREAS + "(" + COL_TAR_ID + "), " +
                "FOREIGN KEY(" + COL_APU_EST_ID + ") REFERENCES " + TABLE_ESTUDIANTES + "(" + COL_EST_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_RECORDATORIOS + " (" +
                COL_REC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REC_MENSAJE + " TEXT NOT NULL, " +
                COL_REC_FECHA + " TEXT, " +
                COL_REC_HORA + " TEXT, " +
                COL_REC_EST_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_REC_EST_ID + ") REFERENCES " + TABLE_ESTUDIANTES + "(" + COL_EST_ID + "))");

        // Categorías por defecto
        db.execSQL("INSERT INTO " + TABLE_CATEGORIAS + " (" + COL_CAT_NOMBRE + "," + COL_CAT_COLOR + "," + COL_CAT_EST_ID + ") VALUES ('Matemáticas','#F44336',NULL)");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIAS + " (" + COL_CAT_NOMBRE + "," + COL_CAT_COLOR + "," + COL_CAT_EST_ID + ") VALUES ('Programación','#2196F3',NULL)");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIAS + " (" + COL_CAT_NOMBRE + "," + COL_CAT_COLOR + "," + COL_CAT_EST_ID + ") VALUES ('Ciencias','#4CAF50',NULL)");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIAS + " (" + COL_CAT_NOMBRE + "," + COL_CAT_COLOR + "," + COL_CAT_EST_ID + ") VALUES ('Historia','#FF9800',NULL)");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIAS + " (" + COL_CAT_NOMBRE + "," + COL_CAT_COLOR + "," + COL_CAT_EST_ID + ") VALUES ('Inglés','#9C27B0',NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDATORIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APUNTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTUDIANTES);
        onCreate(db);
    }

    // ==================== ESTUDIANTES ====================

    public long insertarEstudiante(String nombre, String correo, String password, String cedula, String rol) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EST_NOMBRE, nombre);
        cv.put(COL_EST_CORREO, correo);
        cv.put(COL_EST_PASSWORD, password);
        cv.put(COL_EST_CEDULA, cedula);
        cv.put(COL_EST_ROL, rol);
        cv.put(COL_EST_ACTIVO, 1);
        return db.insert(TABLE_ESTUDIANTES, null, cv);
    }

    public Cursor loginEstudiante(String correo, String password) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ESTUDIANTES, null,
                COL_EST_CORREO + "=? AND " + COL_EST_PASSWORD + "=? AND " + COL_EST_ACTIVO + "=1",
                new String[]{correo, password}, null, null, null);
    }

    public boolean correoExiste(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ESTUDIANTES, new String[]{COL_EST_ID},
                COL_EST_CORREO + "=?", new String[]{correo}, null, null, null);
        boolean existe = c.getCount() > 0;
        c.close();
        return existe;
    }

    public Cursor getEstudiante(int id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ESTUDIANTES, null,
                COL_EST_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public int actualizarEstudiante(int id, String nombre, String password, String cedula) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EST_NOMBRE, nombre);
        cv.put(COL_EST_PASSWORD, password);
        cv.put(COL_EST_CEDULA, cedula);
        return db.update(TABLE_ESTUDIANTES, cv, COL_EST_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int desactivarEstudiante(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EST_ACTIVO, 0);
        return db.update(TABLE_ESTUDIANTES, cv, COL_EST_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ==================== CATEGORÍAS ====================

    public Cursor getCategorias(int estudianteId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_CATEGORIAS, null,
                COL_CAT_EST_ID + " IS NULL OR " + COL_CAT_EST_ID + "=?",
                new String[]{String.valueOf(estudianteId)}, null, null, COL_CAT_NOMBRE);
    }

    public long insertarCategoria(String nombre, String color, int estudianteId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CAT_NOMBRE, nombre);
        cv.put(COL_CAT_COLOR, color);
        cv.put(COL_CAT_EST_ID, estudianteId);
        return db.insert(TABLE_CATEGORIAS, null, cv);
    }

    public List<String> getNombresCategorias(int estudianteId) {
        List<String> lista = new ArrayList<>();
        Cursor c = getCategorias(estudianteId);
        if (c.moveToFirst()) {
            do {
                lista.add(c.getString(c.getColumnIndexOrThrow(COL_CAT_NOMBRE)));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public int getIdCategoriaPorNombre(String nombre, int estudianteId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CATEGORIAS, new String[]{COL_CAT_ID},
                "(" + COL_CAT_EST_ID + " IS NULL OR " + COL_CAT_EST_ID + "=?) AND " + COL_CAT_NOMBRE + "=?",
                new String[]{String.valueOf(estudianteId), nombre}, null, null, null);
        int id = -1;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();
        return id;
    }
    //
    // Actualizar una categoría existente (CRUD - Actualizar)
    public int actualizarCategoria(int id, String nombre) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CAT_NOMBRE, nombre);
        // Nota: Si en el diálogo manejas color, puedes agregar: cv.put(COL_CAT_COLOR, color);
        return db.update(TABLE_CATEGORIAS, cv, COL_CAT_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Eliminar una categoría (CRUD - Eliminar)
    public int eliminarCategoria(int id) {
        SQLiteDatabase db = getWritableDatabase();
        // Al eliminar, las tareas asociadas quedarán con categoria_id en NULL o puedes decidir borrarlas.
        return db.delete(TABLE_CATEGORIAS, COL_CAT_ID + "=?", new String[]{String.valueOf(id)});
    }



    //

    // ==================== TAREAS ====================

    public long insertarTarea(String titulo, String desc, String fecha, String prioridad, int catId, int estId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TAR_TITULO, titulo);
        cv.put(COL_TAR_DESC, desc);
        cv.put(COL_TAR_FECHA, fecha);
        cv.put(COL_TAR_PRIORIDAD, prioridad);
        cv.put(COL_TAR_COMPLETADA, 0);
        cv.put(COL_TAR_CAT_ID, catId);
        cv.put(COL_TAR_EST_ID, estId);
        return db.insert(TABLE_TAREAS, null, cv);
    }

    public Cursor getTareas(int estId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c." + COL_CAT_NOMBRE + " as cat_nombre FROM " + TABLE_TAREAS + " t " +
                        "LEFT JOIN " + TABLE_CATEGORIAS + " c ON t." + COL_TAR_CAT_ID + "=c." + COL_CAT_ID +
                        " WHERE t." + COL_TAR_EST_ID + "=? ORDER BY t." + COL_TAR_COMPLETADA + " ASC, t." + COL_TAR_FECHA + " ASC",
                new String[]{String.valueOf(estId)});
    }

    public Cursor getTareasFiltradas(int estId, int completada) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c." + COL_CAT_NOMBRE + " as cat_nombre FROM " + TABLE_TAREAS + " t " +
                        "LEFT JOIN " + TABLE_CATEGORIAS + " c ON t." + COL_TAR_CAT_ID + "=c." + COL_CAT_ID +
                        " WHERE t." + COL_TAR_EST_ID + "=? AND t." + COL_TAR_COMPLETADA + "=? ORDER BY t." + COL_TAR_FECHA + " ASC",
                new String[]{String.valueOf(estId), String.valueOf(completada)});
    }

    public int actualizarTarea(int id, String titulo, String desc, String fecha, String prioridad, int catId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TAR_TITULO, titulo);
        cv.put(COL_TAR_DESC, desc);
        cv.put(COL_TAR_FECHA, fecha);
        cv.put(COL_TAR_PRIORIDAD, prioridad);
        cv.put(COL_TAR_CAT_ID, catId);
        return db.update(TABLE_TAREAS, cv, COL_TAR_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int marcarTareaCompletada(int id, boolean completada) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TAR_COMPLETADA, completada ? 1 : 0);
        return db.update(TABLE_TAREAS, cv, COL_TAR_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int eliminarTarea(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TAREAS, COL_TAR_ID + "=?", new String[]{String.valueOf(id)});
    }
        //
        // Obtener tareas filtradas por una categoría específica (Filtros)
        public Cursor getTareasPorCategoria(int estId, int catId) {
            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery(
                    "SELECT t.*, c." + COL_CAT_NOMBRE + " as cat_nombre FROM " + TABLE_TAREAS + " t " +
                            "LEFT JOIN " + TABLE_CATEGORIAS + " c ON t." + COL_TAR_CAT_ID + "=c." + COL_CAT_ID +
                            " WHERE t." + COL_TAR_EST_ID + "=? AND t." + COL_TAR_CAT_ID + "=? " +
                            "ORDER BY t." + COL_TAR_COMPLETADA + " ASC, t." + COL_TAR_FECHA + " ASC",
                    new String[]{String.valueOf(estId), String.valueOf(catId)});
        }


        //
    // ==================== APUNTES ====================

    public long insertarApunte(String titulo, String contenido, String fecha, int catId, int tarId, int estId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_APU_TITULO, titulo);
        cv.put(COL_APU_CONTENIDO, contenido);
        cv.put(COL_APU_FECHA, fecha);
        cv.put(COL_APU_CAT_ID, catId > 0 ? catId : null);
        cv.put(COL_APU_TAR_ID, tarId > 0 ? tarId : null);
        cv.put(COL_APU_EST_ID, estId);
        return db.insert(TABLE_APUNTES, null, cv);
    }

    public Cursor getApuntes(int estId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT a.*, c." + COL_CAT_NOMBRE + " as cat_nombre FROM " + TABLE_APUNTES + " a " +
                        "LEFT JOIN " + TABLE_CATEGORIAS + " c ON a." + COL_APU_CAT_ID + "=c." + COL_CAT_ID +
                        " WHERE a." + COL_APU_EST_ID + "=? ORDER BY a." + COL_APU_FECHA + " DESC",
                new String[]{String.valueOf(estId)});
    }

    public Cursor getApuntesPorCategoria(int estId, int catId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT a.*, c." + COL_CAT_NOMBRE + " as cat_nombre FROM " + TABLE_APUNTES + " a " +
                        "LEFT JOIN " + TABLE_CATEGORIAS + " c ON a." + COL_APU_CAT_ID + "=c." + COL_CAT_ID +
                        " WHERE a." + COL_APU_EST_ID + "=? AND a." + COL_APU_CAT_ID + "=? ORDER BY a." + COL_APU_FECHA + " DESC",
                new String[]{String.valueOf(estId), String.valueOf(catId)});
    }

    public int actualizarApunte(int id, String titulo, String contenido, int catId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_APU_TITULO, titulo);
        cv.put(COL_APU_CONTENIDO, contenido);
        cv.put(COL_APU_CAT_ID, catId > 0 ? catId : null);
        return db.update(TABLE_APUNTES, cv, COL_APU_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int eliminarApunte(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_APUNTES, COL_APU_ID + "=?", new String[]{String.valueOf(id)});
    }
    // ADMINISTRACIÓN
    public Cursor getAllEstudiantes() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ESTUDIANTES, null,
                COL_EST_ACTIVO + "=1", null, null, null, COL_EST_ID + " DESC");
    }
    public Cursor getEstudiantePorCedula(String cedula) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ESTUDIANTES, null,
                COL_EST_CEDULA + "=? AND " + COL_EST_ACTIVO + "=1",
                new String[]{cedula}, null, null, null);
    }

    public int actualizarEstudianteCompleto(int id, String nombre, String password, String cedula, String rol) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EST_NOMBRE, nombre);
        cv.put(COL_EST_PASSWORD, password);
        cv.put(COL_EST_CEDULA, cedula);
        cv.put(COL_EST_ROL, rol);
        return db.update(TABLE_ESTUDIANTES, cv, COL_EST_ID + "=?", new String[]{String.valueOf(id)});
    }

    public String getRolEstudiante(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ESTUDIANTES, new String[]{COL_EST_ROL},
                COL_EST_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        String rol = "estudiante";
        if (c.moveToFirst()) {
            rol = c.getString(0);
        }
        c.close();
        return rol;
    }

    // ==================== RECORDATORIOS ====================

    public long insertarRecordatorio(String mensaje, String fecha, String hora, int estId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_REC_MENSAJE, mensaje);
        cv.put(COL_REC_FECHA, fecha);
        cv.put(COL_REC_HORA, hora);
        cv.put(COL_REC_EST_ID, estId);
        return db.insert(TABLE_RECORDATORIOS, null, cv);
    }

    public Cursor getRecordatorios(int estId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_RECORDATORIOS, null,
                COL_REC_EST_ID + "=?", new String[]{String.valueOf(estId)},
                null, null, COL_REC_FECHA + " ASC, " + COL_REC_HORA + " ASC");
    }

    public int actualizarRecordatorio(int id, String mensaje, String fecha, String hora) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_REC_MENSAJE, mensaje);
        cv.put(COL_REC_FECHA, fecha);
        cv.put(COL_REC_HORA, hora);
        return db.update(TABLE_RECORDATORIOS, cv, COL_REC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int eliminarRecordatorio(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_RECORDATORIOS, COL_REC_ID + "=?", new String[]{String.valueOf(id)});
    }
}