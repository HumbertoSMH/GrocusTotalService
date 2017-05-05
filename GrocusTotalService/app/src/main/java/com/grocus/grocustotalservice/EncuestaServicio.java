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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devazt.networking.HttpClient;
import com.devazt.networking.OnHttpRequestComplete;
import com.devazt.networking.Response;
import com.grocus.grocustotalservice.Adapters.AdapterEncuesta;
import com.grocus.grocustotalservice.Adapters.ListadoVisitasAdapter;
import com.grocus.grocustotalservice.Models.Encuesta;
import com.grocus.grocustotalservice.Models.Pregunta;
import com.grocus.grocustotalservice.Models.Respuesta;
import com.grocus.grocustotalservice.Models.RespuestaEncuesta;
import com.grocus.grocustotalservice.Models.Visitado;
import com.grocus.grocustotalservice.Models.Visitas;
import com.grocus.grocustotalservice.Utils.LvToArray;
import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EncuestaServicio extends AppCompatActivity implements OnHttpRequestComplete {

    ListView lsEncuesta;
    Button btnEnviarencuesta;
    Encuesta encuesta;
    ArrayList<Pregunta> pArray = new ArrayList<Pregunta>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_servicio);

        lsEncuesta = (ListView) findViewById(R.id.lvEncuesta);
        btnEnviarencuesta = (Button) findViewById(R.id.btnEnviarEncuesta);
       btnEnviarencuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<RespuestaEncuesta> rEArray = new ArrayList<RespuestaEncuesta>();
                View v;
                TextView idPregunta, idRespuesta;
                EditText preguntaAbierta;
                for(int i= 0; i < lsEncuesta.getCount(); i++)
                {
                    RespuestaEncuesta re = new RespuestaEncuesta();
                    v = lsEncuesta.getChildAt(i);
                    idPregunta = (TextView) v.findViewById(R.id.txtIdPregunta);
                    idRespuesta = (TextView) v.findViewById(R.id.lblRespuestaElegida);
                    preguntaAbierta = (EditText) v.findViewById(R.id.txtRespuestaAbierta);
                    re.idPregunta =  Integer.parseInt(idPregunta.getText().toString());
                    re.respuestaAbierta = preguntaAbierta.getText().toString();
                    re.idRespuestaElegida = pArray.get(i).listaRespuesta.indexOf(idRespuesta.getText().toString());
                    rEArray.add(re);
                }
/*                Log.d("JsonResultante", LvToArray.ObtenerJsonRespuesta(rEArray).toString());
                 JSONObject jsonArray = new JSONObject() ;
                 jsonArray = LvToArray.ObtenerJsonRespuesta(rEArray); */
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... strings) {
                        int idVisita = Integer.parseInt(Preferences.getPreference(EncuestaServicio.this, "idVisita"));
                        String usuarioTecnico = Preferences.getPreference(EncuestaServicio.this,"usuarioTecnico").toString();

                        JSONObject body = new JSONObject();
                        JSONObject parametros = new JSONObject();
                        try
                        {
                            parametros.put("idVisita", idVisita);
                            parametros.put("usuarioTecnico", usuarioTecnico);
                            parametros.put("idEncuesta", 1);

                        }
                        catch(Exception ex)
                        {

                        }
                        return null;
                    }
                }.execute();
            }
        });
        final HttpClient httpClient = new HttpClient(this);
        //Creamos la Url del servicio

        String URlServicio = METHOD.URL_BASE+METHOD.OBTENER_ENCUESTA +  "?idVisita=" + Integer.parseInt(Preferences.getPreference(this,"idVisita")) ;

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
            Encuesta encuestaj = new Encuesta();

            try
            {
                JSONObject jsonObject = new JSONObject(status.getResult());
                Log.d("JsonRespuestaEncuesta", jsonObject.toString());
                boolean seEjecutoConExito = jsonObject.getBoolean("seEjecutoConExito");
                if(seEjecutoConExito)
                {
                    JSONObject jsonEncuesta = jsonObject.getJSONObject("encuestaVisita");
                    encuestaj.idEncuesta = jsonEncuesta.getInt("idEncuesta");
                    encuestaj.descripcionEncuesta = jsonEncuesta.getString("descripcionEncuesta");
                    //Obtenemos la lista de visitas

                    JSONArray ArrayPreguntas = jsonEncuesta.getJSONArray("preguntas");
                    for(int i = 0; i < ArrayPreguntas.length(); i++)
                    {
                        Pregunta preguntaj = new Pregunta();
                        JSONObject item = ArrayPreguntas.getJSONObject(i);
                        Log.d("JsonPreguntas", item.toString());
                        preguntaj.idPregunta = item.getInt("idPregunta");
                        preguntaj.descripcionPregunta = item.getString("descripcionPregunta");
                        JSONArray itemRespuestas = item.getJSONArray("respuestas");
                        Log.d("JsonRespuestas", itemRespuestas.toString());
                        for(int x = 0; x < itemRespuestas.length(); x++)
                        {
                            Respuesta respuestaj = new Respuesta();
                            JSONObject itemR = itemRespuestas.getJSONObject(x);
                            Log.d("JsonRespuestaItem", itemR.toString());
                            respuestaj.idRespuesta = itemR.getInt("idRespuesta");
                            respuestaj.descripcionRespuesta = itemR.getString("descripcionRespuesta");
                            preguntaj.listaRespuesta.add(respuestaj);
                        }
                        Log.d("JsonDebug", "Aqui Llega");
                        encuestaj.ListaPregunta.add(preguntaj);
                        Log.d("jsonCompleto", "Aqui ya no");
                    }
                    pArray = encuestaj.ListaPregunta;
                    AdapterEncuesta adapterEncuesta = new AdapterEncuesta(encuestaj.ListaPregunta, this);
                    lsEncuesta.setAdapter(adapterEncuesta);
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

    final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1==1)
            {
                final Intent serviciosIntent = new Intent(EncuestaServicio.this,Services.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(EncuestaServicio.this);
                builder.setMessage("Información enviada con exito");
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
                Toast.makeText(EncuestaServicio.this,"No se pudo enviar la encuesta de la visita.".toString(), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
}
