package com.grupo1.edutask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PerfilActivity extends AppCompatActivity {

    private TextInputEditText campoNombre;
    private TextInputEditText campoCorreo;
    private TextInputEditText campoCedula;
    private TextInputEditText campoPasswordNueva;
    private TextInputEditText campoPasswordActual;

    private MaterialButton botonGuardar;
    private MaterialButton botonEliminarCuenta;

    private DatabaseHelper db;
    private int estudianteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarPerfil);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db           = DatabaseHelper.getInstance(this);
        estudianteId = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).getInt("estudianteId", -1);

        campoNombre        = findViewById(R.id.campoNombrePerfil);
        campoCorreo        = findViewById(R.id.campoCorreoPerfil);
        campoCedula        = findViewById(R.id.campoCedulaPerfil);
        campoPasswordNueva = findViewById(R.id.campoPasswordNueva);
        campoPasswordActual = findViewById(R.id.campoPasswordActual);
        botonGuardar       = findViewById(R.id.botonGuardarPerfil);
        botonEliminarCuenta = findViewById(R.id.botonEliminarCuenta);

        cargarDatos();

        botonGuardar.setOnClickListener(v -> guardarCambios());
        botonEliminarCuenta.setOnClickListener(v -> confirmarEliminarCuenta());
    }

    private void cargarDatos() {
        Cursor c = db.getEstudiante(estudianteId);
        if (c.moveToFirst()) {
            campoNombre.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_NOMBRE)));
            campoCorreo.setText(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_CORREO)));
            String ced = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_CEDULA));
            if (ced != null) campoCedula.setText(ced);
        }
        c.close();
        campoCorreo.setEnabled(false);
    }

    private void guardarCambios() {
        String nombre   = campoNombre.getText() != null ? campoNombre.getText().toString().trim() : "";
        String cedula   = campoCedula.getText() != null ? campoCedula.getText().toString().trim() : "";
        String passAct  = campoPasswordActual.getText() != null ? campoPasswordActual.getText().toString().trim() : "";
        String passNueva = campoPasswordNueva.getText() != null ? campoPasswordNueva.getText().toString().trim() : "";

        if (nombre.isEmpty()) {
            campoNombre.setError("Ingrese su nombre");
            campoNombre.requestFocus();
            return;
        }

        // Si quiere cambiar contraseña
        String passwordFinal = null;
        if (!passNueva.isEmpty()) {
            if (passAct.isEmpty()) {
                campoPasswordActual.setError("Ingrese su contraseña actual");
                campoPasswordActual.requestFocus();
                return;
            }
            Cursor c = db.getEstudiante(estudianteId);
            String passDB = "";
            if (c.moveToFirst()) {
                passDB = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_PASSWORD));
            }
            c.close();
            if (!passAct.equals(passDB)) {
                campoPasswordActual.setError("Contraseña incorrecta");
                campoPasswordActual.requestFocus();
                return;
            }
            if (passNueva.length() < 6) {
                campoPasswordNueva.setError("Mínimo 6 caracteres");
                campoPasswordNueva.requestFocus();
                return;
            }
            passwordFinal = passNueva;
        } else {
            Cursor c = db.getEstudiante(estudianteId);
            if (c.moveToFirst()) {
                passwordFinal = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EST_PASSWORD));
            }
            c.close();
        }

        int rows = db.actualizarEstudiante(estudianteId, nombre, passwordFinal, cedula);
        if (rows > 0) {
            getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).edit()
                    .putString("estudianteNombre", nombre).apply();
            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            campoPasswordActual.setText("");
            campoPasswordNueva.setText("");
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarCuenta() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta")
                .setMessage("¿Está seguro de que desea eliminar su cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.desactivarEstudiante(estudianteId);
                    getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).edit().clear().apply();
                    Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finishAffinity();
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