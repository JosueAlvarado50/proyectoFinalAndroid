package mx.ita.sneaker_app3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private String CurrentUserId;
    private DatabaseReference UserRef;

    private String Telefono = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bottomNavigationView = findViewById(R.id.boton_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Telefono = bundle.getString("phone");
        }

        auth= FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Admin");
    }//onCreate

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if(item.getItemId() == R.id.fragmentUno){
                Fragmentos(new FragmentUno());
            }
            if(item.getItemId() == R.id.fragmentProductos){
                Fragmentos(new ProductosFragment());
            }
            if(item.getItemId() == R.id.fragmentDos){
                Fragmentos(new FragmentDos());
            }
            if(item.getItemId() == R.id.fragmentTres){
                Fragmentos(new FragmentTres());
            }
            if(item.getItemId() == R.id.fragmentCuatro){
                Fragmentos(new FragmentCuatro());
            }


            return true;
        }
    };

    private void Fragmentos(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser == null){
            EnviaralLogin();

        }else{
            VerificarUsuarioExistente();
        }
    }

    private void VerificarUsuarioExistente(){
        final String SurrentUserId = auth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(CurrentUserId)){
                    EnviaralSetup();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void EnviaralLogin(){
        Intent i = new Intent(AdminActivity.this, LoginAdminActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }



    private void EnviaralSetup(){
        Intent i = new Intent(AdminActivity.this, SetupAdminActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("phone", Telefono);
        startActivity(i);
        finish();

    }
}