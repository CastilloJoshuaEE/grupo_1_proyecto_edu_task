package com.grupo1.edutask;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CategoriasActivity extends AppCompatActivity {

    private RecyclerView rvCategorias;
    private TextView tvEmpty;
    private FloatingActionButton fabAgregar;
    private DatabaseHelper dbHelper;
    private CategoriaAdapter adapter;
    private int estudianteId; // ID del estudiante logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        // Cambia esto según cómo estés manejando tu sesión (por SharedPreferences o Intent)
        estudianteId = getSharedPreferences("UserSession", MODE_PRIVATE).getInt("id", -1);

        dbHelper = DatabaseHelper.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbarCategorias);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvCategorias = findViewById(R.id.rvCategorias);
        tvEmpty = findViewById(R.id.tvEmptyCategorias);
        fabAgregar = findViewById(R.id.fabAgregarCategoria);

        rvCategorias.setLayoutManager(new LinearLayoutManager(this));

        cargarCategorias();

        fabAgregar.setOnClickListener(v -> mostrarDialogoGuardar(null));
    }

    private void cargarCategorias() {
        List<CategoriaItem> lista = new ArrayList<>();
        Cursor c = dbHelper.getCategorias(estudianteId);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_ID));
                    String nombre = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_NOMBRE));
                    String color = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_COLOR));
                    int estId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_EST_ID));

                    lista.add(new CategoriaItem(id, nombre, color, estId));
                } while (c.moveToNext());
            }
            c.close();
        }

        if (lista.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCategorias.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCategorias.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new CategoriaAdapter(lista, new CategoriaAdapter.OnCategoriaClickListener() {
                @Override
                public void onEditClick(CategoriaItem item) {
                    mostrarDialogoGuardar(item);
                }

                @Override
                public void onDeleteClick(CategoriaItem item) {
                    confirmarEliminar(item);
                }
            });
            rvCategorias.setAdapter(adapter);
        } else {
            adapter.actualizarLista(lista);
        }
    }

    // Diálogo unificado para Crear (CRUD - C) y Editar (CRUD - U)
    private void mostrarDialogoGuardar(CategoriaItem itemExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(itemExistente == null ? "Nueva Categoría" : "Editar Categoría");

        final EditText input = new EditText(this);
        input.setHint("Nombre de la categoría");
        if (itemExistente != null) {
            input.setText(itemExistente.getNombre());
        }
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = input.getText().toString().trim();
            if (!nombre.isEmpty()) {
                if (itemExistente == null) {
                    // Crear nueva categoría propia (Usa color por defecto azul)
                    dbHelper.insertarCategoria(nombre, "#2196F3", estudianteId);
                    Toast.makeText(this, "Categoría creada", Toast.LENGTH_SHORT).show();
                } else {
                    // Actualizar categoría
                    dbHelper.actualizarCategoria(itemExistente.getId(), nombre);
                    Toast.makeText(this, "Categoría actualizada", Toast.LENGTH_SHORT).show();
                }
                cargarCategorias();
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Confirmación para Eliminar (CRUD - D)
    private void confirmarEliminar(CategoriaItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Categoría")
                .setMessage("¿Estás seguro de que deseas eliminar \"" + item.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.eliminarCategoria(item.getId());
                    Toast.makeText(this, "Categoría eliminada", Toast.LENGTH_SHORT).show();
                    cargarCategorias();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
