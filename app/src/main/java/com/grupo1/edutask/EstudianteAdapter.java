package com.grupo1.edutask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EstudianteAdapter extends RecyclerView.Adapter<EstudianteAdapter.ViewHolder> {

    public interface OnEditListener {
        void onEdit(EstudianteItem estudiante);
    }

    public interface OnDeleteListener {
        void onDelete(EstudianteItem estudiante);
    }

    private List<EstudianteItem> lista;
    private OnEditListener onEditListener;
    private OnDeleteListener onDeleteListener;

    public EstudianteAdapter(List<EstudianteItem> lista, OnEditListener editListener, OnDeleteListener deleteListener) {
        this.lista = lista;
        this.onEditListener = editListener;
        this.onDeleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estudiante, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EstudianteItem e = lista.get(position);

        holder.tvNombre.setText(e.getNombre());
        holder.tvCorreo.setText(e.getCorreo());
        holder.tvCedula.setText("📄 Cédula: " + e.getCedula());
        
        if (e.getRol().equals("administrador")) {
            holder.tvRol.setText("👑 Administrador");
            holder.tvRol.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.colorPrimario));
        } else {
            holder.tvRol.setText("📚 Estudiante");
            holder.tvRol.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.colorSecundario));
        }

        holder.btnEditar.setOnClickListener(v -> onEditListener.onEdit(e));
        holder.btnEliminar.setOnClickListener(v -> onDeleteListener.onDelete(e));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCorreo, tvCedula;
        CardView tvRol;
        ImageButton btnEditar, btnEliminar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEstudiante);
            tvCorreo = itemView.findViewById(R.id.tvCorreoEstudiante);
            tvCedula = itemView.findViewById(R.id.tvCedulaEstudiante);
            tvRol = itemView.findViewById(R.id.tvRolEstudiante);
            btnEditar = itemView.findViewById(R.id.btnEditarEstudiante);
            btnEliminar = itemView.findViewById(R.id.btnEliminarEstudiante);
        }
    }
}