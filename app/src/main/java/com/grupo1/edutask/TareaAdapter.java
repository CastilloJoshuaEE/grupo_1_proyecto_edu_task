package com.grupo1.edutask;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.VH> {

    public interface OnEditListener   { void onEdit(TareaItem t); }
    public interface OnDeleteListener { void onDelete(TareaItem t); }
    public interface OnToggleListener { void onToggle(TareaItem t, boolean completada); }

    private final List<TareaItem> lista;
    private final OnEditListener   onEdit;
    private final OnDeleteListener onDelete;
    private final OnToggleListener onToggle;

    public TareaAdapter(List<TareaItem> lista, OnEditListener e, OnDeleteListener d, OnToggleListener t) {
        this.lista    = lista;
        this.onEdit   = e;
        this.onDelete = d;
        this.onToggle = t;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        TareaItem t = lista.get(pos);

        h.tvTitulo.setText(t.getTitulo());
        h.tvFecha.setText("📅 " + (t.getFecha() != null ? t.getFecha() : "Sin fecha"));
        h.tvPrioridad.setText("⚡ " + t.getPrioridad());
        h.tvCategoria.setText(t.getCategoriaNombre() != null ? "📚 " + t.getCategoriaNombre() : "");

        h.checkCompletada.setOnCheckedChangeListener(null);
        h.checkCompletada.setChecked(t.isCompletada());

        if (t.isCompletada()) {
            h.tvTitulo.setPaintFlags(h.tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvTitulo.setAlpha(0.5f);
        } else {
            h.tvTitulo.setPaintFlags(h.tvTitulo.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvTitulo.setAlpha(1f);
        }

        h.checkCompletada.setOnCheckedChangeListener((b, checked) -> onToggle.onToggle(t, checked));
        h.btnEditar.setOnClickListener(v -> onEdit.onEdit(t));
        h.btnEliminar.setOnClickListener(v -> onDelete.onDelete(t));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvPrioridad, tvCategoria;
        CheckBox checkCompletada;
        ImageButton btnEditar, btnEliminar;

        VH(@NonNull View v) {
            super(v);
            tvTitulo       = v.findViewById(R.id.tvTituloTarea);
            tvFecha        = v.findViewById(R.id.tvFechaTarea);
            tvPrioridad    = v.findViewById(R.id.tvPrioridadTarea);
            tvCategoria    = v.findViewById(R.id.tvCategoriaTarea);
            checkCompletada = v.findViewById(R.id.checkCompletada);
            btnEditar      = v.findViewById(R.id.btnEditarTarea);
            btnEliminar    = v.findViewById(R.id.btnEliminarTarea);
        }
    }
}