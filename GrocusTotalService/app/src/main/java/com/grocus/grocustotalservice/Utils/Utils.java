package com.grocus.grocustotalservice.Utils;

import android.content.Context;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by JoseLuis on 17/07/16.
 */
public class Utils {

    public enum Enviroment {
        MOCK,
        FAKE,
        DES,
        DIS;

        public static Enviroment currentEnviroment = DES;
    }


    public final static String AMOUNT_FORMATTER = "$#,##0.00";

    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return simpleDateFormat.format(new Date());
    }

    public static String getCurrentDateForCheck() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        return simpleDateFormat.format(new Date());
    }

    public static String amountFormatter(double amount, String pattern) {
        if (pattern == null)
            pattern = AMOUNT_FORMATTER;

        return new DecimalFormat(pattern).format(amount);
    }

    /*
    * Valida el estatus de la peticion realizada
    * */
    public static boolean seEjecutoConExito(JSONObject jsonObject) {
        boolean esExitoso = false;
        try {
            esExitoso = jsonObject.getBoolean("seEjecutoConExito");
        } catch (JSONException exc) {
            esExitoso = false;
        }
        return esExitoso;
    }

    public static String getMessageOfErrortoShow(JSONObject jsonObject) {
        StringBuilder msgError = new StringBuilder("");
        try {
            msgError.append(jsonObject.getString("claveError")).
                    append(" - ").
                    append(jsonObject.getString("mensaje"));
        } catch (JSONException exc) {
            msgError = new StringBuilder("Error al procesar la solicitud");
        }
        return msgError.toString();
    }

    public static String obtenerStringBase64(byte[] buffer) {
        return Base64.encodeToString(buffer, 0, buffer.length, Base64.NO_WRAP);
    }

    public static boolean isValidFormatDate( String dia, String mes , String anio){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String mm = Meses.getmmFromMes( mes );
            try {
                format.parse( anio + "-"+mm+"-"+dia );
                return true;
            }
            catch(ParseException e){
                e.printStackTrace();
                return false;
            }
    }


   public static enum Meses{
        ENERO ( 31 , "Enero" , "01"),
        FEBRERO( 29 , "Febrero" , "02"),
        MARZO( 31 , "Marzo" , "03"),
        ABRIL( 30 , "Abril", "04"),
        MAYO( 31 , "Mayo" , "05"),
        JUNIO(30 , "Junio" , "06"),
        JULIO( 31 , "Julio" , "07"),
        AGOSTO( 31 , "Agosto" , "08"),
        SEPTIEMBRE(30 , "Septiembre", "09"),
        OCTUBRE( 31 , "Octubre" , "10"),
        NOVIEMBRE( 30 , "Novimebre" , "11"),
        DICIEMBRE( 31 , "Diciembre" , "12");

        private int dias ;
        private String nombre;
        private String mm;
        Meses( int dias , String nombre , String mm){
            this.dias = dias;
            this.nombre = nombre;
            this.mm = mm;
        }

        public static String  getmmFromMes( String mes ){
            String mm="";
            for(Meses itemMes : Meses.values() ){
                if( itemMes.nombre.equalsIgnoreCase( mes ) ){
                    mm = itemMes.mm;
                    break;
                }
            }
            return mm;
        }

       public static int  getDiasFromMes( String mes ){
           int dias = 0;
           for(Meses itemMes : Meses.values() ){
               if( itemMes.nombre.equalsIgnoreCase( mes ) ){
                   dias = itemMes.dias;
                   break;
               }
           }
           return dias;
       }

    }


}