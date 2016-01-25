package com.example.gaudy_pc.kiteracy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/*Pantalla de inicio de Kiteracy la cual muestra los dispositivos disponibles para conectarse*/
public class PantallaPrincipal extends AppCompatActivity
{
    //Objetos para manejar la lista de dispositivos vinculados y cargarlos con el botón
    Button btnDispositivosVinculados;
    ListView listaDispositivos;

    //Objetos para el manejo de la conexión bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> dispositivosVinculados;

    /*Aquí se almacena la dirección MAC del dispositivo bluetooth a conectar*/
    public static String EXTRA_ADDRESS = "device_address";

    /**/
    XMLHandler objetoXML = new XMLHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        //Se carga la lista de dispositivos y el botón en pantalla
        btnDispositivosVinculados = (Button)findViewById(R.id.btnCargarVinculados);
        listaDispositivos = (ListView)findViewById(R.id.listView);

        //Si el dispositivo tiene bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
                //Se muestra un mensaje si el dispositivo no cuenta con bluetooth
            Toast.makeText(getApplicationContext(), "Dispositivo sin Bluetooth disponible", Toast.LENGTH_LONG).show();

            finish();
        }
        /*Si el dispositivo cuenta con bluetooth pero se encuentra apagado solicita encenderlo*/
        else if(!myBluetooth.isEnabled())
        {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        /*Acción del botón para cargar la lista de dispositivos vinculados del telefono*/
        btnDispositivosVinculados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });

    }

    /*Método para cargar la lista de dispositivos vinculados del telefono en el listView*/
    private void pairedDevicesList()
    {
        dispositivosVinculados = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (dispositivosVinculados.size()>0)
        {
            for(BluetoothDevice bt : dispositivosVinculados)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No se encontraron dispositivos Bluetooth vinculados.", Toast.LENGTH_LONG).show();
        }

        /*Se carga en el listView todos los items */
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        listaDispositivos.setAdapter(adapter);
        listaDispositivos.setOnItemClickListener(myListClickListener); //Al dar click en un item de la lista

    }

    /*Acción al dar click en un elemento del listView*/
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Se obtiene la dirección MAC del dispositivo, la cual es los 17 ultimos caracteres
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            /*Llama al método para cambiarActivity que verifica a cual activity debe pasar*/
            cambiarActivity(address);

        }
    };

    public void cambiarActivity(String MACAddress){

        boolean existeConfiguracion = false;

        Intent intent = getIntent();
        String recibido = intent.getStringExtra(InicioApp.seleccion);

        /*Se pregunta si existe configuración previa en el archivo de configuracion de tags*/
        try{
            existeConfiguracion = objetoXML.existeConfiguracionXML(this);
        }
        catch (XmlPullParserException e){
        }
        catch (IOException e){
        }

        Log.e("valor del recibido", recibido);

        if(existeConfiguracion) {
            Log.e("valor del existeConf", "true");
        }

        /*Si la configuración ya se encuentra en el XML pasa a la pantalla de sesión*/
        if(existeConfiguracion == true && recibido.equals("Iniciar"))
        {
            Log.e("hola", "Detecto que hay tags");
            Intent i = new Intent(PantallaPrincipal.this, Sesion.class);
            i.putExtra(EXTRA_ADDRESS, MACAddress);
            startActivity(i);
        }
        else if(existeConfiguracion && recibido.equals("Configurar"))
        {
            Intent i = new Intent(PantallaPrincipal.this, configuracionTags.class);
            i.putExtra(EXTRA_ADDRESS, MACAddress);
            startActivity(i);
        }
        /*Si no existe se pasa a la pantalla de configuración de Tags de RFID sin importar que seleccionó en la primera pantalla*/
        else if(existeConfiguracion == false){
            Intent i = new Intent(PantallaPrincipal.this, configuracionTags.class);
            i.putExtra(EXTRA_ADDRESS, MACAddress);
            startActivity(i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listadispositivos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

