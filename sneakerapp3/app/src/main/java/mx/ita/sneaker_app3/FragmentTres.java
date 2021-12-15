package mx.ita.sneaker_app3;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentTres extends Fragment {

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

    private View fragmento;



    public FragmentTres() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmento = inflater.inflate(R.layout.fragment_tres, container, false);

        auth= FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Admin");//conexion a la BD, guardar datos
        dialog = new ProgressDialog(getContext());
        UserImagePerfil = FirebaseStorage.getInstance().getReference().child("Perfil");//crea carpeta para guardar docs

        nombre = (EditText)fragmento.findViewById(R.id.admperfil_nombre);
        ciudad = (EditText)fragmento.findViewById(R.id.admperfil_ciudad);
        direccion =(EditText)fragmento.findViewById(R.id.admperfil_direccion);
        telefono = (EditText)fragmento.findViewById(R.id.admperfil_telefono);
        guardar = (Button)fragmento.findViewById(R.id.admperfil_boton);
        imagen=(CircleImageView)fragmento.findViewById(R.id.admperfil_imagen);

        UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists() && snapshot.hasChild("imagen")){

                    String nombres = snapshot.child("nombre").getValue().toString();
                    String direccions = snapshot.child("direccion").getValue().toString();
                    String ciudads = snapshot.child("ciudad").getValue().toString();
                    String telefonos = snapshot.child("telefono").getValue().toString();
                    String imagens = snapshot.child("imagen").getValue().toString();

                    Picasso.get().load(imagens).placeholder(R.drawable.welcome).into(imagen);

                    nombre.setText(nombres);
                    direccion.setText(direccions);
                    ciudad.setText(ciudads);
                    telefono.setText(telefonos);



                }else if (snapshot.exists()){
                    String nombres = snapshot.child("nombre").getValue().toString();
                    String direccions = snapshot.child("direccion").getValue().toString();
                    String ciudads = snapshot.child("ciudad").getValue().toString();
                    String telefonos = snapshot.child("telefono").getValue().toString();

                    nombre.setText(nombres);
                    direccion.setText(direccions);
                    ciudad.setText(ciudads);
                    telefono.setText(telefonos);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                        Toast.makeText(getContext(), "Selecciona una imagen de perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return fragmento;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Galery_Pick && resultCode==RESULT_OK && data != null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(getActivity());

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
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Imagen no soportada", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Ingrese nombre", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(direccions)){
            Toast.makeText(getContext(), "Ingrese direccion", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(ciudads)){
            Toast.makeText(getContext(), "Ingrese ciudad", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phones)){
            Toast.makeText(getContext(), "Ingrese telefono", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Guardadooo", Toast.LENGTH_SHORT).show();
                        EnviaralInicio();
                        dialog.dismiss();
                    }else{
                        String mensaje = task.getException().toString();
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });


        }

    }

    private void EnviaralInicio(){
        Intent i = new Intent(getContext(), AdminActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
}