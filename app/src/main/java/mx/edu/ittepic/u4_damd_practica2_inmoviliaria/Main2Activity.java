package mx.edu.ittepic.u4_damd_practica2_inmoviliaria;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    EditText Identificador,Domicilio,Precioventa,Preciorenta,Fecha;
    Button Insertar,Consultar,Borrar,Actualizar;
    Spinner propietarioidp;
    String [] idp = new String[1000];
    BaseDatos base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Identificador = findViewById(R.id.idinmueble);
        Domicilio = findViewById(R.id.idomicilio);
        Precioventa = findViewById(R.id.precioventa);
        Preciorenta  = findViewById(R.id.preciorenta);
        Fecha = findViewById(R.id.fecha);
        Consultar = findViewById(R.id.consultari);
        Insertar = findViewById(R.id.insertari);
        Actualizar = findViewById(R.id.actualizari);
        Borrar = findViewById(R.id.borrari);
        propietarioidp = findViewById(R.id.idp);

        base = new BaseDatos(this, "primera",null,1);

        SQLiteDatabase tabla = base.getReadableDatabase();
        String SQL = "SELECT IDP, NOMBRE FROM PROPIETARIO";
        Cursor resultado = tabla.rawQuery(SQL,null);
        int i=0;
        if (resultado.moveToFirst()) {
            do {
                idp[i] = resultado.getString(0) + ": " + resultado.getString(1);
                resultado.moveToPosition(i);
                i++;
            } while (resultado.moveToNext());
        }
         propietarioidp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,idp));

        Insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });

        Consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });

        Borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(2);
            }
        });

        Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Actualizar.getText().toString().startsWith("CONFIRMAR ACTUALIZACION")){
                    confirmacionactualizar();
                    return;
                }
                pedirID(3);
            }
        });

    }

    private void confirmacionactualizar() {
        AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
        confirmar.setTitle("ATENCION").setMessage("ESTAS SEGURO QUE DESEAS ACTUALIZAR EL REGISTRO")
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actualizarDato();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }

    private void actualizarDato(){
        try{
            String [] id =propietarioidp.getSelectedItem().toString().split(": ");
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "UPDATE INMUEBLE SET DOMICILIO='"+Domicilio.getText().toString()+"',PRECIOVENTA="+Precioventa.getText().toString()+",PRECIORENTA="+Preciorenta.getText().toString()+",FECHA='"+Fecha.getText().toString()+"',IDP="+id[0]+" WHERE IDINMUEBLE="+Identificador.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(Main2Activity.this, "SE ACTUALIZO", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(Main2Activity.this, "NO SE PUDO ACTULIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }

    private void confirmarborrar(String datos){
        String[] cadena = datos.split("&");
        final String id =cadena[0];
        String nombre = cadena[1];
        AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
        confirmar.setTitle("ATENCION").setMessage("ESTAS SEGURO QUE DESEAS BORRAR EL REGISTRO" + nombre +"?")
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarDato(id);
                        habilitarBotonesYLimpiarCampos();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();

    }

    private void habilitarBotonesYLimpiarCampos(){
        Identificador.setText("");
        Precioventa.setText("");
        Fecha.setText("");
        Preciorenta.setText("");
        Domicilio.setText("");
        Insertar.setEnabled(true);
        Consultar.setEnabled(true);
        Borrar.setEnabled(true);
        propietarioidp.setSelected(false);
        Actualizar.setText("ACTUALIZAR");
        Identificador.setEnabled(true);
    }
    private void eliminarDato(String idborrar){
        try {

            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "DELETE FROM INMUEBLE WHERE IDINMUEBLE=" + idborrar;
            tabla.execSQL(SQL);
            Toast.makeText(Main2Activity.this, "SE ELIMINO CORRECTAMENTE EL REGISTRO", Toast.LENGTH_LONG).show();
            habilitarBotonesYLimpiarCampos();
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(Main2Activity.this, "NO SE ELIMINO EL REGISTRO", Toast.LENGTH_LONG).show();
        }
    }


    private void pedirID(final int origen) {
        final EditText pidoID = new EditText(this);
        String mensaje ="", mensajeButton = null;
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(origen==1){
            mensaje = "ESCRIBE ID A BUSCAR";
            mensajeButton = "BUSCAR";
        }
        if(origen==2){
            mensaje = "ESCRIBE EL ID A ELIMINAR";
            mensajeButton = "ELIMINAR";
        }

        if(origen==3){
            mensaje= "ESCRIBE EL ID A MODIFICAR";
            mensajeButton = "MODIFICAR";
        }

        pidoID.setHint(mensaje);

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        alerta.setTitle("ATENCIÓN").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton(mensajeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){
                            Toast.makeText(Main2Activity.this,"DEBES ESCRIBIR VALOR", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscardato(pidoID.getText().toString(),origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR",null).show();


    }

    private void buscardato (String idBuscar, int origen){
        try {
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL ="SELECT * FROM INMUEBLE WHERE IDINMUEBLE="+idBuscar;
            Cursor resultado = tabla.rawQuery(SQL,null);
            if (resultado.moveToFirst()){
                Identificador.setText(resultado.getString(0));
                Domicilio.setText(resultado.getString(1));
                Precioventa.setText(resultado.getString(2));
                Preciorenta.setText(resultado.getString(3));
                Fecha.setText(resultado.getString(4));
                String [] natha;
                int j =0;
                do{
                    natha= idp[j].split(": ");
                    j++;
                }while (!natha[0].equals(resultado.getString(5)));
                Toast.makeText(Main2Activity.this,natha[0]+"--"+String.valueOf(resultado.getString(5)), Toast.LENGTH_LONG).show();
                propietarioidp.setSelection(j-1);

                if(origen==2){
                    //Esto siginifica que el resultó  para borrar
                    String datos = idBuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4)+"&"+resultado.getString(5);
                    confirmarborrar(datos);
                    return;
                }
                if(origen==3){
                    //modificar
                    Insertar.setEnabled(false);
                    Consultar.setEnabled(false);
                    Borrar.setEnabled(false);
                    Actualizar.setText("CONFIRMAR ACTUALIZACION");
                    Identificador.setEnabled(false);
                }
                Toast.makeText(this,"SI SE ENCONTRO RESULTADO",Toast.LENGTH_LONG).show();
            }else {
                //no hay resultado
                Toast.makeText(this,"ERROR: NO SE PUDO ENCONTRAR",Toast.LENGTH_LONG).show();
            }
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this,"ERROR: NO SE PUDO ENCONTRAR",Toast.LENGTH_LONG).show();
        }
    }

    private void codigoInsertar() {
        try {
            if (Identificador.getText().toString().isEmpty()) {
                Toast.makeText(Main2Activity.this, "AGREGAR IN ID", Toast.LENGTH_LONG).show();
                return;
            }
            if (!idrepetido(Identificador.getText().toString())) {
                String[] fecha = Fecha.getText().toString().split("-");
                if (!(fecha[0].length() == 4 && fecha[1].length() <= 2 && fecha[2].length() <= 2)) {

                    Toast.makeText(Main2Activity.this, "ESCRIBIR LA FECHA CON EL FORMATO (YYY-MM-DD)", Toast.LENGTH_LONG).show();
                    Fecha.setText("");
                    return;
                }

                String[] id = propietarioidp.getSelectedItem().toString().split(": ");
                SQLiteDatabase tabla = base.getWritableDatabase();
                String SQL = "INSERT INTO INMUEBLE VALUES (" + Identificador.getText().toString() + ",'" + Domicilio.getText().toString() + "'," + Precioventa.getText().toString() + "," + Preciorenta.getText().toString() + ",'" + Fecha.getText().toString() + "'," + id[0] + ")";
                tabla.execSQL(SQL);
                tabla.close();
                Toast.makeText(Main2Activity.this, "SE INSERTO CORRECTAMENTE EL REGISTRO", Toast.LENGTH_LONG).show();
                habilitarBotonesYLimpiarCampos();
            }else {
                Toast.makeText(Main2Activity.this, "ESCRIBIR OTRO ID (ESTA REPETIDO)", Toast.LENGTH_LONG).show();

            }
        }catch (SQLiteException e){
            Toast.makeText(Main2Activity.this, "NO SE INSERTO EL REGISTRO", Toast.LENGTH_LONG).show();
            Identificador.setText("");
        }
    }

    private boolean idrepetido(String idBuscar){
        SQLiteDatabase tabla = base.getWritableDatabase();
        String SQL ="SELECT * FROM INMUEBLE WHERE IDINMUEBLE="+idBuscar;
        Cursor resultado = tabla.rawQuery(SQL, null);
        if (resultado.moveToFirst()){
            return true;
        }
        return false;
    }
}
