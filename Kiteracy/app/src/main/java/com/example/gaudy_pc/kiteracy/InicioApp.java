package com.example.gaudy_pc.kiteracy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/*Pantalla inicial del app, muestra el numero de sesión y el tipo de voz a ejecutar*/
public class InicioApp extends AppCompatActivity {

    /*Vector con los elementos del spinner de la pantalla inicial*/
    public String[] elementosVoz;

    public static String seleccion = "Iniciar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);

        /*Distintas voces presentes en la aplicacion*/
        this.elementosVoz = new String[] {"Mujer", "Hombre", "Nina", "Nino"};

        /*Se cargan los elementos en el spinner*/
        Spinner s = (Spinner) findViewById(R.id.SpinnerVoz);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, elementosVoz);
        s.setAdapter(adapter);
    }

    /*Método para el cambio de pantalla para configurar el bluetooth del botón iniciar*/
    public void accionBtnIniciar(View view) {

        Intent intent = new Intent(this, PantallaPrincipal.class);
        intent.putExtra(seleccion, "Iniciar");
        startActivity(intent);
    }

    /*Método para el cambio de pantalla al seleccionar el botón configurar*/
    public void accionBtnConfigurar (View view){
        Intent i = new Intent(this, PantallaPrincipal.class);
        i.putExtra(seleccion, "Configurar");
        startActivity(i);

    }

}