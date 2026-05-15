package com.grupo1.edutask;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<CategoriaItem> listaCategorias;
    private OnCategoriaClickListener listener;

    public interface OnCategoriaClickListener {
        void onEditClick(CategoriaItem item);
        void onDeleteClick(CategoriaItem item);
    }

    public CategoriaAdapter(List<CategoriaItem> listaCategorias, OnCategoriaClickListener listener) {
        this.listaCategorias = listaCategorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        CategoriaItem item = listaCategorias.get(position);
        holder.tvNombre.setText(item.getNombre());

        // Pintar el indicador del color de la categoría de forma dinámica
        try {
            GradientDrawable bgShape = (GradientDrawable) holder.viewColor.getBackground();
            bgShape.setColor(Color.parseColor(item.getColor()));
        } catch (Exception e) {
            holder.viewColor.setBackgroundColor(Color.GRAY);
        }

        holder.btnEditar.setOnClickListener(v -> listener.onEditClick(item));
        holder.btnEliminar.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public void actualizarLista(List<CategoriaItem> nuevaLista) {
        this.listaCategorias = nuevaLista;
        notifyDataSetChanged();
    }

    // El ViewHolder propio para este adaptador
    static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvNombre;
        ImageButton btnEditar, btnEliminar;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.viewColorCategoria);
            tvNombre = itemView.findViewById(R.id.tvNombreCategoria);
            btnEditar = itemView.findViewById(R.id.btnEditarCategoria);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCategoria);
        }
    }
}