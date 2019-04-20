package mx.edu.ittepic.ricardojimenez.tpdm_u3_practica2_ricardojimenez;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button productos,almacen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productos = findViewById(R.id.button);
        almacen = findViewById(R.id.button2);
        productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prod = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(prod);
            }
        });
        almacen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alm = new Intent(MainActivity.this,Main3Activity.class);
                startActivity(alm);
            }
        });

    }
}
