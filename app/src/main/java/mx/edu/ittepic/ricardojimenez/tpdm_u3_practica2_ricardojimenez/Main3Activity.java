package mx.edu.ittepic.ricardojimenez.tpdm_u3_practica2_ricardojimenez;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class Main3Activity extends AppCompatActivity {
    EditText id,desc,capacidad,ubicacion;
    Button insertar,consultar,eliminar,actualizar;
    ListView lista;
    List<Almacenes> datosConsultaAlmacenes;
    FirebaseFirestore servicioFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        id = findViewById(R.id.editText5);
        desc = findViewById(R.id.editText6);
        capacidad = findViewById(R.id.editText7);
        ubicacion = findViewById(R.id.editText8);

        insertar = findViewById(R.id.button7);
        consultar = findViewById(R.id.button8);
        eliminar = findViewById(R.id.button9);
        actualizar = findViewById(R.id.button10);

        lista = findViewById(R.id.listaalmacen);

        servicioFirestore = FirebaseFirestore.getInstance();

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.getText().toString().equals("")
                        || desc.getText().toString().equals("")
                        || capacidad.getText().toString().equals("")
                        || ubicacion.getText().toString().equals("")){
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
                consultarPorID("eliminar");
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarPorID("actualizar");
            }
        });
    }

    private void consultarPorID(final String accion) {
        final EditText id = new EditText(this);
        id.setHint("Ingrese el ID del almacen");
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("Buscar almacen por ID").setView(id).setPositiveButton("Buscar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buscarPorID(id.getText().toString(),accion);
                    }
                }).setNegativeButton("Cancelar",null).show();
    }

    private void buscarPorID(final String id,final String accion) {
        servicioFirestore.collection("almacenes")
                .whereEqualTo("idAlmacen",id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        Query q = queryDocumentSnapshots.getQuery();
                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    Map<String, Object> dato=null;
                                    Almacenes al=null;
                                    for(QueryDocumentSnapshot registro : task.getResult()){
                                        dato = registro.getData();
                                        al = new Almacenes(dato.get("idAlmacen").toString(),
                                                dato.get("descripcion").toString(),
                                                Integer.parseInt(dato.get("capacidad").toString()),
                                                dato.get("ubicacion").toString());
                                    }
                                    if(accion.equals("eliminar")){
                                        eliminarPorID(al);
                                    }else{
                                        actualizarAlmacen(al);
                                    }
                                }
                            }
                        });
                    }
                });
    }

    private  void eliminarPorID(final Almacenes alm){
        if(alm==null) {
            Toast.makeText(Main3Activity.this, "No se encontro el registro", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("ATENCION").setMessage("Esta seguro de eliminar el alamcen\n"+alm.descripcion)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(Main2Activity.this, "asl "+pr.sku, Toast.LENGTH_SHORT).show();
                        servicioFirestore.collection("almacenes")
                                .document(alm.idAlmacen)
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

    private void actualizarAlmacen(final Almacenes alm) {
        if(alm==null){
            Toast.makeText(Main3Activity.this, "No se encontro el registro", Toast.LENGTH_SHORT).show();
            return;
        }
        final View actualiza = getLayoutInflater().inflate(R.layout.interfaz_actualizar_almacenes,null);
        final  EditText de = actualiza.findViewById(R.id.desc_almacen);
        final EditText cap = actualiza.findViewById(R.id.capacidad_almacen);
        final EditText ub = actualiza.findViewById(R.id.ubicacion_almacen);
        de.setText(alm.descripcion);cap.setText(alm.capacidad+"");ub.setText(alm.ubicacion);
        final AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("ATENCION ACTUALIZANDO "+alm.descripcion).setView(actualiza)
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("idAlmacen",alm.idAlmacen);
                        data.put("descripcion",de.getText().toString());
                        data.put("capacidad",Integer.parseInt(cap.getText().toString()));
                        data.put("ubicacion",ub.getText().toString());
                        servicioFirestore.collection("almacenes")
                                .document(alm.idAlmacen)
                                .update(data).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main3Activity.this,
                                        "ERROR AL ACTUALIZAR",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Main3Activity.this,
                                                "ACTUALIZACIÓN EXITOSA",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).setNegativeButton("Cancelar",null)
                .show();
    }

    private void consultarTodos() {
        servicioFirestore.collection("almacenes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        datosConsultaAlmacenes = new ArrayList<>();
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot registro : task.getResult()){
                                Map<String,Object> datos = registro.getData();
                                Almacenes al = new Almacenes(datos.get("idAlmacen").toString(),
                                        datos.get("descripcion").toString(),
                                        Integer.parseInt(datos.get("capacidad").toString()),
                                        datos.get("ubicacion").toString());
                                datosConsultaAlmacenes.add(al);
                            }
                            ponerloEnListView();
                        }else{
                            Toast.makeText(Main3Activity.this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void ponerloEnListView() {
        if(datosConsultaAlmacenes.size()==0)
            return;
        String[] datos = new String[datosConsultaAlmacenes.size()];
        int i=0;
        for(Almacenes al: datosConsultaAlmacenes){
            datos[i] = al.idAlmacen+" - "+al.descripcion+"\nCapacidad: "+al.capacidad;
            i++;
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(Main3Activity.this,android.R.layout.simple_list_item_1,datos);
        lista.setAdapter(adaptador);
    }
    private void inserta() {
        servicioFirestore.collection("almacenes").document(id.getText().toString())
                .set(new Almacenes(id.getText().toString(),desc.getText().toString(),
                        Integer.parseInt(capacidad.getText().toString()),ubicacion.getText().toString()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mensaje("Exito","Se inserto el almacen "+desc.getText().toString());
                        desc.setText("");capacidad.setText("");ubicacion.setText("");id.setText("");
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
