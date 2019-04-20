package mx.edu.ittepic.ricardojimenez.tpdm_u3_practica2_ricardojimenez;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    EditText sku,desc,stock,precio;
    Button insertar,consultar,eliminar,actualizar;
    ListView lista;
    List<Productos> datosConsultaProductos;
    FirebaseFirestore servicioFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sku = findViewById(R.id.editText);
        desc = findViewById(R.id.editText2);
        precio = findViewById(R.id.editText3);
        stock = findViewById(R.id.editText4);

        insertar = findViewById(R.id.button3);
        consultar = findViewById(R.id.button4);
        eliminar = findViewById(R.id.button5);
        actualizar = findViewById(R.id.button6);

        lista = findViewById(R.id.lista);

        servicioFirestore = FirebaseFirestore.getInstance();

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sku.getText().toString().equals("")
                || desc.getText().toString().equals("")
                || precio.getText().toString().equals("")
                || stock.getText().toString().equals("")){
                    mensaje("Error","Llene los campos vacios");
                    return;
                }
                inserta();
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarTodos();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarPorSKU("eliminar");
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarPorSKU("actualizar");
            }
        });

    }

    private void consultarPorSKU(final String accion) {
        final EditText id = new EditText(this);
        id.setHint("Ingrese el SKU");
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("Buscar producto por SKU").setView(id).setPositiveButton("Buscar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buscarPorSKU(id.getText().toString(),accion);
                    }
                }).setNegativeButton("Cancelar",null).show();


    }
    private  void eliminarPorSKU(final Productos pr){
        if(pr==null) {
            Toast.makeText(Main2Activity.this, "No se encontro el registro", Toast.LENGTH_SHORT).show();
            return;
        }
            AlertDialog.Builder al = new AlertDialog.Builder(this);
            al.setTitle("ATENCION").setMessage("Esta seguro de eliminar el producto\n"+pr.descripcion)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(Main2Activity.this, "asl "+pr.sku, Toast.LENGTH_SHORT).show();
                            servicioFirestore.collection("productos")
                                    .document(pr.sku)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mensaje("Atencion","Se elimino con exito");
                                        }
                                    })
                                   ;
                        }
                    }).setNegativeButton("Cancelar",null)
                    .show();

    }

    private void buscarPorSKU(final String id,final String accion) {
        servicioFirestore.collection("productos")
                .whereEqualTo("sku",id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        Query q = queryDocumentSnapshots.getQuery();
                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    Map<String, Object> dato=null;
                                    Productos pr=null;
                                    for(QueryDocumentSnapshot registro : task.getResult()){
                                        dato = registro.getData();
                                        pr = new Productos(dato.get("sku").toString(),dato.get("descripcion").toString(),
                                                Float.parseFloat(dato.get("precio").toString()),Integer.parseInt(dato.get("stock").toString()));
                                    }
                                    if(accion.equals("eliminar")){
                                        eliminarPorSKU(pr);
                                    }else{
                                        actualizarProducto(pr);
                                    }
                                }
                            }
                        });
                    }
                });

    }


    private void actualizarProducto(final Productos pr) {
        if(pr==null){
            Toast.makeText(Main2Activity.this, "No se encontro el registro", Toast.LENGTH_SHORT).show();
            return;
        }
        final View actualiza = getLayoutInflater().inflate(R.layout.interfaz_actualizar,null);
        final  EditText de = actualiza.findViewById(R.id.desc);
        final EditText pre = actualiza.findViewById(R.id.precio);
        final EditText stk = actualiza.findViewById(R.id.stock);
        de.setText(pr.descripcion);pre.setText(pr.precio+"");stk.setText(""+pr.stock);
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("ATENCION ACTUALIZANDO "+pr.descripcion).setView(actualiza)
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("sku",pr.sku);
                        data.put("descripcion",de.getText().toString());
                        data.put("precio",Float.parseFloat(pre.getText().toString()));
                        data.put("stock",Integer.parseInt(stk.getText().toString()));

                        servicioFirestore.collection("productos")
                                .document(pr.sku)
                                .update(data).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main2Activity.this,
                                        "ERROR AL ACTUALIZAR",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Main2Activity.this,
                                                "ACTUALIZACIÓN EXITOSA",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).setNegativeButton("Cancelar",null)
                .show();
    }

    private void consultarTodos() {
        servicioFirestore.collection("productos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        datosConsultaProductos = new ArrayList<>();
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot registro : task.getResult()){
                                Map<String,Object> datos = registro.getData();
                                Productos pr = new Productos(datos.get("sku").toString(),
                                        datos.get("descripcion").toString(),
                                        Float.parseFloat(datos.get("precio").toString()),
                                        Integer.parseInt(datos.get("stock").toString()));
                                datosConsultaProductos.add(pr);
                            }
                            ponerloEnListView();
                        }else{
                            Toast.makeText(Main2Activity.this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void ponerloEnListView() {
        if(datosConsultaProductos.size()==0)
            return;
        String[] datos = new String[datosConsultaProductos.size()];
        int i=0;
        for(Productos  pr: datosConsultaProductos){
            datos[i] = pr.sku+" - "+pr.descripcion+"\nStock: "+pr.stock;
            i++;
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(Main2Activity.this,android.R.layout.simple_list_item_1,datos);
        lista.setAdapter(adaptador);
    }

    private void inserta() {
        servicioFirestore.collection("productos").document(sku.getText().toString())
                .set(new Productos(sku.getText().toString(),desc.getText().toString(),
                        Float.parseFloat(precio.getText().toString()),
                        Integer.parseInt(stock.getText().toString())))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mensaje("Exito","Se inserto el producto "+desc.getText().toString());
                        desc.setText("");stock.setText("");precio.setText("");sku.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensaje("Error",e.getMessage());
            }
        });
    }

    private void mensaje(String title,String message){
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle(title).setMessage(message).setPositiveButton("OK",null).show();
    }
}
