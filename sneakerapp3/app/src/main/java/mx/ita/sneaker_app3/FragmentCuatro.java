package mx.ita.sneaker_app3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import mx.ita.sneaker_app3.Clases.Ordenes;

public class FragmentCuatro extends Fragment {
    private View Vista;
    private RecyclerView recycler;
    private DatabaseReference OrdenRef;


    public FragmentCuatro() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Vista = inflater.inflate(R.layout.fragment_cuatro, container, false);
        OrdenRef = FirebaseDatabase.getInstance().getReference().child("Ordenes");
        recycler = (RecyclerView) Vista.findViewById(R.id.recycler_ordenes);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return Vista;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Ordenes>().setQuery(OrdenRef, Ordenes.class).build();
        FirebaseRecyclerAdapter<Ordenes, OrdenesViewHolder> adapter = new FirebaseRecyclerAdapter<Ordenes, OrdenesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrdenesViewHolder holder, int position, @NonNull Ordenes model) {
                holder.nombre.setText("Cliente: "+model.getNombre());
                holder.telefono.setText("Tel: " +model.getTelefono());
                holder.precio.setText("Total: $ "+model.getTotal());
                holder.correo.setText("Correo: " +model.getCorreo() + "\nDir: " + model.getDireccion());
                holder.fecha.setText("Fecha: "+model.getFecha()+ "Hora: " + model.getHora());
                holder.nombre.setText(model.getNombre());
                holder.boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Correcto", Toast.LENGTH_SHORT).show();


                    }
                });




            }

            @NonNull
            @Override
            public OrdenesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordenes_layout, parent, false);
                OrdenesViewHolder viewHolder = new OrdenesViewHolder(view);
                return viewHolder;
            }
        };

        recycler.setAdapter(adapter);
        adapter.startListening();

    }
    public static class OrdenesViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, telefono, precio, correo,  fecha;
        Button boton ;

        public OrdenesViewHolder(View itemView){
            super(itemView);
            nombre = itemView.findViewById(R.id.orden_nombre);
            telefono = itemView.findViewById(R.id.orden_telefono);
            precio = itemView.findViewById(R.id.orden_precio);
            fecha = itemView.findViewById(R.id.orden_fecha);
            correo = itemView.findViewById(R.id.orden_correo);
            boton = (Button) itemView.findViewById(R.id.orden_ver);

        }
    }
}
