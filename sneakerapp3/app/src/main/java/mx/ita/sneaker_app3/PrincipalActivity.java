package mx.ita.sneaker_app3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mx.ita.sneaker_app3.Modal.Productos;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    private String CurrentUserId;
    private DatabaseReference UserRef, ProductosRef;
    private FloatingActionButton botonFlotante;
    private RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;


    private String Telefono = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Telefono = bundle.getString("phone");
        }

        auth=FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        ProductosRef = FirebaseDatabase.getInstance().getReference().child("Productos");
        recyclerMenu=findViewById(R.id.recicler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);
        botonFlotante = (FloatingActionButton)findViewById(R.id.fab);
        botonFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PrincipalActivity.this, CarritoActivity.class);
                startActivity(i);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle("Sneakerland");
        setActionBar(toolbar);


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView nombreHeader = (TextView) headerView.findViewById(R.id.nombre_perfil);
        CircleImageView imagenHeader = (CircleImageView) headerView.findViewById(R.id.user_perfil);

        UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("imagen")){
                    String imagen = snapshot.child("imagen").getValue().toString();
                    nombreHeader.setText(snapshot.child("nombre").getValue().toString());
                    Picasso.get().load(imagen).error(R.drawable.welcome).into(imagenHeader);


                }else if(snapshot.exists()){
                    nombreHeader.setText(snapshot.child("nombre").getValue().toString());



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        FirebaseRecyclerOptions<Productos> options = new FirebaseRecyclerOptions.Builder<Productos>()
                .setQuery(ProductosRef, Productos.class).build();

        FirebaseRecyclerAdapter<Productos, ProductoViewHolder> adapter = new FirebaseRecyclerAdapter<Productos, ProductoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductoViewHolder holder, int position, @NonNull Productos model) {

                holder.productoNom.setText(model.getNombre());
                holder.productoCantidad.setText(model.getCantidad());
                holder.productoDescripcion.setText(model.getDescripcion());
                holder.productoPrecio.setText(model.getPrecioven());
                Picasso.get().load(model.getImagen()).into(holder.productoImagen);

                holder.productoImagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(PrincipalActivity.this, ProductoDetallesActivity.class);
                        i.putExtra("pid", model.getPid());
                       startActivity(i);
                  }
                });
            }

            @NonNull
            @Override
            public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productos_item_layout, viewGroup, false);
                ProductoViewHolder holder = new ProductoViewHolder(view);


                return holder;
            }
        };

        recyclerMenu.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onBackPressed() {// cerrar navigation
        DrawerLayout drawerLayout = (DrawerLayout)  findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_principal_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_carrito){
            ActivityCarrito();
        }
        else if(id == R.id.nav_categorias){
            ActivityCategoria();
        }
        else if(id == R.id.nav_buscar){
            ActivityBuscar();
        }
        else if(id == R.id.nav_perfil){
            ActivityPerfil();
        }
        else if(id == R.id.nav_salir){
            auth.signOut();
            EnviaralLogin();

        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ActivityPerfil() {
        Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(PrincipalActivity.this, PerfilActivity.class);
        startActivity(i);
    }

    private void ActivityBuscar() {
        Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
        //Intent i = new Intent(PrincipalActivity.this, PerfilActivity.class);
        //startActivity(i);
    }

    private void ActivityCategoria() {
        Toast.makeText(this, "Categoria", Toast.LENGTH_SHORT).show();
        //Intent i = new Intent(PrincipalActivity.this, PerfilActivity.class);
        //startActivity(i);
    }

    private void ActivityCarrito() {
        Toast.makeText(this, "Carrito", Toast.LENGTH_SHORT).show();
        //Intent i = new Intent(PrincipalActivity.this, PerfilActivity.class);
        //startActivity(i);
    }

    private void EnviaralLogin(){
        Intent i = new Intent(PrincipalActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }
    private void EnviaralSetup(){
        Intent i = new Intent(PrincipalActivity.this, SetupActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("phone", Telefono);
        startActivity(i);
        finish();

    }


}