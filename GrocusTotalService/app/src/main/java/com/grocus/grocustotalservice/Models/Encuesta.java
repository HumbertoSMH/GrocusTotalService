package com.grocus.grocustotalservice.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by humbertohernandez on 4/29/17.
 */

public class Encuesta {
    public int idEncuesta;
    public String descripcionEncuesta;
    public ArrayList<Pregunta> ListaPregunta;

    public Encuesta()
    {
        this.idEncuesta = 0;
        this.descripcionEncuesta = "";
        this.ListaPregunta = new ArrayList<Pregunta>();
    }
}
