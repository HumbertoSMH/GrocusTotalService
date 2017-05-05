package com.grocus.grocustotalservice.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grocus.grocustotalservice.Models.*;
import com.grocus.grocustotalservice.R;
import com.grocus.grocustotalservice.Utils.*;

import java.util.ArrayList;

/**
 * Created by Saso on 09/02/2017.
 */

public class ListadoVisitasAdapter extends BaseAdapter {

    Context context;
    protected LayoutInflater inflater;
    ArrayList<Visitas> visitas  = new ArrayList<Visitas>();

    public ListadoVisitasAdapter(Context context, ArrayList<Visitas> visitas){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.visitas = visitas;
    }

    @Override
    public int getCount() {
        return visitas.size();
    }

    @Override
    public Object getItem(int position) {
        return visitas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder  viewHolder = new ViewHolder();

        if (v == null){
            v = inflater.inflate(R.layout.item_visitas, parent, false);

            viewHolder.txtVisita = (TextView) v.findViewById(R.id.txtVisita);
            viewHolder.txtStatusVisita = (TextView) v.findViewById(R.id.txtStatusVisita);
            viewHolder.txtTienda = (TextView)v.findViewById(R.id.txtTienda);
            Visitas visita = visitas.get(position);

            viewHolder.txtVisita.setText(visita.Visitado.nombreResponsable);
            switch (visita.idEstatusVisita)
            {
                case 4:
                    viewHolder.txtStatusVisita.setText("En Ruta");
                    break;
                case 6:
                    viewHolder.txtStatusVisita.setText("Check Out");
                    break;
                case 7 :
                    viewHolder.txtStatusVisita.setText("Check Out Sin Validar");
                    break;
                case 8:
                    viewHolder.txtStatusVisita.setText("Check In");
                    break;
                default:
            }

            viewHolder.txtTienda.setText(visita.Visitado.nombreEmpresa + " "+ visita.Visitado.direccion + " "+ visita.Visitado.colonia + " " + visita.Visitado.municipio);

            String estatusVisita = viewHolder.txtStatusVisita.getText().toString() ;
            try{
                Constants.StatusVisit sv = Constants.StatusVisit.getStatusVisitByName( estatusVisita );
                v.setBackgroundColor( sv.getRGBColor() );
                if(viewHolder.txtStatusVisita.getText().toString().equals("Check In"))
                {
                    viewHolder.txtVisita.setTextColor(Color.BLACK);
                    viewHolder.txtStatusVisita.setTextColor(Color.BLACK);
                    viewHolder.txtTienda.setTextColor(Color.BLACK);
                }
            }catch(Exception exc){
                exc.printStackTrace();
               v.setBackgroundColor(Color.BLUE );
            }
            v.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return v;
    }

    static class ViewHolder
    {
        TextView txtVisita, txtStatusVisita, txtTienda;
    }
}
