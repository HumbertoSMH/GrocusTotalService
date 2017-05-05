package com.grocus.grocustotalservice.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by humbertohernandez on 4/29/17.
 */

public class Pregunta {
   public int idPregunta;
    public String descripcionPregunta;
    public ArrayList<Respuesta> listaRespuesta;

    public Pregunta()
    {
        this.idPregunta = 0;
        this.descripcionPregunta = "";
        this.listaRespuesta = new ArrayList<Respuesta>();
    }
}
