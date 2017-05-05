package com.grocus.grocustotalservice.Utils;

import android.util.Log;
import android.widget.Toast;

import com.grocus.grocustotalservice.Models.RespuestaEncuesta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Saso on 02/05/2017.
 */

public class LvToArray {

    static public JSONObject ObtenerJsonRespuesta(ArrayList<RespuestaEncuesta> respuestaEncuestas)
    {
        JSONObject respuestas = new JSONObject();
        JSONArray  arrayRespuestas = new JSONArray();

        try
        {
            for(int i = 0; i < respuestaEncuestas.size(); i++)
            {
                JSONObject respuestaIndividual = new JSONObject();
                respuestaIndividual.put("idPregunta",respuestaEncuestas.get(i).idPregunta);
                respuestaIndividual.put("idRespuestaElegida", respuestaEncuestas.get(i).idRespuestaElegida);
                respuestaIndividual.put("respuestaAbierta", respuestaEncuestas.get(i).respuestaAbierta);
                arrayRespuestas.put(respuestaIndividual);
            }
            respuestas.put("respuestasEncuesta", arrayRespuestas.toString());
        }
        catch (Exception ex)
        {
            Log.d("ErrorJson", ex.toString());
        }

        return respuestas;
    }
}
