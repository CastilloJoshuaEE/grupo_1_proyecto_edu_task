package com.grupo1.edutask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApunteAdapter extends RecyclerView.Adapter<ApunteAdapter.VH> {

    public interface OnEditListener   { void onEdit(ApunteItem a); }
    public interface OnDeleteListener { void onDelete(ApunteItem a); }

    private final List<ApunteItem> lista;
    private final OnEditListener   onEdit;
    private final OnDeleteListener onDelete;

    public ApunteAdapter(List<ApunteItem> lista, OnEditListener e, OnDeleteListener d) {
        this.lista    = lista;
        this.onEdit   = e;
        this.onDelete = d;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apunte, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ApunteItem a = lista.get(pos);
        h.tvTitulo.setText(a.getTitulo());
        h.tvContenido.setText(a.getContenido() != null ? a.getContenido() : "");
        h.tvFecha.setText("📅 " + (a.getFecha() != null ? a.getFecha() : ""));
        h.tvCategoria.setText(a.getCategoriaNombre() != null ? "📚 " + a.getCategoriaNombre() : "");

        h.btnEditar.setOnClickListener(v -> onEdit.onEdit(a));
        h.btnEliminar.setOnClickListener(v -> onDelete.onDelete(a));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvContenido, tvFecha, tvCategoria;
        ImageButton btnEditar, btnEliminar;

        VH(@NonNull View v) {
            super(v);
            tvTitulo    = v.findViewById(R.id.tvTituloApunte);
            tvContenido = v.findViewById(R.id.tvContenidoApunte);
            tvFecha     = v.findViewById(R.id.tvFechaApunte);
            tvCategoria = v.findViewById(R.id.tvCategoriaApunte);
            btnEditar   = v.findViewById(R.id.btnEditarApunte);
            btnEliminar = v.findViewById(R.id.btnEliminarApunte);
        }
    }
}