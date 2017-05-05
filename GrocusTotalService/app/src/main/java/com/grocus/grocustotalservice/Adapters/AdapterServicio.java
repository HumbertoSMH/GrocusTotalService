package com.grocus.grocustotalservice.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grocus.grocustotalservice.Models.Servicio;
import com.grocus.grocustotalservice.R;

import java.util.ArrayList;

/**
 * Created by Saso on 18/02/2017.
 */

public class AdapterServicio extends BaseAdapter {

    ArrayList<Servicio> servicios;
    protected LayoutInflater inflater;
    Context context;

    public AdapterServicio(ArrayList<Servicio> servicios, Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.servicios = servicios;
    }

    @Override
    public int getCount() {
        return servicios.size();
    }

    @Override
    public Object getItem(int position) {
        return servicios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Declaramos las Variables
        View v = convertView;
        //Para Mapear los atributos
        ViewHolder viewHolder = new ViewHolder();
        Servicio servicio;
        if(v == null)
        {
            v = inflater.inflate(R.layout.item_servicio, parent, false);
            viewHolder.idServicio = (TextView) v.findViewById(R.id.lblIdServicioItem);
            viewHolder.descripcion = (TextView) v.findViewById(R.id.lblNombreServicioItem);

           servicio = servicios.get(position);

            Log.d("Adapter", "Seteando Texto en los Controles");

            viewHolder.descripcion.setText(servicio.NombreServicio.toString());
            viewHolder.idServicio.setText(String.valueOf(servicio.idServicio).toString());

            v.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        return v;

    }

    static  class ViewHolder
    {
        TextView idServicio, descripcion;
    }
}
