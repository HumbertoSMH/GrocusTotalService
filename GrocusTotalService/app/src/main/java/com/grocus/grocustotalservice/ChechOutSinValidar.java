package com.grocus.grocustotalservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.devazt.networking.HttpClient;
import com.devazt.networking.OnHttpRequestComplete;
import com.grocus.grocustotalservice.Models.Servicio;
import com.grocus.grocustotalservice.Utils.LocationServiceImpl;
import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;
import com.grocus.grocustotalservice.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChechOutSinValidar extends AppCompatActivity implements OnHttpRequestComplete, AdapterView.OnItemSelectedListener {


    ArrayAdapter arrayAdapter;

    //Controles UI
    TextView lblServicio,
            lblResponsable;

    Spinner comboMotivos;

    EditText comentarios;

    public Bitmap fotoDelantera,
            fotoDerecha,
            fotoIzquierda,
            fotoAtras;

    public ImageView imgFotoDelantera,
            imgFotoDerecha,
            imgFotoIzquierda,
            imgFotoAtras;

    public Button btnFotoDelantera,
            btnFotoDerecha,
            btnFotoIzquierda,
            btnFotoAtras,
            guardar;

    final int REQUEST_CODE_DEL = 0;
    final int REQUEST_CODE_DER = 1;
    final int REQUEST_CODE_IZQ = 2;
    final int REQUEST_CODE_ATR = 3;
    int idMotivo;
    //Atributos para llamada POST
    String ComentariosPost,
            usuarioTecnico ,
            fechaCheck,
            latitud,
            longitud,
            cadenaFotografiaFrente,
            cadenaFotografiaLateralDerecha,
            cadenaFotografiaLateralIzquierda,
            cadenaFotografiaAtras;

    int idVisita;
    List<Integer> idMotivoList = new ArrayList<Integer>();
    LocationServiceImpl locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chech_out_sin_validar);


        locationService = LocationServiceImpl.getLocationManager(this);

        //EditText y textView
        comentarios = (EditText) findViewById(R.id.txtComentariosCOS);
        lblServicio = (TextView) findViewById(R.id.lblIdServicioCOS);
        lblResponsable = (TextView) findViewById(R.id.lblResponsableCOS);
        comboMotivos = (Spinner) findViewById(R.id.comboMotivos);

        lblResponsable.setText(Preferences.getPreference(this, "Responsable"));
        lblServicio.setText(Preferences.getPreference(this, "idVisita"));

        //Ligamiento de botones
        btnFotoDelantera = (Button)findViewById(R.id.btnFotografiaF);
        btnFotoDerecha = (Button)findViewById(R.id.btnFotografiaD);
        btnFotoIzquierda = (Button)findViewById(R.id.btnFotografiaI);
        btnFotoAtras = (Button)findViewById(R.id.btnFotografiaA);
        guardar = (Button) findViewById(R.id.btnGuardarCOS);

        //Ligamiento de IMGView
        imgFotoDelantera = (ImageView) findViewById(R.id.imgFrente);
        imgFotoAtras = (ImageView) findViewById(R.id.imgAtras);
        imgFotoDerecha = (ImageView) findViewById(R.id.imgDerecho);
        imgFotoIzquierda = (ImageView) findViewById(R.id.imgIzquierdo);

        //Botones OnClick
        btnFotoDerecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE_DER);
                }
            }
        });


        btnFotoIzquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE_IZQ);
                }
            }
        });


        btnFotoDelantera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE_DEL);
                }
            }
        });


        btnFotoAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE_ATR);
                }
            }
        });


        HttpClient cliente = new HttpClient(this);
        try
        {
            cliente.excecute(METHOD.URL_BASE + METHOD.OBTENER_MOTIVOS_CHECK_OUT);
        }
        catch(Exception e)
        {

        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationService.isGPSEnabled)
                {
                    ComentariosPost = comentarios.getText().toString();
                    latitud = String.valueOf(locationService.latitude);
                    longitud = String.valueOf(locationService.longitude);
                    usuarioTecnico = Preferences.getPreference(ChechOutSinValidar.this, "usuarioTecnico");
                    idVisita = Integer.parseInt(Preferences.getPreference(ChechOutSinValidar.this, "idVisita"));
                    fechaCheck = Utils.getCurrentDateForCheck().toString();
                    if(!validaFotografias())
                    {
                        Log.d("Respuesta", "Alguna foto es no valida");
                    }

                    //Preparamos Llamada Asincrona al servicio
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {


                            JSONObject checkOut = new JSONObject();
                            JSONObject parametros = new JSONObject();

                            try {

                                parametros.put("usuarioTecnico",usuarioTecnico);
                                parametros.put("idVisita",idVisita);
                                parametros.put("idMotivoCheckOutsinValidar",idMotivo);
                                parametros.put("fechaCheck",fechaCheck);
                                parametros.put("latitud",latitud);
                                parametros.put("longitud",longitud);
                                parametros.put("comentario",ComentariosPost);
                                parametros.put("cadenaFotografiaFrente",cadenaFotografiaFrente);
                                parametros.put("cadenaFotografiaLateralDerecha",cadenaFotografiaLateralDerecha);//
                                parametros.put("cadenaFotografiaLateralIzquierda",cadenaFotografiaLateralIzquierda);//
                                parametros.put("cadenaFotografiaAtras",cadenaFotografiaAtras);//

                               //cadenaFirmaResponsable

                                checkOut.put("checkOutVisita", parametros);
                                Log.d("Json", checkOut.toString());
                                String UrlServicio = METHOD.URL_BASE + METHOD.CHECKOUT_SIN_VALIDAR;

                                try {
                                    String respuesta = post(UrlServicio, checkOut.toString());
                                    Log.d("errorCheckOut", respuesta);
                                    JSONObject respuestaPost = new JSONObject(respuesta);
                                    JSONObject ResultPost = respuestaPost.getJSONObject("insertarCheckOutSinValidarVisitaResult");
                                    boolean seEjecutoConExito = ResultPost.getBoolean("seEjecutoConExito");
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

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                }
                else
                {
                    Toast.makeText(ChechOutSinValidar.this, "No se puede Hacer check Out sin el gps Activado", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Imagen", "Entrando al onActivityResult");
        Log.d("Imagen",  "resultCode: " + resultCode + "requestCode: " + requestCode + "RESULT_OK: " + RESULT_OK);
        if ( resultCode == RESULT_OK) {
            Log.d("Imagen", "Entrando al if del requestCode" + "resultCode: " + resultCode + "requestCode: " + requestCode + "RESULT_OK: " + RESULT_OK);
            Bundle extras = data.getExtras();

            switch (requestCode)
            {
                case REQUEST_CODE_DEL:
                {
                    if(fotoDelantera == null)
                    {
                        Log.d("Imagen", "Creando BitMap en ImgView");
                        fotoDelantera = (Bitmap) extras.get("data");
                        imgFotoDelantera.setImageBitmap(fotoDelantera);
                    }
                }
                break;

                case REQUEST_CODE_ATR:
                {
                    if(fotoAtras == null)
                    {
                        Log.d("Imagen", "Creando BitMap en ImgView");
                        fotoAtras = (Bitmap) extras.get("data");
                        imgFotoAtras.setImageBitmap(fotoAtras);
                    }
                }
                break;

                case REQUEST_CODE_DER:
                {
                    if(fotoDerecha == null)
                    {
                        Log.d("Imagen", "Creando BitMap en ImgView");
                        fotoDerecha = (Bitmap) extras.get("data");
                        imgFotoDerecha.setImageBitmap(fotoDerecha);
                    }
                }
                break;

                case REQUEST_CODE_IZQ:
                {
                    if(fotoIzquierda == null)
                    {
                        Log.d("Imagen", "Creando BitMap en ImgView");
                        fotoIzquierda = (Bitmap) extras.get("data");
                        imgFotoIzquierda.setImageBitmap(fotoIzquierda);
                    }
                }
                break;

                default:
                    Log.d("Imagen", "Sin resultado");

            }


        }
    }

    public boolean validaFotografias()
    {
        boolean esValido = true;

        if(fotoDelantera != null)
        {
            cadenaFotografiaFrente = getByteArray(fotoDelantera).toString();
        }
        else
        {
            esValido = false;
        }
        if(fotoDerecha != null)
        {
            cadenaFotografiaLateralDerecha = getByteArray(fotoDerecha).toString();
        }
        else
        {
            esValido = false;
        }
        if(fotoIzquierda != null)
        {
            cadenaFotografiaLateralIzquierda = getByteArray(fotoIzquierda).toString();
        }
        else
        {
            esValido = false;
        }
        if(fotoAtras !=  null)
        {
            cadenaFotografiaAtras = getByteArray(fotoAtras).toString();
        }
        else
        {
            esValido = false;
        }

        return esValido;
    }


    public String getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 32, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onComplete(com.devazt.networking.Response status) {
        if(status.isSuccess())
        {
            LinkedList list = new LinkedList();

            JSONObject respuesta = null;
            try
            {
                respuesta = new JSONObject(status.getResult());
                JSONArray arrayMotivos = respuesta.getJSONArray("motivosCheckOutSinValidar");
                for(int i = 0; i < arrayMotivos.length(); i++)
                {

                    JSONObject jsonObject =  arrayMotivos.getJSONObject(i);
                    idMotivoList.add(jsonObject.getInt("idMotivo"));
                    list.add(jsonObject.getString("descripcion"));
                }

                arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                comboMotivos.setAdapter(arrayAdapter);
                comboMotivos.setOnItemSelectedListener(this);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
        {
            Toast.makeText(this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
    }



    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1==1)
            {
                final Intent serviciosIntent = new Intent(ChechOutSinValidar.this,  ListadoServicios.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(ChechOutSinValidar.this);
                builder.setMessage("Check Out Sin Validar Exitoso");
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
                Toast.makeText(ChechOutSinValidar.this,"No se pudo realizar el check out", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        idMotivo =  idMotivoList.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
