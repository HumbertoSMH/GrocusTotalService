package com.grocus.grocustotalservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;
import com.grocus.grocustotalservice.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CapturarServicio extends AppCompatActivity {


    //Controles
    TextView Bienvenido,
            Fecha,
            Responsable,
            TitutoServicio;

    Button CapturarFoto,
            Guardar;

    EditText Comentarios;

    ImageView imgFoto;

    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;

    int idVisita,
        idServicio;
    String ComentarioServicio,
            usuarioTecnico;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar_servicio);

        final Intent serviciosIntent = new Intent(this, Services.class);
        Bienvenido = (TextView) findViewById(R.id.lblCSBienvenido);
        Bienvenido.setText(Bienvenido.getText() + " " + Preferences.getPreference(this, "nombreUsuario"));

        Fecha = (TextView) findViewById(R.id.lblCSFecha);
        Fecha.setText(Utils.getCurrentDate().toString());

        Responsable = (TextView) findViewById(R.id.lblCSResponsable);
        Responsable.setText(Preferences.getPreference(this, "Responsable"));

        Comentarios = (EditText) findViewById(R.id.txtComentariosServicio);
        TitutoServicio = (TextView) findViewById(R.id.lblCSTituloServicio);
        TitutoServicio.setText(Preferences.getPreference(this, "nombreServicio"));

        Guardar = (Button) findViewById(R.id.btnGuardar);

        CapturarFoto = (Button) findViewById(R.id.btnCapturarFoto);
        imgFoto = (ImageView) findViewById(R.id.imgServicio);

        CapturarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE);
                }
            }
        });
        if( Comentarios.getText().toString() == "")
        {
            Toast.makeText(this, "Es necesario ingresar comentarios", Toast.LENGTH_LONG).show();
        }
        else
        {
            ComentarioServicio = Comentarios.getText().toString();
        }

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idVisita = Integer.parseInt(Preferences.getPreference(CapturarServicio.this, "idVisita"));
                usuarioTecnico = Preferences.getPreference( CapturarServicio.this,"usuarioTecnico");
                idServicio = Integer.parseInt(Preferences.getPreference(CapturarServicio.this, "idServicio"));
                        new AsyncTask<String, Void, Void>()
                {

                    @Override
                    protected Void doInBackground(String... params) {

                        JSONObject body = new JSONObject();
                        JSONObject parametros = new JSONObject();
                        try {
                            parametros.put("idServicio", idServicio);
                            parametros.put("idVisita", idVisita);
                            if(bitmap == null)
                            {
                                parametros.put("cadenaFotografia", " ");
                            }
                            else
                            {
                                parametros.put("cadenaFotografia", getByteArray().toString());
                                Log.d("Respuesta", getByteArray());
                            }
                            parametros.put("comentarioServicio", ComentarioServicio);
                            parametros.put("usuarioTecnico", usuarioTecnico );

                            body.put("informacionServicio", parametros);
                            if(Preferences.getPreference(CapturarServicio.this,"nombreServicio").toString().toLowerCase().contains("devolucion"))
                            {
                                parametros.put("devolvioRefacciones",true);
                            }
                            Log.d("Respuesta", body.toString());
                            String UrlServicio = METHOD.URL_BASE + METHOD.CAPTURAR_SERVICIO;
                            String respuesta = post(UrlServicio, body.toString());
                            Log.d("Respuesta", respuesta);

                            JSONObject respue = new JSONObject(respuesta);
                            JSONObject servicioResult = respue.getJSONObject("insertarInformacionServicioResult");
                            boolean seEjecutoConExito = servicioResult.getBoolean("seEjecutoConExito");
                            if(seEjecutoConExito)
                            {
                                //Todo Logica que regrese a la pantalla anterior
                                Log.d("Respuesta", "Exito");
                                startActivity(serviciosIntent);
                            }
                            else
                            {
                                Toast.makeText(CapturarServicio.this, servicioResult.getString("mensaje").toString(), Toast.LENGTH_SHORT).show();
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
        });
    }

    ///TODO Quitar los Log
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Imagen", "Entrando al onActivityResult");
        Log.d("Imagen",  "resultCode: " + resultCode + "requestCode: " + requestCode + "RESULT_OK: " + RESULT_OK);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("Imagen", "Entrando al if del requestCode" + "resultCode: " + resultCode + "requestCode: " + requestCode + "RESULT_OK: " + RESULT_OK);
            Bundle extras = data.getExtras();
            if(bitmap == null)
            {
                Log.d("Imagen", "Creando BitMap en ImgView");
                bitmap = (Bitmap) extras.get("data");
                imgFoto.setImageBitmap(bitmap);
            }

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
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String getByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
