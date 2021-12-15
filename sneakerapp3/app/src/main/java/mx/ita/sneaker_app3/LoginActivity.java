package mx.ita.sneaker_app3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText telefono, codigo;
    private Button enviartelefono, enviarcodigo;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private String phoneNumber;
    private String VerificacionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        telefono=(EditText) findViewById(R.id.Et_telefono);
        codigo = (EditText) findViewById(R.id.Et_pass);
        enviartelefono = (Button) findViewById(R.id.Btn_codigo);
        enviarcodigo = (Button) findViewById(R.id.Btn_login);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        enviartelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = telefono.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(LoginActivity.this, "Ingresa tu telefono primero...", Toast.LENGTH_SHORT).show();

                }else{
                    dialog.setTitle("Validando numero telefonico");
                    dialog.setMessage("Espere mientras validamos su numero");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginActivity.this)
                            .setCallbacks(callbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);// Metodo enviar codigo

                }
            }
        });

        enviarcodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telefono.setVisibility(View.GONE);
                enviarcodigo.setVisibility(View.GONE);
                String VerificacionCodigo= codigo.getText().toString();
                if(TextUtils.isEmpty(VerificacionCodigo)){
                    Toast.makeText(LoginActivity.this, "Ingreesa el codigo resibido", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setTitle("Verificando");
                    dialog.setMessage("Espere...");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificacionID, VerificacionCodigo);
                    IngresadoConExito(credential);

                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                IngresadoConExito(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this,"Fallo en el inicio", Toast.LENGTH_SHORT).show();
                telefono.setVisibility(View.VISIBLE);
                enviartelefono.setVisibility(View.VISIBLE);
                codigo.setVisibility(View.GONE);
                enviarcodigo.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                //super.onCodeSent(s, token);
                VerificacionID = s;
                resendingToken=token;
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "Codigo enviado satisfactoriamente, Revisa tu bandeja", Toast.LENGTH_SHORT).show();
                telefono.setVisibility(View.GONE);
                enviartelefono.setVisibility(View.GONE);
                codigo.setVisibility(View.VISIBLE);
                enviarcodigo.setVisibility(View.VISIBLE);
            }
        };
    }

    private void IngresadoConExito(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Ingresó con exito", Toast.LENGTH_SHORT).show();
                    EnviaralaPrincipal();
                }else{
                    String e = task.getException().toString();
                    Toast.makeText(LoginActivity.this, "Error : "+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null){
            EnviaralaPrincipal();
        }
    }

    private void EnviaralaPrincipal(){
        Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("phone", phoneNumber);
        i.putExtra("papel", "usuario");

        startActivity(i);
        finish();

    }

}