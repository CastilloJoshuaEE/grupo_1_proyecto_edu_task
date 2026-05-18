package com.grupo1.edutask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int estudianteId;
    private String estudianteNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences prefs = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE);
        estudianteId = prefs.getInt("estudianteId", -1);
        estudianteNombre = prefs.getString("estudianteNombre", "Estudiante");
        String rol = prefs.getString("rol", "estudiante");

        if (estudianteId == -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        TextView tvNombreNav = navigationView.getHeaderView(0).findViewById(R.id.tvNombreNav);
        if (tvNombreNav != null) {
            tvNombreNav.setText(estudianteNombre);
        }

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        tvBienvenida.setText("¡Hola, " + estudianteNombre + "!");

        Menu menu = navigationView.getMenu();
        MenuItem itemAdmin = menu.findItem(R.id.nav_admin);

        if (itemAdmin != null) {
            itemAdmin.setVisible(rol.equals("administrador"));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();

            if (id == R.id.nav_tareas) {
                abrirTareas();

            } else if (id == R.id.nav_apuntes) {
                abrirApuntes();

            } else if (id == R.id.nav_recordatorios) {
                abrirRecordatorios();

            } else if (id == R.id.nav_perfil) {
                abrirPerfil();

            } else if (id == R.id.nav_admin) {
                abrirAdminEstudiantes();

            } else if (id == R.id.nav_acerca) {
                AcercaDeDialog.newInstance().show(getSupportFragmentManager(), "AcercaDe");

            } else if (id == R.id.nav_salir) {
                confirmarCerrarSesion();
            }

            return true;
        });

        // Cards del dashboard
        CardView cardTareas = findViewById(R.id.cardTareas);
        CardView cardApuntes = findViewById(R.id.cardApuntes);
        CardView cardCategorias = findViewById(R.id.cardCategorias);
        CardView cardPerfil = findViewById(R.id.cardPerfil);
        CardView cardCerrar = findViewById(R.id.cardCerrar);
        CardView cardAdmin = findViewById(R.id.cardAdmin);
        CardView cardRecordatorios = findViewById(R.id.cardRecordatorios);

        if (cardTareas != null) {
            cardTareas.setOnClickListener(v -> abrirTareas());
        }

        if (cardApuntes != null) {
            cardApuntes.setOnClickListener(v -> abrirApuntes());
        }

        if (cardCategorias != null) {
            cardCategorias.setOnClickListener(v -> abrirCategorias());
        }

        if (cardPerfil != null) {
            cardPerfil.setOnClickListener(v -> abrirPerfil());
        }

        if (cardCerrar != null) {
            cardCerrar.setOnClickListener(v -> confirmarCerrarSesion());
        }

        if (cardRecordatorios != null) {
            cardRecordatorios.setOnClickListener(v -> abrirRecordatorios());
        }

        if (cardAdmin != null) {
            cardAdmin.setVisibility(rol.equals("administrador") ? View.VISIBLE : View.GONE);
            cardAdmin.setOnClickListener(v -> abrirAdminEstudiantes());
        }
    }

    private void abrirTareas() {
        startActivity(new Intent(this, TareasActivity.class));
    }

    private void abrirApuntes() {
        startActivity(new Intent(this, ApuntesActivity.class));
    }

    private void abrirCategorias() {
        startActivity(new Intent(this, CategoriasActivity.class));
    }

    private void abrirRecordatorios() {
        startActivity(new Intent(this, RecordatoriosActivity.class));
    }

    private void abrirPerfil() {
        startActivity(new Intent(this, PerfilActivity.class));
    }

    private void abrirAdminEstudiantes() {
        startActivity(new Intent(this, AdminEstudiantesActivity.class));
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Sí", (d, w) -> cerrarSesion())
                .setNegativeButton("No", null)
                .show();
    }

    private void cerrarSesion() {
        SharedPreferences.Editor editor = getSharedPreferences("EduTaskPrefs", MODE_PRIVATE).edit();

        editor.remove("estudianteId");
        editor.remove("estudianteNombre");
        editor.remove("correo");
        editor.remove("password");
        editor.remove("rol");
        editor.apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}