package com.grocus.grocustotalservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devazt.networking.HttpClient;
import com.devazt.networking.OnHttpRequestComplete;
import com.devazt.networking.Response;
import com.grocus.grocustotalservice.Adapters.AdapterServicio;
import com.grocus.grocustotalservice.Models.Encuesta;
import com.grocus.grocustotalservice.Models.Servicio;
import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;
import com.grocus.grocustotalservice.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Services extends AppCompatActivity implements OnHttpRequestComplete,  AdapterView.OnItemClickListener {

    //Variables
    TextView Bienvenido;
    TextView Fecha;
    TextView Visita;
    TextView Responsable;
    ListView lvServicios;
    AdapterServicio adapter;

    Button checkOutSinvalidar,
                    checkOut,
            encuesta,
            infoGeneral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        Bienvenido = (TextView) findViewById(R.id.lblSBienvenido);
        Fecha = (TextView) findViewById(R.id.lblSFecha);
        Visita = (TextView) findViewById(R.id.lblIdVisita);
        Responsable = (TextView) findViewById(R.id.lblResponsable);
        lvServicios = (ListView) findViewById(R.id.lvServicios);

        //Llenamos los campos con la informacion Correspondiente.
        Bienvenido.setText(Bienvenido.getText().toString() +  "  " + Preferences.getPreference(this, "nombreUsuario").toString());
        Fecha.setText(Utils.getCurrentDate().toString());
        Visita.setText(Preferences.getPreference(this, "idVisita").toString());
        Responsable.setText(Preferences.getPreference(this,"Responsable").toString());

        checkOutSinvalidar = (Button) findViewById(R.id.btnCheckOutSinValidar);
        checkOutSinvalidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(Services.this, ChechOutSinValidar.class);
                startActivity(siguiente);
            }
        });

        checkOut = (Button) findViewById(R.id.btnCheckOut);
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(Services.this, CheckOut.class);
                startActivity(siguiente);
            }
        });

        infoGeneral = (Button) findViewById(R.id.btnInfoGeneral);
        infoGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(Services.this , InformacionGeneral.class);
                startActivity(siguiente);
            }
        });

        encuesta = (Button) findViewById(R.id.btnEncuesta);
        encuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent siguiente = new Intent(Services.this , EncuestaServicio.class);
                startActivity(siguiente);
            }
        });
        final HttpClient client = new HttpClient(this);
        String UrlServicio = METHOD.URL_BASE + METHOD.OBTENER_LISTADO_SERVICIOS;

        try
        {
            client.excecute(UrlServicio);
            Log.d("Servicio", "Ejecutando servicio: " + UrlServicio);
        }
        catch (Exception e)
        {
            Log.d("erro", e.toString());
        }
    }

    @Override
    public void onComplete(Response status) {

        if(status.isSuccess())
        {
            ArrayList<Servicio> servicioArray = new ArrayList<Servicio>();

            try
            {
                JSONObject respuesta = new JSONObject(status.getResult());
                boolean seEjecutoConExito = respuesta.getBoolean("seEjecutoConExito");
                if(seEjecutoConExito)
                {
                    JSONArray jsonArray = respuesta.getJSONArray("servicios");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        Servicio servicio = new Servicio();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        servicio.idServicio = jsonObject.getInt("idServicio");
                        servicio.NombreServicio = jsonObject.getString("descripcion");
                        servicioArray.add(servicio);
                    }

                    adapter = new AdapterServicio(servicioArray, this);
                    lvServicios.setAdapter(adapter);
                    lvServicios.setOnItemClickListener(this);
                }
                else
                {
                    Toast error = Toast.makeText(this, respuesta.getString("mensaje"), Toast.LENGTH_LONG);
                    error.show();
                }

            }
            catch(Exception e){
                Log.d("erro", e.toString());
            }

        }
        else
        {
            Toast error = Toast.makeText(this,"No se pudo obtener el listado de servicios", Toast.LENGTH_LONG);
            error.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try
        {
            Servicio servicio = (Servicio) parent.getItemAtPosition(position);

            Preferences.savePreference(this,"idServicio", String.valueOf(servicio.idServicio));
            Preferences.savePreference(this,"nombreServicio", servicio.NombreServicio);

            Intent siguiente = new Intent(this, CapturarServicio.class);
            startActivity(siguiente);
        }
        catch(Exception e)
        {
            Log.d("error", e.toString());
        }

    }
}
