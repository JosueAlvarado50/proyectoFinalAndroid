package mx.ita.sneaker_app3;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarritoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView carProductoNombre, carProductoCantidad, carProductoPrecio;
    private ItemClickListener itemClickListener ;


    public CarritoViewHolder(@NonNull View itemView) {
        super(itemView);
        carProductoNombre=itemView.findViewById(R.id.car_producto_nombre);
        carProductoCantidad=itemView.findViewById(R.id.car_producto_cantidad);
        carProductoPrecio=itemView.findViewById(R.id.car_producto_precio);

    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
}
