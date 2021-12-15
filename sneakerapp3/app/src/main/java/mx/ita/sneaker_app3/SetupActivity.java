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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private EditText nombre, direccion, telefono, ciudad, edad;
    private Button guardar;
    private String phone = "";
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int Galery_Pick = 1;
    private StorageReference UserImagePerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        auth=FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");//conexion a la BD, guardar datos
        dialog = new ProgressDialog(this);
        UserImagePerfil = FirebaseStorage.getInstance().getReference().child("Perfil");//crea carpeta para guardar docs
        nombre = (EditText)findViewById(R.id.setup_nombre);
        ciudad = (EditText)findViewById(R.id.setup_ciudad);
        direccion =(EditText)findViewById(R.id.setup_direccion);
        telefono = (EditText)findViewById(R.id.setup_telefono);
        guardar = (Button)findViewById(R.id.setup_boton);
        imagen=(CircleImageView)findViewById(R.id.setup_imagen);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            phone = bundle.getString("phone");
        }
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuardarInformacion();
            }
        });
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/");
                startActivityForResult(i, Galery_Pick);
            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("imagen")){
                        String imagestr = snapshot.child("imagen").getValue().toString();
                            Picasso.get().load(imagestr).placeholder(R.drawable.welcome).into(imagen);
                    }else{
                        Toast.makeText(SetupActivity.this, "Selecciona una imagen de perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    //OnCreate***********************


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Galery_Pick && resultCode==RESULT_OK && data != null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                dialog.setTitle("Imagen de perfil");
                dialog.setMessage("Estamos procesando su foto");
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);

                final Uri resultUri = result.getUri();
                StorageReference filePath = UserImagePerfil.child(CurrentUserId + ".jpg");// se guardan fotos como unicos para que no se repitan
                final File uri = new File(resultUri.getPath());
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            UserImagePerfil.child(CurrentUserId+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();
                                    UserRef.child(CurrentUserId).child("imagen").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Picasso.get().load(downloadUri).into(imagen);
                                                dialog.dismiss();
                                            }else {
                                                String mensaje = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }else {
                Toast.makeText(this, "Imagen no soportada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }

    private void GuardarInformacion(){
        String nombres = nombre.getText().toString().toUpperCase();
        String direccions = direccion.getText().toString();
        String ciudads = ciudad.getText().toString();
        String phones = telefono.getText().toString();

        if(TextUtils.isEmpty(nombres)){
            Toast.makeText(this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(direccions)){
            Toast.makeText(this, "Ingrese direccion", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(ciudads)){
            Toast.makeText(this, "Ingrese ciudad", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phones)){
            Toast.makeText(this, "Ingrese telefono", Toast.LENGTH_SHORT).show();
        }else {
            dialog.setTitle("Guardando");
            dialog.setMessage("Espere por favor...");
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            HashMap map = new HashMap();
            map.put("nombre", nombres);
            map.put("direccion", direccions);
            map.put("ciudad", ciudads);
            map.put("telefono", phones);
            map.put("uid", CurrentUserId);

            UserRef.child(CurrentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SetupActivity.this, "Guardadooo", Toast.LENGTH_SHORT).show();
                        EnviaralInicio();
                        dialog.dismiss();
                    }else{
                        String mensaje = task.getException().toString();
                        Toast.makeText(SetupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });


        }

    }

    private void EnviaralInicio(){
        Intent i = new Intent(SetupActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

}