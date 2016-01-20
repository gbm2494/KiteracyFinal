package com.example.gaudy_pc.kiteracy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class InicioApp extends AppCompatActivity {

    /*Vector con los elementos del spinner de la pantalla inicial*/
    public String[] elementosVoz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);

        /*Distintas voces presentes en la aplicaci{on*/
        this.elementosVoz = new String[] {"Mujer", "Hombre", "Niña", "Niño"};
        /*Se cargan los elementos en el spinner*/
        Spinner s = (Spinner) findViewById(R.id.SpinnerVoz);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, elementosVoz);
        s.setAdapter(adapter);
    }

    public void mostrarNuevaPantalla(View view) {
        Intent intent = new Intent(this, PantallaPrincipal.class);
        startActivity(intent);
    }

}