package com.grupo1.edutask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordatorioAdapter extends RecyclerView.Adapter<RecordatorioAdapter.VH> {

    public interface OnEditListener   { void onEdit(RecordatorioItem r); }
    public interface OnDeleteListener { void onDelete(RecordatorioItem r); }

    private final List<RecordatorioItem> lista;
    private final OnEditListener   onEdit;
    private final OnDeleteListener onDelete;

    public RecordatorioAdapter(List<RecordatorioItem> lista, OnEditListener e, OnDeleteListener d) {
        this.lista   = lista;
        this.onEdit  = e;
        this.onDelete = d;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recordatorio, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RecordatorioItem r = lista.get(pos);
        h.tvMensaje.setText(r.getMensaje());
        h.tvFecha.setText("📅 " + (r.getFecha() != null ? r.getFecha() : ""));
        h.tvHora.setText("🕐 " + (r.getHora() != null ? r.getHora() : ""));
        h.btnEditar.setOnClickListener(v -> onEdit.onEdit(r));
        h.btnEliminar.setOnClickListener(v -> onDelete.onDelete(r));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvMensaje, tvFecha, tvHora;
        ImageButton btnEditar, btnEliminar;

        VH(@NonNull View v) {
            super(v);
            tvMensaje  = v.findViewById(R.id.tvMensajeRecordatorio);
            tvFecha    = v.findViewById(R.id.tvFechaRecordatorio);
            tvHora     = v.findViewById(R.id.tvHoraRecordatorio);
            btnEditar  = v.findViewById(R.id.btnEditarRecordatorio);
            btnEliminar = v.findViewById(R.id.btnEliminarRecordatorio);
        }
    }
}
