package mx.ita.sneaker_app3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button botonusuario, botonadmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        botonusuario = (Button) findViewById(R.id.botonusuario);
        botonadmin=(Button)findViewById(R.id.botonadmin);

        botonusuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);

            }
        });

        botonadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, LoginAdminActivity.class);
                startActivity(i);

            }
        });

    }
}