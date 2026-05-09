package com.grupo1.edutask;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AcercaDeDialog extends DialogFragment {

    public static AcercaDeDialog newInstance() {
        return new AcercaDeDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_acerca_de, null);

        TextView tvIntegrantes = view.findViewById(R.id.tv_integrantes);
        Button btnCerrar       = view.findViewById(R.id.btn_cerrar);

        String info =
                "UNIVERSIDAD DE GUAYAQUIL\n" +
                        "FACULTAD DE CIENCIAS MATEMÁTICAS Y FÍSICAS\n" +
                        "CARRERA INGENIERÍA EN SOFTWARE\n\n" +
                        "DESARROLLO DE APLICACIONES MÓVILES\n" +
                        "DOCENTE: ING. CHARCO AGUIRRE JORGE LUIS\n\n" +
                        "CURSO: SOF-S-VE-8-4\n\n" +
                        "=== INTEGRANTES DEL GRUPO #1 ===\n\n" +
                        "• CASTILLO MEREJILDO JOSHUA JAVIER\n" +
                        "• ESPINOZA GOMEZ JENNIFFER MARISOL\n" +
                        "• GABINO VILLAO JOEL FABIAN\n" +
                        "• PARRA AGUAYO KEVIN JOEL\n" +
                        "• VERA CHUQUIMARCA LESLIE ARIANNA\n\n" +
                        "PROYECTO: EduTask\n" +
                        "Aplicación móvil para la gestión académica\n\n" +
                        "AÑO: 2026 – 2027 Ciclo I";

        tvIntegrantes.setText(info);
        btnCerrar.setOnClickListener(v -> dismiss());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("ACERCA DE")
                .setView(view)
                .setCancelable(true);

        return builder.create();
    }
}