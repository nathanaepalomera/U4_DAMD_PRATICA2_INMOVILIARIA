package mx.edu.ittepic.u4_damd_practica2_inmoviliaria;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText Identificador,Nombre,Telefono,Domicilio;
    Button Insertar,Actualizar,Consultar,Borrar,Siguiente;
    BaseDatos base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Identificador = findViewById(R.id.ide);
        Nombre = findViewById(R.id.nombre);
        Telefono = findViewById(R.id.telefono);
        Domicilio = findViewById(R.id.domicilio);

        Insertar = findViewById(R.id.insertar);
        Actualizar = findViewById(R.id.actualizar);
        Consultar = findViewById(R.id.consultar);
        Borrar = findViewById(R.id.borrar);
        Siguiente = findViewById(R.id.sig);

        base = new BaseDatos(this, "primera",null,1);

        Insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar ();
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

        Siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrir = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(abrir);
            }
        });

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
                            Toast.makeText(MainActivity.this,"DEBES ESCRIBIR VALOR", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscardato(pidoID.getText().toString(),origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR",null).show();

    }

    private void codigoInsertar() {

        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "INSERT INTO PROPIETARIO VALUES(1,'%2','%3','%4')";
            SQL =SQL.replace("1",Identificador.getText().toString());
            SQL = SQL.replace("%2",Nombre.getText().toString());
            SQL = SQL.replace("%3",Domicilio.getText().toString());
            SQL = SQL.replace("%4",Telefono.getText().toString());
            tabla.execSQL(SQL);
            habilitarBotonesYLimpiarCampos();
            Toast.makeText(this,"SI SE PUDO INSERTAR",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this,"ERROR: NO SE PUDO INSERTAR",Toast.LENGTH_LONG).show();
        }

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

    private void habilitarBotonesYLimpiarCampos(){
        Identificador.setText("");
        Nombre.setText("");
        Domicilio.setText("");
        Telefono.setText("");
        Insertar.setEnabled(true);
        Consultar.setEnabled(true);
        Borrar.setEnabled(true);
        Actualizar.setText("ACTUALIZAR");
        Identificador.setEnabled(true);
    }

        private void actualizarDato() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "UPDATE PROPIETARIO SET NOMBRE='"+Nombre.getText().toString()+"',DOMICILIO='"+Domicilio.getText().toString()+"',TELEFONO='"+Telefono.getText().toString()+"' WHERE IDP="+Identificador.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(MainActivity.this, "SE ACTUALIZO", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(MainActivity.this, "NO SE PUDO ACTULIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }

    private void eliminarDato(String idborrar) {
        try {


            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "DELETE FROM PROPIETARIO WHERE IDP=" + idborrar;
            tabla.execSQL(SQL);
            Toast.makeText(MainActivity.this, "SE ELIMINO CORRECTAMENTE EL REGISTRO", Toast.LENGTH_LONG).show();
            habilitarBotonesYLimpiarCampos();
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(MainActivity.this, "NO SE ELIMINO EL REGISTRO", Toast.LENGTH_LONG).show();
        }
    }

    private void confirmarborrar (String datos) {

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


    private void buscardato(String idABuscar, int origen){

        try {
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "SELECT * FROM PROPIETARIO  WHERE IDP="+idABuscar;
            Cursor resultado = tabla.rawQuery(SQL,null);

            if(resultado.moveToFirst()){
                //resultado
                Identificador.setText(resultado.getString(0));
                Nombre.setText(resultado.getString(1));
                Domicilio.setText(resultado.getString(2));
                Telefono.setText(resultado.getString(3));

                if(origen==2){
                    //Esto siginifica que el resultó  para borrar
                    String datos = idABuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3);
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
}
