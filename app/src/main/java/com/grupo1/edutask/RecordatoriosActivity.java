package com.grupo1.edutask;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
import java.util.Locale;

public class RecordatoriosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecordatorioAdapter adapter;
    private List<RecordatorioItem> listaRecordatorios;
    private DatabaseHelper db;
    private int estudianteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios);

        Toolbar toolbar = findViewById(R.id.toolbarRecordatorios);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db           = DatabaseHelper.getInstance(this);
        estudianteId = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).getInt("estudianteId", -1);

        recyclerView = findViewById(R.id.recyclerRecordatorios);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregarRecordatorio);

        listaRecordatorios = new ArrayList<>();
        adapter = new RecordatorioAdapter(listaRecordatorios,
                this::mostrarDialogoEditar,
                this::confirmarEliminar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> mostrarFormulario(null));

        cargarRecordatorios();
    }

    private void cargarRecordatorios() {
        listaRecordatorios.clear();
        Cursor c = db.getRecordatorios(estudianteId);
        if (c.moveToFirst()) {
            do {
                RecordatorioItem r = new RecordatorioItem(
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_REC_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REC_MENSAJE)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REC_FECHA)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REC_HORA)),
                        estudianteId
                );
                listaRecordatorios.add(r);
            } while (c.moveToNext());
        }
        c.close();
        adapter.notifyDataSetChanged();

        TextView tvVacio = findViewById(R.id.tvRecordatoriosVacio);
        tvVacio.setVisibility(listaRecordatorios.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarFormulario(RecordatorioItem recordatorioExistente) {
        View view = getLayoutInflater().inflate(R.layout.dialog_recordatorio_form, null);

        TextInputEditText etMensaje = view.findViewById(R.id.etMensajeRecordatorio);
        TextView tvFecha            = view.findViewById(R.id.tvFechaRecordatorioForm);
        TextView tvHora             = view.findViewById(R.id.tvHoraRecordatorioForm);

        final String[] fechaSeleccionada = {""};
        final String[] horaSeleccionada  = {""};

        if (recordatorioExistente != null) {
            etMensaje.setText(recordatorioExistente.getMensaje());
            fechaSeleccionada[0] = recordatorioExistente.getFecha() != null ? recordatorioExistente.getFecha() : "";
            horaSeleccionada[0]  = recordatorioExistente.getHora()  != null ? recordatorioExistente.getHora()  : "";
            tvFecha.setText(fechaSeleccionada[0].isEmpty() ? "Toque para seleccionar fecha" : fechaSeleccionada[0]);
            tvHora.setText(horaSeleccionada[0].isEmpty()   ? "Toque para seleccionar hora"  : horaSeleccionada[0]);
        }

        tvFecha.setOnClickListener(v -> {
            DatePickerFragment dp = new DatePickerFragment();
            dp.setOnDateSelectedListener((y, m, d) -> {
                fechaSeleccionada[0] = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
                tvFecha.setText(fechaSeleccionada[0]);
            });
            dp.show(getSupportFragmentManager(), "DatePicker");
        });

        tvHora.setOnClickListener(v -> {
            android.app.TimePickerDialog tpd = new android.app.TimePickerDialog(this,
                    (timePicker, hour, minute) -> {
                        horaSeleccionada[0] = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                        tvHora.setText(horaSeleccionada[0]);
                    },
                    java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY),
                    java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE),
                    true);
            tpd.show();
        });

        String titulo = recordatorioExistente == null ? "Nuevo Recordatorio" : "Editar Recordatorio";

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(view)
                .setPositiveButton("Guardar", (d, w) -> {
                    String msg = etMensaje.getText() != null ? etMensaje.getText().toString().trim() : "";

                    if (msg.isEmpty()) {
                        Toast.makeText(this, "Ingrese un mensaje", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fechaSeleccionada[0].isEmpty()) {
                        Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (horaSeleccionada[0].isEmpty()) {
                        Toast.makeText(this, "Seleccione una hora", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (recordatorioExistente == null) {
                        long id = db.insertarRecordatorio(msg, fechaSeleccionada[0], horaSeleccionada[0], estudianteId);
                        if (id > 0) Toast.makeText(this, "Recordatorio creado", Toast.LENGTH_SHORT).show();
                    } else {
                        db.actualizarRecordatorio(recordatorioExistente.getId(), msg, fechaSeleccionada[0], horaSeleccionada[0]);
                        Toast.makeText(this, "Recordatorio actualizado", Toast.LENGTH_SHORT).show();
                    }
                    cargarRecordatorios();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditar(RecordatorioItem r) {
        mostrarFormulario(r);
    }

    private void confirmarEliminar(RecordatorioItem r) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar recordatorio")
                .setMessage("¿Eliminar este recordatorio?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.eliminarRecordatorio(r.getId());
                    Toast.makeText(this, "Recordatorio eliminado", Toast.LENGTH_SHORT).show();
                    cargarRecordatorios();
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
