package com.grupo1.edutask;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText campoNombre;
    private TextInputEditText campoCorreo;
    private TextInputEditText campoPassword;
    private TextInputEditText campoConfirmar;
    private TextInputEditText campoCedula;

    private Spinner spinnerCarrera;
    private Spinner spinnerRol;

    private RadioGroup radioGroupSexo;
    private TextView txtFechaNac;

    private MaterialButton botonRegistrar;
    private Button botonCancelar;

    private String fechaSeleccionada = "";
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        db = DatabaseHelper.getInstance(this);

        campoNombre    = findViewById(R.id.campoNombre);
        campoCorreo    = findViewById(R.id.campoCorreoReg);
        campoPassword  = findViewById(R.id.campoPasswordReg);
        campoConfirmar = findViewById(R.id.campoConfirmar);
        campoCedula    = findViewById(R.id.campoCedula);

        spinnerCarrera  = findViewById(R.id.spinnerCarrera);
        spinnerRol = findViewById(R.id.spinnerRol);
        radioGroupSexo  = findViewById(R.id.radioGroupSexo);
        txtFechaNac     = findViewById(R.id.txtFechaNac);

        botonRegistrar  = findViewById(R.id.botonRegistrar);
        botonCancelar   = findViewById(R.id.botonCancelar);

        configurarSpinner();
        configurarSpinnerRol();
        configurarFecha();

        botonRegistrar.setOnClickListener(v -> registrar());
        botonCancelar.setOnClickListener(v -> finish());
    }

    private void configurarSpinner() {
        String[] carreras = {
                "Seleccione carrera...",
                "Ingeniería en Software",
                "Ingeniería en Sistemas",
                "Ingeniería en Redes",
                "Ingeniería Civil",
                "Medicina",
                "Derecho",
                "Economía",
                "Arquitectura",
                "Otra"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, carreras);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarrera.setAdapter(adapter);
    }
    private void configurarSpinnerRol() {
        String[] roles = {"estudiante", "administrador"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);
    }
    private void configurarFecha() {
        txtFechaNac.setOnClickListener(v -> {
            DatePickerFragment dp = new DatePickerFragment();
            dp.setOnDateSelectedListener((year, month, day) -> {
                fechaSeleccionada = String.format(Locale.getDefault(),
                        "%02d/%02d/%04d", day, month + 1, year);
                txtFechaNac.setText(fechaSeleccionada);
            });
            dp.show(getSupportFragmentManager(), "DatePicker");
        });
    }

    private void registrar() {
        String nombre    = campoNombre.getText() != null ? campoNombre.getText().toString().trim() : "";
        String correo    = campoCorreo.getText() != null ? campoCorreo.getText().toString().trim() : "";
        String password  = campoPassword.getText() != null ? campoPassword.getText().toString().trim() : "";
        String confirmar = campoConfirmar.getText() != null ? campoConfirmar.getText().toString().trim() : "";
        String cedula    = campoCedula.getText() != null ? campoCedula.getText().toString().trim() : "";
        String rol       = spinnerRol.getSelectedItem() != null ? spinnerRol.getSelectedItem().toString() : "";

        if (nombre.isEmpty()) {
            campoNombre.setError("Ingrese su nombre");
            campoNombre.requestFocus(); return;
        }
        if (!nombre.matches("[a-zA-ZáéíóúñÑ ]+")) {
            campoNombre.setError("Solo letras");
            campoNombre.requestFocus(); return;
        }
        if (correo.isEmpty()) {
            campoCorreo.setError("Ingrese su correo");
            campoCorreo.requestFocus(); return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            campoCorreo.setError("Correo inválido");
            campoCorreo.requestFocus(); return;
        }
        if (password.isEmpty()) {
            campoPassword.setError("Ingrese contraseña");
            campoPassword.requestFocus(); return;
        }
        if (password.length() < 6) {
            campoPassword.setError("Mínimo 6 caracteres");
            campoPassword.requestFocus(); return;
        }
        if (!password.equals(confirmar)) {
            campoConfirmar.setError("Las contraseñas no coinciden");
            campoConfirmar.requestFocus(); return;
        }
        if (!cedula.isEmpty() && cedula.length() != 10) {
            campoCedula.setError("La cédula debe tener 10 dígitos");
            campoCedula.requestFocus(); return;
        }
        if (spinnerCarrera.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione su carrera", Toast.LENGTH_SHORT).show(); return;
        }
        if (radioGroupSexo.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleccione sexo", Toast.LENGTH_SHORT).show(); return;
        }
        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Seleccione fecha de nacimiento", Toast.LENGTH_SHORT).show(); return;
        }

        if (db.correoExiste(correo)) {
            campoCorreo.setError("Este correo ya está registrado");
            campoCorreo.requestFocus(); return;
        }

        long id = db.insertarEstudiante(nombre, correo, password, cedula, rol);

        if (id > 0) {
            Toast.makeText(this, "¡Registro exitoso! Ya puede iniciar sesión.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }
}