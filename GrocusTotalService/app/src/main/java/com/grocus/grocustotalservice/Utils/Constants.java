package com.grocus.grocustotalservice.Utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.devazt.networking.HttpClient;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oscar Vargas
 * @since 22/07/16.
 */
public class Constants {

    public static enum StatusVisit{
        EN_RUTA( "En Ruta" , Color.rgb(206,0,0) ),
        CHECK_IN( "Check In" , Color.rgb(216,237,31) ),
        CHECKOUT( "Check Out" , Color.rgb(0,176,0) ),
        CHECKOUT_WITHOUT_VALIDATION( "Check Out Sin Validar" , Color.MAGENTA ),
        OTHER( "" ,Color.rgb(206,0,0) );

        private String nombre;
        private int RGBColor;

        StatusVisit(String nombre , int RGBColor ){
            this.nombre = nombre;
            this.RGBColor = RGBColor;
        }

        public String getNombre() { return nombre; }
        public int getRGBColor() { return RGBColor; }

        public static StatusVisit getStatusVisitByName( String nombre ){
            StatusVisit sv = null;
            for( StatusVisit item : StatusVisit.values() ){
                if( item.nombre.equals( nombre ) )
                {
                    sv = item ;
                }
            }
            return sv;
        }


    }



}
