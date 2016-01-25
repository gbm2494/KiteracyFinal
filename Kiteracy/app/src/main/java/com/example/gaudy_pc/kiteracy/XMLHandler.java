package com.example.gaudy_pc.kiteracy;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Gaudy-PC on 24/01/2016.
 */
public class XMLHandler {

    /*Método para saber si existe configuración previa de tags en el archivo recursos.XML
    * Recibe: un activity
    * Modifica: busca tags configurados en el XML
    * Retorna: si existe o no configuracion previa*/

    public XMLHandler(){

    }
    public boolean existeConfiguracionXML(Activity activity) throws XmlPullParserException, IOException {
        Resources res = activity.getResources();
        XmlResourceParser xpp = res.getXml(R.xml.recursos);
        xpp.next();
        int eventType = xpp.getEventType();

        boolean configuracion = false;

        while (eventType != XmlPullParser.END_DOCUMENT && configuracion == false){

            if (eventType == XmlPullParser.START_TAG){
                if (xpp.getName().equals("palabra") || xpp.getName().equals("letra")){
                    if(!xpp.getAttributeValue(null, "RFIDTag").equals("")){
                        Log.e("hola", "Detecto un tag");
                        configuracion = true;
                    }
                }
            }
            eventType = xpp.next();
        }
        return configuracion;
    }
}
