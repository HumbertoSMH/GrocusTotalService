package com.grocus.grocustotalservice.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grocus.grocustotalservice.EncuestaServicio;
import com.grocus.grocustotalservice.Models.Encuesta;
import com.grocus.grocustotalservice.Models.Pregunta;
import com.grocus.grocustotalservice.Models.Respuesta;
import com.grocus.grocustotalservice.Models.RespuestaEncuesta;
import com.grocus.grocustotalservice.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by humbertohernandez on 4/29/17.
 */

public class AdapterEncuesta extends BaseAdapter implements AdapterView.OnItemSelectedListener {
    //public static ArrayList<RespuestaEncuesta> respuestaEncuestas = new ArrayList<RespuestaEncuesta>();
    //String paraToast;
    ArrayList<Pregunta> listaEncuesta;
    ArrayList<Integer> adapterInt = new ArrayList<Integer>();
    static ViewHolder viewHolder;
    protected LayoutInflater inflater;
    Context context;


    public AdapterEncuesta(ArrayList<Pregunta> listaEncuesta, Context context)
    {
        this.listaEncuesta = listaEncuesta;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return listaEncuesta.size();
    }

    @Override
    public Object getItem(int position) {
        return listaEncuesta.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        viewHolder = new ViewHolder();
        if(v == null)
        {
            Pregunta pregunta = listaEncuesta.get(position);
            v = inflater.inflate(R.layout.item_encuesta, parent, false);

            viewHolder.pregunta = (TextView) v.findViewById(R.id.lblPregunta);
            viewHolder.otro = (EditText) v.findViewById(R.id.txtRespuestaAbierta);
            viewHolder.respuestas = (Spinner) v.findViewById(R.id.spRespuesta);
            viewHolder.idRespuesta = (TextView) v.findViewById(R.id.lblRespuestaElegida);
            viewHolder.idPregunta = (TextView) v.findViewById(R.id.txtIdPregunta);

            viewHolder.pregunta.setText(pregunta.descripcionPregunta);
            viewHolder.idPregunta.setText(String.valueOf(pregunta.idPregunta));

            ArrayList<String> adapterString = new ArrayList<String>();

            RespuestaEncuesta nuevo = new RespuestaEncuesta();
            for(int i =0; i< pregunta.listaRespuesta.size(); i++)
            {
                adapterString.add(pregunta.listaRespuesta.get(i).descripcionRespuesta);
                adapterInt.add(pregunta.listaRespuesta.get(i).idRespuesta);
            }
            ArrayAdapter<String> adapter =  new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,adapterString);

            viewHolder.respuestas.setAdapter(adapter);
            viewHolder.respuestas.setOnItemSelectedListener(this);
            v.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        viewHolder.idRespuesta.setText(adapterView.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
         viewHolder.idRespuesta.setText("");
    }

    static class ViewHolder
    {
        TextView pregunta;
        TextView idPregunta;
        EditText otro;
        Spinner respuestas;
        TextView idRespuesta;
    }

}
