package com.grupo1.edutask;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApuntesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApunteAdapter adapter;
    private List<ApunteItem> listaApuntes;
    private Spinner spinnerFiltroCategoria;
    private DatabaseHelper db;
    private int estudianteId;
    private List<String> nombresCats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apuntes);

        Toolbar toolbar = findViewById(R.id.toolbarApuntes);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db           = DatabaseHelper.getInstance(this);
        estudianteId = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).getInt("estudianteId", -1);

        recyclerView         = findViewById(R.id.recyclerApuntes);
        spinnerFiltroCategoria = findViewById(R.id.spinnerFiltroCategoria);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregarApunte);

        listaApuntes = new ArrayList<>();
        adapter = new ApunteAdapter(listaApuntes,
                this::mostrarDialogoEditar,
                this::confirmarEliminar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarFiltroCategoria();

        spinnerFiltroCategoria.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                cargarApuntes();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });

        fabAgregar.setOnClickListener(v -> mostrarFormulario(null));

        cargarApuntes();
    }

    private void cargarFiltroCategoria() {
        nombresCats = db.getNombresCategorias(estudianteId);
        List<String> opciones = new ArrayList<>();
        opciones.add("Todas las categorías");
        opciones.addAll(nombresCats);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroCategoria.setAdapter(adapter);
    }

    private void cargarApuntes() {
        listaApuntes.clear();
        Cursor c;

        int posicion = spinnerFiltroCategoria.getSelectedItemPosition();
        if (posicion == 0) {
            c = db.getApuntes(estudianteId);
        } else {
            String catNombre = nombresCats.get(posicion - 1);
            int catId = db.getIdCategoriaPorNombre(catNombre, estudianteId);
            c = db.getApuntesPorCategoria(estudianteId, catId);
        }

        if (c.moveToFirst()) {
            do {
                ApunteItem a = new ApunteItem(
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_APU_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_APU_TITULO)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_APU_CONTENIDO)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_APU_FECHA)),
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_APU_CAT_ID)),
                        c.getString(c.getColumnIndexOrThrow("cat_nombre"))
                );
                listaApuntes.add(a);
            } while (c.moveToNext());
        }
        c.close();
        adapter.notifyDataSetChanged();

        TextView tvVacio = findViewById(R.id.tvApuntesVacio);
        tvVacio.setVisibility(listaApuntes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarFormulario(ApunteItem apunteExistente) {
        View view = getLayoutInflater().inflate(R.layout.dialog_apunte_form, null);

        TextInputEditText etTitulo   = view.findViewById(R.id.etTituloApunte);
        TextInputEditText etContenido = view.findViewById(R.id.etContenidoApunte);
        Spinner spinnerCat            = view.findViewById(R.id.spinnerCategoriaApunte);

        List<String> cats = db.getNombresCategorias(estudianteId);
        spinnerCat.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cats));
        ((ArrayAdapter) spinnerCat.getAdapter())
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (apunteExistente != null) {
            etTitulo.setText(apunteExistente.getTitulo());
            etContenido.setText(apunteExistente.getContenido());
            for (int i = 0; i < cats.size(); i++) {
                if (cats.get(i).equals(apunteExistente.getCategoriaNombre())) {
                    spinnerCat.setSelection(i);
                    break;
                }
            }
        }

        String dialogTitulo = apunteExistente == null ? "Nuevo Apunte" : "Editar Apunte";

        new AlertDialog.Builder(this)
                .setTitle(dialogTitulo)
                .setView(view)
                .setPositiveButton("Guardar", (d, w) -> {
                    String t  = etTitulo.getText() != null ? etTitulo.getText().toString().trim() : "";
                    String co = etContenido.getText() != null ? etContenido.getText().toString().trim() : "";
                    String ca = spinnerCat.getSelectedItem().toString();

                    if (t.isEmpty()) {
                        Toast.makeText(this, "Ingrese un título", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int catId = db.getIdCategoriaPorNombre(ca, estudianteId);
                    String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                    if (apunteExistente == null) {
                        long id = db.insertarApunte(t, co, fecha, catId, -1, estudianteId);
                        if (id > 0) Toast.makeText(this, "Apunte creado", Toast.LENGTH_SHORT).show();
                    } else {
                        db.actualizarApunte(apunteExistente.getId(), t, co, catId);
                        Toast.makeText(this, "Apunte actualizado", Toast.LENGTH_SHORT).show();
                    }
                    cargarApuntes();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditar(ApunteItem apunte) {
        mostrarFormulario(apunte);
    }

    private void confirmarEliminar(ApunteItem apunte) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar apunte")
                .setMessage("¿Eliminar \"" + apunte.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarApunte(apunte.getId());
                    Toast.makeText(this, "Apunte eliminado", Toast.LENGTH_SHORT).show();
                    cargarApuntes();
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