package com.grupo1.edutask;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminEstudiantesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EstudianteAdapter adapter;
    private List<EstudianteItem> listaEstudiantes;
    private DatabaseHelper db;
    private EditText etBuscarCedula;
    private Button btnBuscar, btnLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_estudiantes);

        Toolbar toolbar = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Administrar Estudiantes");
        }

        db = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recyclerEstudiantes);
        etBuscarCedula = findViewById(R.id.etBuscarCedula);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpiar = findViewById(R.id.btnLimpiarBusqueda);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregarEstudiante);

        listaEstudiantes = new ArrayList<>();
        adapter = new EstudianteAdapter(listaEstudiantes,
                this::editarEstudiante,
                this::eliminarEstudiante);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarTodosLosEstudiantes();

        btnBuscar.setOnClickListener(v -> buscarPorCedula());
        btnLimpiar.setOnClickListener(v -> cargarTodosLosEstudiantes());
        fabAgregar.setOnClickListener(v -> mostrarFormulario(null));
    }

    private void cargarTodosLosEstudiantes() {
        listaEstudiantes.clear();
        Cursor c = db.getAllEstudiantes();
        if (c.moveToFirst()) {
            do {
                EstudianteItem e = new EstudianteItem(
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_NOMBRE)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_CORREO)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_CEDULA)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_ROL))
                );
                listaEstudiantes.add(e);
            } while (c.moveToNext());
        }
        c.close();
        adapter.notifyDataSetChanged();
    }

    private void buscarPorCedula() {
        String cedula = etBuscarCedula.getText().toString().trim();
        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingrese una cédula para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        listaEstudiantes.clear();
        Cursor c = db.getEstudiantePorCedula(cedula);
        if (c.moveToFirst()) {
            do {
                EstudianteItem e = new EstudianteItem(
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_NOMBRE)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_CORREO)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_CEDULA)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_ROL))
                );
                listaEstudiantes.add(e);
            } while (c.moveToNext());
        } else {
            Toast.makeText(this, "No se encontró estudiante con cédula: " + cedula, Toast.LENGTH_SHORT).show();
        }
        c.close();
        adapter.notifyDataSetChanged();
    }

    private void mostrarFormulario(EstudianteItem estudiante) {
        View view = getLayoutInflater().inflate(R.layout.dialog_estudiante_form, null);

        TextInputEditText etNombre = view.findViewById(R.id.etNombreEstudiante);
        TextInputEditText etCorreo = view.findViewById(R.id.etCorreoEstudiante);
        TextInputEditText etCedula = view.findViewById(R.id.etCedulaEstudiante);
        TextInputEditText etPassword = view.findViewById(R.id.etPasswordEstudiante);
        Spinner spinnerRol = view.findViewById(R.id.spinnerRol);

        String[] roles = {"estudiante", "administrador"};
        ArrayAdapter<String> adapterRol = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        adapterRol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterRol);

        boolean esEdicion = estudiante != null;
        String titulo = esEdicion ? "Editar Estudiante" : "Nuevo Estudiante";

        if (esEdicion) {
            etNombre.setText(estudiante.getNombre());
            etCorreo.setText(estudiante.getCorreo());
            etCorreo.setEnabled(false);
            etCedula.setText(estudiante.getCedula());
            etCedula.setEnabled(false);
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].equals(estudiante.getRol())) {
                    spinnerRol.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(view)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
                    String correo = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
                    String cedula = etCedula.getText() != null ? etCedula.getText().toString().trim() : "";
                    String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
                    String rol = spinnerRol.getSelectedItem().toString();

                    if (nombre.isEmpty()) {
                        Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (correo.isEmpty()) {
                        Toast.makeText(this, "Ingrese el correo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!esEdicion && password.isEmpty()) {
                        Toast.makeText(this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (esEdicion) {
                        db.actualizarEstudianteCompleto(estudiante.getId(), nombre, password, cedula, rol);
                        Toast.makeText(this, "Estudiante actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        if (db.correoExiste(correo)) {
                            Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        db.insertarEstudiante(nombre, correo, password, cedula, rol);
                        Toast.makeText(this, "Estudiante registrado", Toast.LENGTH_SHORT).show();
                    }
                    cargarTodosLosEstudiantes();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarEstudiante(EstudianteItem estudiante) {
        mostrarFormulario(estudiante);
    }

    private void eliminarEstudiante(EstudianteItem estudiante) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar estudiante")
                .setMessage("¿Eliminar a " + estudiante.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.desactivarEstudiante(estudiante.getId());
                    Toast.makeText(this, "Estudiante eliminado", Toast.LENGTH_SHORT).show();
                    cargarTodosLosEstudiantes();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}