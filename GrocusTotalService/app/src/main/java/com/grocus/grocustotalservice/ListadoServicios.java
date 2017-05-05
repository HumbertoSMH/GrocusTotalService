package com.grocus.grocustotalservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devazt.networking.HttpClient;
import com.devazt.networking.OnHttpRequestComplete;
import com.devazt.networking.Response;
import com.grocus.grocustotalservice.Adapters.ListadoVisitasAdapter;
import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;
import com.grocus.grocustotalservice.Utils.Utils;

import com.grocus.grocustotalservice.Models.Visitado;
import com.grocus.grocustotalservice.Models.Visitas;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListadoServicios extends AppCompatActivity implements OnHttpRequestComplete, AdapterView.OnItemClickListener {


    TextView bienvenido,
            fecha;
    ListView lvVisitas;
    ListadoVisitasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_servicios2);

        lvVisitas = (ListView) findViewById(R.id.lvVisitas);
        bienvenido = (TextView) findViewById(R.id.lblBienvenido);
        fecha = (TextView) findViewById(R.id.lblFecha);

        bienvenido.setText("Bienvenido" +  "  " +  Preferences.getPreference(this,"nombreUsuario").toString());
        fecha.setText(Utils.getCurrentDate().toString());

        final HttpClient httpClient = new HttpClient(this);
        //Creamos la Url del servicio

        String URlServicio = METHOD.URL_BASE+METHOD.OBTENER_RUTA +  "usuarioTecnico=" + Preferences.getPreference(this,"usuarioTecnico");

        try
        {
                httpClient.excecute(URlServicio);
        }
        catch(Exception e)
        {

        }
    }

    @Override
    public void onComplete(Response status) {

        if(status.isSuccess())
        {
            ArrayList<Visitas> visitas = new ArrayList<Visitas>();

            try
            {
                JSONObject jsonObject = new JSONObject(status.getResult());
                boolean seEjecutoConExito = jsonObject.getBoolean("seEjecutoConExito");
                if(seEjecutoConExito)
                {
                    //Obtenemos la lista de visitas
                    JSONObject listaRutas = jsonObject.getJSONObject("rutaTecnico");
                    JSONArray ArrayVisitas = listaRutas.getJSONArray("visitas");
                    for(int i = 0; i < ArrayVisitas.length(); i++)
                    {

                        Visitas v = new Visitas();
                        Visitado visitado = new Visitado();
                        JSONObject item = ArrayVisitas.getJSONObject(i);
                        v.idVisita = item.getInt("idVisita");
                        v.idEstatusVisita = item.getInt("idEstatusVisita");

                        //LLenamos la Informacion del usuario visitado
                        JSONObject usuarioVisitado =  item.getJSONObject("usuarioVisitado");
                        visitado.direccion = usuarioVisitado.getString("direccion");
                        visitado.colonia = usuarioVisitado.getString("colonia");
                        visitado.municipio = usuarioVisitado.getString("municipio");
                        visitado.idVisitado = usuarioVisitado.getInt("idVisitado");
                        visitado.nombreEmpresa = usuarioVisitado.getString("nombreEmpresa");
                        visitado.nombreResponsable = usuarioVisitado.getString("nombreResponsable");
                        visitado.telefono = usuarioVisitado.getString("telefono");

                        v.Visitado = visitado;
                        visitas.add(v);
                    }
                    adapter = new ListadoVisitasAdapter(this, visitas);
                    lvVisitas.setAdapter(adapter);
                    lvVisitas.setOnItemClickListener(this);
                }
                else
                {
                    String mensaje = jsonObject.getString("mensaje");
                    Toast toast = Toast.makeText(this ,mensaje, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
            catch(Exception e)
            {
                Toast exito = Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
                exito.show();
            }

        }
        else
        {
            Toast exito = Toast.makeText(this,"No se Pudo Conectar con el servidor", Toast.LENGTH_LONG);
            exito.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try
        {
            Visitas visitas = (Visitas)parent.getItemAtPosition(position);
            if(visitas.idEstatusVisita == 7 || visitas.idEstatusVisita == 6)
            {
                Toast.makeText(this, "Ya se Han realiza los CheckOut del servicio", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Preferences.savePreference(this, "idVisita", String.valueOf(visitas.idVisita));
                Preferences.savePreference(this, "Responsable", visitas.Visitado.nombreResponsable);
                Preferences.savePreference(this, "Direccion", visitas.Visitado.direccion);

                Log.d("Visita", String.valueOf(visitas.idEstatusVisita));
                if(visitas.idEstatusVisita == 8)
                {
                    Intent checkIn = new Intent(this,Services.class);
                    startActivity(checkIn);
                }else
                {

                    Intent siguiente = new Intent(this, CheckIn.class);
                    startActivity(siguiente);
                }
            }


        }
        catch(Exception e)
        {
            Log.d("Error de cast", "Error al convertir el item al tipo visita");
        }

    }
}
