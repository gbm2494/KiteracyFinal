package com.example.gaudy_pc.kiteracy;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/*Activity para mostrar el objeto seleccionado en el listView de la activity
* configuracionTags así como su imagen y audio*/
public class ConfiguracionObjeto extends AppCompatActivity {

    /*label de prueba para verificar que el intent envie bien el dato clickeado en el listview*/
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_objeto);

        /*Aquí se carga en pantalla el nombre del objeto seleccionado en la pantalla pasada*/
        label = (TextView)findViewById(R.id.objetoRecibido);
        Intent intent = getIntent();
        label.setText(intent.getStringExtra(configuracionTags.OBJETO_SELECCIONADO));


        /*Esta clase debe conectarse con bluetooth como lo hace la clase configuracionTags*/
    }
}
