package com.grocus.grocustotalservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grocus.grocustotalservice.Utils.LocationServiceImpl;
import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;
import com.grocus.grocustotalservice.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckIn extends AppCompatActivity {

    TextView Bienvenido,
            Fecha,
            Direccion;

    Button CheckIn;
    String latitud,
            longitud;

    LocationServiceImpl locationService;

    String usuarioTecnico;
    String fechaCheck;

    int idVisita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = LocationServiceImpl.getLocationManager(this);
        setContentView(R.layout.activity_check_in);


        Bienvenido = (TextView) findViewById(R.id.lblCIBienvenido);
        Fecha = (TextView) findViewById(R.id.lblCIFecha);
        Direccion = (TextView) findViewById(R.id.lblDireccion);

        CheckIn = (Button) findViewById(R.id.btnCheckIn);

        Bienvenido.setText(Bienvenido.getText() + "  " + Preferences.getPreference(this,"nombreUsuario"));
        Fecha.setText(Utils.getCurrentDate().toString());

        Direccion.setText("Dirección: " + Preferences.getPreference(this, "Direccion"));


        CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationService.isGPSEnabled)
                {
                    try
                    {
                        latitud = String.valueOf(locationService.latitude);
                        longitud = String.valueOf(locationService.longitude);
                        idVisita = Integer.parseInt(Preferences.getPreference(CheckIn.this, "idVisita"));
                        usuarioTecnico = Preferences.getPreference(CheckIn.this,"usuarioTecnico").toString();

                        new AsyncTask<String, Void, Void>()
                        {

                            @Override
                            protected Void doInBackground(String... params) {

                                JSONObject body = new JSONObject();
                                JSONObject parametros = new JSONObject();
                                try {
                                    parametros.put("usuarioTecnico", usuarioTecnico);
                                    parametros.put("idVisita", idVisita);
                                    parametros.put("fechaCheck", Utils.getCurrentDateForCheck().toString());
                                    parametros.put("latitud", latitud);
                                    parametros.put("longitud", longitud);

                                    body.put("checkInVisita", parametros);
                                    String UrlServicio = METHOD.URL_BASE + METHOD.CHECK_IN;
                                    String respuesta = post(UrlServicio, body.toString());
                                    Log.d("Respuesta", respuesta);

                                    JSONObject respue = new JSONObject(respuesta);
                                    JSONObject servicioResult = respue.getJSONObject("insertarCheckInVisitaResult");
                                    boolean seEjecutoConExito = servicioResult.getBoolean("seEjecutoConExito");
                                    if(seEjecutoConExito)
                                    {
                                        Message msg = new Message();
                                        msg.arg1=1;
                                        handler.sendMessage(msg);

                                    }
                                    else
                                    {
                                        Message msg = new Message();
                                        msg.arg1=2;

                                        handler.sendMessage(msg);
                                       //
                                    }
                                    Log.d("json",respue.getString("insertarCheckInVisitaResult"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();
                    }
                    catch(Exception e)
                    {
                        Log.d("Error", e.toString());
                    }

                }
                else
                {
                    Toast.makeText(CheckIn.this, "No se puede realizar el Check In sin el gps activado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1==1)
            {
                final Intent serviciosIntent = new Intent(CheckIn.this,Services.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckIn.this);
                builder.setMessage("Check In Exitoso");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(serviciosIntent);
                    }
                });
                builder.show();
            }
            if(msg.arg1 == 2)
            {
                Toast.makeText(CheckIn.this,"No se pudo realizar el check In".toString(), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
}
