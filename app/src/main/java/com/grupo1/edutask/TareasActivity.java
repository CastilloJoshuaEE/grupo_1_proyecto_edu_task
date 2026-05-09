package com.grupo1.edutask;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TareasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TareaAdapter adapter;
    private List<TareaItem> listaTareas;
    private FloatingActionButton fabAgregar;
    private RadioGroup radioFiltro;
    private DatabaseHelper db;
    private int estudianteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        Toolbar toolbar = findViewById(R.id.toolbarTareas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db           = DatabaseHelper.getInstance(this);
        estudianteId = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).getInt("estudianteId", -1);

        recyclerView = findViewById(R.id.recyclerTareas);
        fabAgregar   = findViewById(R.id.fabAgregarTarea);
        radioFiltro  = findViewById(R.id.radioFiltroTareas);

        listaTareas = new ArrayList<>();
        adapter = new TareaAdapter(listaTareas,
                this::mostrarDialogoEditar,
                this::confirmarEliminar,
                this::toggleCompletada);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        radioFiltro.setOnCheckedChangeListener((g, id) -> cargarTareas());

        fabAgregar.setOnClickListener(v -> mostrarDialogoNueva());

        cargarTareas();
    }

    private void cargarTareas() {
        listaTareas.clear();
        Cursor c;
        int checkedId = radioFiltro.getCheckedRadioButtonId();

        if (checkedId == R.id.radioPendientes) {
            c = db.getTareasFiltradas(estudianteId, 0);
        } else if (checkedId == R.id.radioCompletadas) {
            c = db.getTareasFiltradas(estudianteId, 1);
        } else {
            c = db.getTareas(estudianteId);
        }

        if (c.moveToFirst()) {
            do {
                TareaItem t = new TareaItem(
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_TAR_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TAR_TITULO)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TAR_DESC)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TAR_FECHA)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TAR_PRIORIDAD)),
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_TAR_COMPLETADA)) == 1,
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_TAR_CAT_ID)),
                        c.getString(c.getColumnIndexOrThrow("cat_nombre"))
                );
                listaTareas.add(t);
            } while (c.moveToNext());
        }
        c.close();
        adapter.notifyDataSetChanged();

        TextView tvVacio = findViewById(R.id.tvTareasVacio);
        tvVacio.setVisibility(listaTareas.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarDialogoNueva() {
        mostrarFormularioTarea(null);
    }

    private void mostrarDialogoEditar(TareaItem tarea) {
        mostrarFormularioTarea(tarea);
    }

    private void mostrarFormularioTarea(TareaItem tareaExistente) {
        View view = getLayoutInflater().inflate(R.layout.dialog_tarea_form, null);

        TextInputEditText etTitulo = view.findViewById(R.id.etTituloTarea);
        TextInputEditText etDesc   = view.findViewById(R.id.etDescTarea);
        TextView tvFecha           = view.findViewById(R.id.tvFechaTarea);
        Spinner spinnerPrioridad   = view.findViewById(R.id.spinnerPrioridad);
        Spinner spinnerCategoria   = view.findViewById(R.id.spinnerCategoriaTarea);

        // Prioridades
        String[] prioridades = {"Alta", "Media", "Baja"};
        spinnerPrioridad.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, prioridades));
        ((ArrayAdapter) spinnerPrioridad.getAdapter())
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Categorías
        List<String> cats = db.getNombresCategorias(estudianteId);
        spinnerCategoria.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cats));
        ((ArrayAdapter) spinnerCategoria.getAdapter())
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final String[] fechaSeleccionada = {tareaExistente != null ? tareaExistente.getFecha() : ""};
        if (!fechaSeleccionada[0].isEmpty()) tvFecha.setText(fechaSeleccionada[0]);

        tvFecha.setOnClickListener(v -> {
            DatePickerFragment dp = new DatePickerFragment();
            dp.setOnDateSelectedListener((y, m, d) -> {
                fechaSeleccionada[0] = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
                tvFecha.setText(fechaSeleccionada[0]);
            });
            dp.show(getSupportFragmentManager(), "DatePicker");
        });

        if (tareaExistente != null) {
            etTitulo.setText(tareaExistente.getTitulo());
            etDesc.setText(tareaExistente.getDescripcion());
            for (int i = 0; i < prioridades.length; i++) {
                if (prioridades[i].equals(tareaExistente.getPrioridad())) {
                    spinnerPrioridad.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < cats.size(); i++) {
                if (cats.get(i).equals(tareaExistente.getCategoriaNombre())) {
                    spinnerCategoria.setSelection(i);
                    break;
                }
            }
        }

        String titulo = tareaExistente == null ? "Nueva Tarea" : "Editar Tarea";

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(view)
                .setPositiveButton("Guardar", (d, w) -> {
                    String t  = etTitulo.getText() != null ? etTitulo.getText().toString().trim() : "";
                    String de = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
                    String pr = spinnerPrioridad.getSelectedItem().toString();
                    String ca = spinnerCategoria.getSelectedItem().toString();

                    if (t.isEmpty()) {
                        Toast.makeText(this, "Ingrese un título", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fechaSeleccionada[0].isEmpty()) {
                        Toast.makeText(this, "Seleccione fecha de entrega", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int catId = db.getIdCategoriaPorNombre(ca, estudianteId);

                    if (tareaExistente == null) {
                        long id = db.insertarTarea(t, de, fechaSeleccionada[0], pr, catId, estudianteId);
                        if (id > 0) Toast.makeText(this, "Tarea creada", Toast.LENGTH_SHORT).show();
                    } else {
                        db.actualizarTarea(tareaExistente.getId(), t, de, fechaSeleccionada[0], pr, catId);
                        Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                    }
                    cargarTareas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminar(TareaItem tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Eliminar \"" + tarea.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarTarea(tarea.getId());
                    Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                    cargarTareas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void toggleCompletada(TareaItem tarea, boolean completada) {
        db.marcarTareaCompletada(tarea.getId(), completada);
        cargarTareas();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}