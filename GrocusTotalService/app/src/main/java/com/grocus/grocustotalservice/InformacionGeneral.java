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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InformacionGeneral extends AppCompatActivity {

    EditText placas,
             NoServicio,
             kilometraje,
             otros;
    RadioGroup radioMantenimiento;
    RadioButton radioPreventivo,
                radioCorrectivo;
    Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_general);

        placas = (EditText) findViewById(R.id.txtPlaca);
        NoServicio = (EditText) findViewById(R.id.txtNServicio);
        kilometraje = (EditText) findViewById(R.id.txtKilometraje);
        otros = (EditText) findViewById(R.id.txtCorrecciones);

        radioMantenimiento = (RadioGroup) findViewById(R.id.rbgTipoMantenimiento);
        radioPreventivo = (RadioButton) findViewById(R.id.rbPreventivo);
        radioCorrectivo = (RadioButton) findViewById(R.id.rbCorrectivo);

        guardar = (Button) findViewById(R.id.btnGuardarInformacionGen);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    final String nServicio = NoServicio.getText().toString();
                    final String placasj = placas.getText().toString();
                    final String kilometrajej = kilometraje.getText().toString();
                    final String otrosj =  otros.getText().toString();
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            int idVisita = Integer.parseInt(Preferences.getPreference(InformacionGeneral.this, "idVisita"));
                            String usuarioTecnico = Preferences.getPreference(InformacionGeneral.this,"usuarioTecnico").toString();

                            JSONObject body = new JSONObject();
                            JSONObject parametros = new JSONObject();
                            try
                            {
                                parametros.put("idVisita", idVisita);
                                parametros.put("usuarioTecnico", usuarioTecnico);
                                parametros.put("numeroServicio", nServicio );
                                parametros.put("placa", placasj);
                                parametros.put("kilometraje", kilometrajej);
                                parametros.put("correccionesAdicionales",otrosj);

                                body.put("informacionVisita", parametros);
                                String UrlServicio = METHOD.URL_BASE + METHOD.INFORMACION_GENERAL;
                                String respuesta = post(UrlServicio, body.toString());
                                Log.d("JsonInfo", body.toString());
                                Log.d("RespuestaInfoGeneral", respuesta);

                                JSONObject respue = new JSONObject(respuesta);
                                JSONObject servicioResult = respue.getJSONObject("insertarInformacionGeneralVisitaResult");
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

                            }catch (Exception ex)
                            {
                                Toast.makeText(InformacionGeneral.this, "", Toast.LENGTH_SHORT).show();
                            }

                            return null;
                        }
                    }.execute();
                }
                catch (Exception e)
                {
                    Toast.makeText(InformacionGeneral.this, "", Toast.LENGTH_SHORT).show();
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
                final Intent serviciosIntent = new Intent(InformacionGeneral.this,Services.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(InformacionGeneral.this);
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
                Toast.makeText(InformacionGeneral.this,"No se pudo actualizar la información general de la visita.".toString(), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
}
