package mx.ita.sneaker_app3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import mx.ita.sneaker_app3.Modal.Productos;

public class ProductoDetallesActivity extends AppCompatActivity {

    private Button agregarCarrito;
    private EditText numeroBoton;
    private ImageView productoImagen;
    private TextView productoPrecio, productoDescripcion, productoNombre;
    private String productoID ="", estado = "Normal", CurrentUserID;
    private FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalles);

        productoID = getIntent().getStringExtra("pid");

        agregarCarrito = (Button) findViewById(R.id.boton_siguiente_detalles);
        numeroBoton = (EditText) findViewById(R.id.cantidad_producto_detalles);
        productoImagen = (ImageView) findViewById(R.id.producto_imagen_detalles);
        productoPrecio = (TextView) findViewById(R.id.producto_precio_detalles);
        productoDescripcion = (TextView) findViewById(R.id.producto_descripcion_detalles);
        productoNombre = (TextView) findViewById(R.id.producto_nombre_detalles);
        ObtenerDatosProducto(productoID);
        auth=FirebaseAuth.getInstance();
        CurrentUserID= auth.getCurrentUser().getUid();

        agregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(estado.equals("Pedido") || estado.equals("Enviado")){
                    Toast.makeText(ProductoDetallesActivity.this, "Espérando a que finalice el primer pedido...", Toast.LENGTH_SHORT).show();

                }else{
                    agregarAlaLista();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        VerificarEstadoyOrden();
    }



    private void agregarAlaLista() {
        String CurrentTime, CurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat data = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate = data.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        CurrentTime = time.format(calendar.getTime());

        final DatabaseReference CartListRef = FirebaseDatabase.getInstance().getReference().child("Carrito");
        final HashMap<String, Object> map = new HashMap<>();
        map.put("pid", productoID);
        map.put("nombre", productoNombre.getText().toString());
        map.put("precio", productoPrecio.getText().toString());
        map.put("fecha", CurrentDate);
        map.put("hora", CurrentTime);
        map.put("cantidad", numeroBoton.getText().toString());
        map.put("descuento", "");

        CartListRef.child("Usuario Compra").child(CurrentUserID).child("Productos").child(productoID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    CartListRef.child("Administracion").child(CurrentUserID).child("Productos").child(productoID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProductoDetallesActivity.this, "Agregado..", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ProductoDetallesActivity.this,PrincipalActivity.class);
                                startActivity(i);
                            }
                        }
                    });
                }
            }
        });


    }

    private void ObtenerDatosProducto(String productoID) {
        DatabaseReference ProductoRef = FirebaseDatabase.getInstance().getReference().child("Productos");
        ProductoRef.child(productoID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Productos productos = snapshot.getValue(Productos.class);
                    productoNombre.setText(productos.getNombre());
                    productoDescripcion.setText(productos.getDescripcion());
                    productoPrecio.setText(productos.getPrecioven());
                    Picasso.get().load(productos.getImagen()).into(productoImagen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void VerificarEstadoyOrden() {

        DatabaseReference OrdenRef ;
        OrdenRef= FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserID);
        OrdenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String envioEstado = snapshot.child("estado").getValue().toString();
                    if(envioEstado.equals("Enviado")){
                        estado="Enviado";
                    }else if(envioEstado.equals("No Enviado")){
                            estado="Pedido";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}