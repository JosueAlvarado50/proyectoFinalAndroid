package mx.ita.sneaker_app3;

import android.content.ClipData;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ProductoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productoNom, productoDescripcion, productoPrecio, productoCantidad;
    public ImageView productoImagen;
    public ItemClickListener listener;

    public ProductoViewHolder(View itemView){
        super(itemView);

        productoNom = (TextView) itemView.findViewById(R.id.producto_nombre);
        productoDescripcion = (TextView) itemView.findViewById(R.id.producto_descripcion);
        productoCantidad = (TextView) itemView.findViewById(R.id.producto_cantidad);
        productoPrecio = (TextView) itemView.findViewById(R.id.producto_precio);
        productoImagen = (ImageView) itemView.findViewById(R.id.producto_imagen);



    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener= listener;

    }


    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);

    }
}
