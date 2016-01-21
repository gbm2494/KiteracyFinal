package com.example.gaudy_pc.kiteracy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

/*Activity para conectarse con el dispositivo bluetooth seleccionado en la activity
pantallaPrincipal, además muestra la lista de objetos disponibles para configurar su tag y se
 puede seleccionar un objeto especifico para ser configurado en la activity ConfiguracionObjeto*/
public class configuracionTags extends AppCompatActivity {

    /*botón para declarar el botón "Desconectar" en pantalla*/
    Button btnDis;
    /*String para guardar la dirección MAC del bluetooth al que se desea conectar*/
    String address = null;
    /*Barra de progreso para el estado de la conexión del bluetooth*/
    private ProgressDialog progress;

    /*Adapter y socket bluetooth para crear la conexión bluetooth con el dispositivo seleccionado*/
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

    /*Booleano para verificar si ya está conectado*/
    private boolean isBtConnected = false;

    //UUID SSP para crear la conexión con un bluetooth slave
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /*Atributos para cargar la lista de objetos del XML recursos.xml*/
    String item;
    ListView resourceList;

    /*variable que guarda la seleccion del usuario en el listView y lo envia con el intent*/
    public final static String OBJETO_SELECCIONADO = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*Con el intent recibe la dirección MAC del dispositivo seleccionado en la activity
         PantallaPrincipal para realizar la conexión*/
        Intent newint = getIntent();
        address = newint.getStringExtra(PantallaPrincipal.EXTRA_ADDRESS); //receive the address of the bluetooth device

        setContentView(R.layout.activity_configuracion_tags);

        btnDis = (Button)findViewById(R.id.btnDesconectarBT);

        new ConnectBT().execute(); //Conexión bluetooth

        /*Acción del click en el botón desconectar*/
        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }

        });

        /*Se carga el listview con los objetos para configurar del XML recursos.xml*/
        resourceList = (ListView)findViewById(R.id.listaRecursos);
        try{
            /*Se obtiene valores del XML especificos para la pantalla*/
            item = getItemFromXML(this);
        }catch (XmlPullParserException e){
        }catch (IOException e){
        }

        ArrayList list = new ArrayList();
        String[] items = item.split("\n");
        for(int i=0; i < items.length; i++)
        {
            list.add(items[i]); //Get the device's name and the address
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        resourceList.setAdapter(adapter);
        resourceList.setOnItemClickListener(myListClickListener); //Método que se activa al dar click en un elemento de la lista

    }

    /*Método para desconectar el bluetooth y volver a la pantalla InicioApp*/
    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish();
        /*Regresa a la pantalla InicioApp*/
        Intent intent = new Intent(this, InicioApp.class);
        startActivity(intent);
    }

    // Método para crear mensajes modales
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.configuraciontags, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*Método para obtener valores del XML de recursos
    * Recibe: un activity
    * Modifica: analiza todo el XML y obtiene los tags cuyo nombre es palabra o letra
    * Retorna: los valores del XML de la etiqueta valor*/
    public String getItemFromXML(Activity activity) throws XmlPullParserException, IOException{
        StringBuffer stringBuffer = new StringBuffer();
        Resources res = activity.getResources();
        XmlResourceParser xpp = res.getXml(R.xml.recursos);
        xpp.next();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT){
            if (eventType == XmlPullParser.START_TAG){
                if (xpp.getName().equals("palabra") || xpp.getName().equals("letra")){
                    stringBuffer.append(xpp.getAttributeValue(null, "valor") + "\n");
                }
            }
            eventType = xpp.next();
        }
        return stringBuffer.toString();

    }

    /*Método que se ejecuta al seleccionar un objeto del listView y cambia al activity ConfiguracionObjeto*/
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Se obtiene el valor que selecciona el usuario del listview
            String info = ((TextView) v).getText().toString();

            // Se pasa el valor seleccionado a la pantalla ConfiguracionObjeto
            Intent intent = new Intent(configuracionTags.this, ConfiguracionObjeto.class);

            //Se cambia de activity
            intent.putExtra(OBJETO_SELECCIONADO, info);
            startActivity(intent);
        }
    };

    /*Clase para conectar el bluetooth con una tarea asincronica*/
    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        /*Método que muestra un mensaje de espera para el usuario mientras se conecta*/
        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(configuracionTags.this, "Conectando...", "Por favor espere...");
        }

        /*Mientras se muestra el mensaje de espera al usuario en el background del app se realiza la conexión*/
        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        /*Despues de realizar la conexión con el bluetooth revisa si esta falló o funcionó*/
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Conexión fallida. Intente de nuevo.");
                finish();
            }
            else
            {
                msg("Conectado.");
                isBtConnected = true;
            }
            progress.dismiss();
        }

    }
}