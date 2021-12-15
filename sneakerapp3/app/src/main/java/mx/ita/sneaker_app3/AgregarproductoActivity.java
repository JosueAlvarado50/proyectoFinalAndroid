package mx.ita.sneaker_app3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class AgregarproductoActivity extends AppCompatActivity {
    private ImageView imagenpro;
    private EditText nombrepro, descripcionpro, preciocomprapro, precioventapro, cantidadpro;
    private TextView textox;
    private Button agregarpro;
    private static final int  Gallery_pick = 1;
    private Uri imagenUri;
    private String productoRandomKey, downloadUri;
    private StorageReference ProductoImagenRef;
    private DatabaseReference ProductoRef;
    private ProgressDialog dialog;
    private String Categoria, Nom, Desc, PrecioCom, PrecioVen, Cant, CurrentDate, CurrentTime ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregarproducto);

        Categoria = getIntent().getExtras().get("categoria").toString();
        ProductoImagenRef = FirebaseStorage.getInstance().getReference().child("Imagenes productos");
        ProductoRef = FirebaseDatabase.getInstance().getReference().child("Productos");

        textox = (TextView) findViewById(R.id.textox);
        imagenpro = (ImageView) findViewById(R.id.imagenpro);
        nombrepro = (EditText) findViewById(R.id.nombrepro);
        descripcionpro = (EditText) findViewById(R.id.descripcionpro);
        preciocomprapro = (EditText) findViewById(R.id.preciocomprapro);
        precioventapro = (EditText) findViewById(R.id.precioventapro);
        cantidadpro = (EditText) findViewById(R.id.cantidadpro);

        agregarpro = (Button) findViewById(R.id.agregarpro);

        dialog = new ProgressDialog(this);

        imagenpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirGaleria();
            }
        });
        agregarpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidarProducto();
            }
        });
        textox.setText(Categoria+"\nAgregar producto");

    }

    private void AbrirGaleria(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/");
        startActivityForResult(i, Gallery_pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data != null){
            imagenUri = data.getData();
            imagenpro.setImageURI(imagenUri);
        }
    }

    private void ValidarProducto(){
        Nom = nombrepro.getText().toString();
        Desc = descripcionpro.getText().toString();
        PrecioCom = preciocomprapro.getText().toString();
        PrecioVen = precioventapro.getText().toString();
        Cant = cantidadpro.getText().toString();



        if(imagenUri == null){
            Toast.makeText(this, "Primero agrega una imagen", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Nom)){
            Toast.makeText(this, "Debes ingresar nombre del producto", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(Desc)){
            Toast.makeText(this, "Debes ingresar descripcion del producto", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(PrecioCom)){
            Toast.makeText(this, "Debes ingresar precio de compra del producto", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(PrecioVen)){
            Toast.makeText(this, "Debes ingresar precio de venta del producto", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(Cant)){
            Toast.makeText(this, "Debes ingresar csntidad del producto", Toast.LENGTH_SHORT).show();

        }else{
            GuardarInformacionProducto();
        }

    }

    private void GuardarInformacionProducto(){

        dialog.setTitle("Guardando Producto");
        dialog.setMessage( "Espere mientras guardamos ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat curreDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate = curreDateFormat.format(calendar.getTime());

        SimpleDateFormat CurrenTimeFormat = new SimpleDateFormat("HH:mm:ss");
        CurrentTime = CurrenTimeFormat.format(calendar.getTime());

        productoRandomKey = CurrentDate + CurrentTime;//llave para que no se repitan

        final StorageReference filePath = ProductoImagenRef.child(imagenUri.getPathSegments()+ productoRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imagenUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    String mensaje = e.toString();
                Toast.makeText(AgregarproductoActivity.this, "Error: "+ mensaje, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarproductoActivity.this, "Imagen Guardada exitosamente", Toast.LENGTH_SHORT).show();
                Task<Uri>uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                         if(!task.isSuccessful()){
                             throw task.getException();
                         }
                         downloadUri = filePath.getDownloadUrl().toString();
                         return filePath.getDownloadUrl();//ubicacion imagen
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUri = task.getResult().toString();
                            Toast.makeText(AgregarproductoActivity.this, "Imagen guardada en firebase", Toast.LENGTH_SHORT).show();
                            GuardarEnFirebase();
                        }else{
                            Toast.makeText(AgregarproductoActivity.this, "Error...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }

    private void GuardarEnFirebase(){
        HashMap<String,Object>map = new HashMap<>();
        map.put("pid",productoRandomKey);
        map.put("fecha",CurrentDate);
        map.put("hora",CurrentTime);
        map.put("descripcion",Desc);
        map.put("nombre",Nom);
        map.put("preciocom",PrecioCom);
        map.put("precioven",PrecioVen);
        map.put("cantidad",Cant);
        map.put("imagen",downloadUri);
        map.put("categoria",Categoria);

        ProductoRef.child(productoRandomKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent i = new Intent(AgregarproductoActivity.this, AdminActivity.class);
                    startActivity(i);
                    dialog.dismiss();
                    Toast.makeText(AgregarproductoActivity.this, "Guardado Exitosamente", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                    String mensaje = task.getException().toString();
                    Toast.makeText(AgregarproductoActivity.this, "Error: "+ mensaje, Toast.LENGTH_SHORT).show();
                }

            }
        });








    }
}