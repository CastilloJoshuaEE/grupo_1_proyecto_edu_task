package com.grupo1.edutask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText campoCorreo;
    private TextInputEditText campoPassword;
    private CheckBox checkRecordar;
    private MaterialButton botonLogin;
    private MaterialButton botonRegistro;
    private MaterialButton botonAcercaDe;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DatabaseHelper.getInstance(this);

        campoCorreo   = findViewById(R.id.campoCorreo);
        campoPassword = findViewById(R.id.campoPassword);
        checkRecordar = findViewById(R.id.checkRecordar);
        botonLogin    = findViewById(R.id.botonLogin);
        botonRegistro = findViewById(R.id.botonRegistro);
        botonAcercaDe = findViewById(R.id.botonAcercaDe);

        // Autocompletar si hay sesión guardada
        SharedPreferences prefs = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE);
        String correoGuardado = prefs.getString("correo", "");
        String passGuardada   = prefs.getString("password", "");

        if (!correoGuardado.isEmpty()) {
            campoCorreo.setText(correoGuardado);
            campoPassword.setText(passGuardada);
            checkRecordar.setChecked(true);
        }

        botonLogin.setOnClickListener(v -> validarLogin());

        botonRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));

        botonAcercaDe.setOnClickListener(v ->
                AcercaDeDialog.newInstance().show(getSupportFragmentManager(), "AcercaDe"));
    }

    private void validarLogin() {
        String correo = campoCorreo.getText() != null ? campoCorreo.getText().toString().trim() : "";
        String pass   = campoPassword.getText() != null ? campoPassword.getText().toString().trim() : "";
        String rol = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EST_ROL));

        if (correo.isEmpty()) {
            campoCorreo.setError("Ingrese su correo");
            campoCorreo.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            campoCorreo.setError("Correo inválido");
            campoCorreo.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            campoPassword.setError("Ingrese su contraseña");
            campoPassword.requestFocus();
            return;
        }

        Cursor cursor = db.loginEstudiante(correo, pass);

        if (cursor != null && cursor.moveToFirst()) {
            int id     = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EST_ID));
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EST_NOMBRE));
            String rol = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EST_ROL));
            cursor.close();

            SharedPreferences.Editor editor = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).edit();
            editor.putInt("estudianteId", id);
            editor.putString("estudianteNombre", nom);
            editor.putString("rol", rol);
            if (checkRecordar.isChecked()) {
                editor.putString("correo", correo);
                editor.putString("password", pass);
            } else {
                editor.remove("correo");
                editor.remove("password");
            }
            editor.apply();

            Toast.makeText(this, "¡Bienvenido, " + nom + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}