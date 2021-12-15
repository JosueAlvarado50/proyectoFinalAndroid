package mx.ita.sneaker_app3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmarordenActivity extends AppCompatActivity {

    private EditText nombre, correo, direccion, telefono;
    private Button confirmar;
    private String totalPago= "";
    private FirebaseAuth auth;
    private String CurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmarorden);

        totalPago = getIntent().getStringExtra("Total");
        Toast.makeText(this, "Total a pagar: $ "+totalPago, Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();
        CurrentUserID=auth.getCurrentUser().getUid();

        nombre = (EditText) findViewById(R.id.confirma_nombre);
        correo = (EditText) findViewById(R.id.confirma_correo);
        direccion = (EditText) findViewById(R.id.confirma_direccion);
        telefono = (EditText) findViewById(R.id.confirma_telefono);

        confirmar = (Button) findViewById(R.id.confirma_boton);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificarDatos();
            }
        });

    }

    private void VerificarDatos() {


        if(TextUtils.isEmpty(nombre.getText().toString())){
            Toast.makeText(this, "Ingresa nombre", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(correo.getText().toString())){
            Toast.makeText(this, "Ingresa correo", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(direccion.getText().toString())){
            Toast.makeText(this, "Ingresa direccion", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(telefono.getText().toString())){
            Toast.makeText(this, "Ingresa telefono", Toast.LENGTH_SHORT).show();
        }else{
            ConfirmarOrden();
        }
    }

    private void ConfirmarOrden() {

        final String CurrentTime, CurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat data = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate = data.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        CurrentTime = time.format(calendar.getTime());

        final DatabaseReference OrdenesRef = FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserID);

        HashMap<String, Object>map = new HashMap<>();
        map.put("total", totalPago);
        map.put("nombre", nombre.getText().toString());
        map.put("correo", correo.getText().toString());
        map.put("direccion", direccion.getText().toString());
        map.put("telefono", telefono.getText().toString());
        map.put("fecha", CurrentDate);
        map.put("hora", CurrentTime);
        map.put("estado", "Enviado");

        OrdenesRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Carrito")
                            .child("Usuario Compra")
                            .child(CurrentUserID).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfirmarordenActivity.this, "Su pedido se ha realizado..", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(ConfirmarordenActivity.this, PrincipalActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}