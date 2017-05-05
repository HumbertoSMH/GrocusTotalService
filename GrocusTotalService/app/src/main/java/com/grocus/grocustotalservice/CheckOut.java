package com.grocus.grocustotalservice;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grocus.grocustotalservice.Utils.LocationServiceImpl;
import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;
import com.grocus.grocustotalservice.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckOut extends AppCompatActivity {

    //UI Elements for sign
    LinearLayout mContent;
    signature mSignature;
    //Button mClear, mGetSign, mCancel;
    Button mClear;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;

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

    EditText comentarios;

    String ComentariosPost,
            usuarioTecnico ,
            fechaCheck,
            latitud,
            longitud,
            cadenaFirmaResponsable,
            cadenaFotografiaFrente,
            cadenaFotografiaLateralDerecha,
            cadenaFotografiaLateralIzquierda,
            cadenaFotografiaAtras;

    int idVisita;

    LocationServiceImpl locationService;

    View mView;
    File mypath;
    TextView lblServicio;
    TextView lblResponsable;
    private String uniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        locationService = LocationServiceImpl.getLocationManager(this);

        //EditText y textView
        comentarios = (EditText) findViewById(R.id.txtComentariosCO);
        lblServicio = (TextView) findViewById(R.id.lblIdServicioCO);
        lblResponsable = (TextView) findViewById(R.id.lblResponsableCO);

        lblResponsable.setText(Preferences.getPreference(this, "Responsable"));
        lblServicio.setText(Preferences.getPreference(this, "idVisita"));


        //Ligamiento de botones
        btnFotoDelantera = (Button)findViewById(R.id.btnFotografiaFCO);
        btnFotoDerecha = (Button)findViewById(R.id.btnFotografiaDCO);
        btnFotoIzquierda = (Button)findViewById(R.id.btnFotografiaICO);
        btnFotoAtras = (Button)findViewById(R.id.btnFotografiaACO);
        guardar = (Button) findViewById(R.id.btnGuardarCO);

        //Ligamiento de IMGView
        imgFotoDelantera = (ImageView) findViewById(R.id.imgFrenteCO);
        imgFotoAtras = (ImageView) findViewById(R.id.imgAtrasCO);
        imgFotoDerecha = (ImageView) findViewById(R.id.imgDerechoCO);
        imgFotoIzquierda = (ImageView) findViewById(R.id.imgIzquierdoCO);

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

        this.preparaFuncionFirma();


        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationService.isGPSEnabled)
                {
                    ComentariosPost = comentarios.getText().toString();
                    latitud = String.valueOf(locationService.latitude);
                    longitud = String.valueOf(locationService.longitude);
                    usuarioTecnico = Preferences.getPreference(CheckOut.this, "usuarioTecnico");
                    idVisita = Integer.parseInt(Preferences.getPreference(CheckOut.this, "idVisita"));
                    fechaCheck = Utils.getCurrentDateForCheck().toString();
                    if(!validaFotografias())
                    {
                        Log.d("Respuesta", "Alguna foto es no valida");
                        Toast.makeText(CheckOut.this, "Verifica que tengas todas las Fotografias y la firma del responsable", Toast.LENGTH_SHORT).show();
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
                                parametros.put("fechaCheck",fechaCheck);
                                parametros.put("latitud",latitud);
                                parametros.put("longitud",longitud);
                                parametros.put("comentario",ComentariosPost);
                                parametros.put("cadenaFotografiaFrente",cadenaFotografiaFrente);//
                                parametros.put("cadenaFotografiaLateralDerecha",cadenaFotografiaLateralDerecha);//
                                parametros.put("cadenaFotografiaLateralIzquierda",cadenaFotografiaLateralIzquierda);//
                                parametros.put("cadenaFotografiaAtras",cadenaFotografiaAtras);//
                                parametros.put("cadenaFirmaResponsable",cadenaFirmaResponsable);//

                                checkOut.put("checkOutVisita", parametros);

                                String UrlServicio = METHOD.URL_BASE + METHOD.CHECKOUT;

                                try {
                                    String respuesta = post(UrlServicio, checkOut.toString());
                                    JSONObject res = new JSONObject(respuesta);
                                    Log.d("errorCheckOut", respuesta);
                                    JSONObject respuestaPost = res.getJSONObject("insertarCheckOutVisitaResult");
                                    boolean seEjecutoConExito = respuestaPost.getBoolean("seEjecutoConExito");
                                    String mensaje = respuestaPost.getString("mensaje");
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
                    Toast.makeText(CheckOut.this, "No se puede Hacer check Out sin el gps Activado", Toast.LENGTH_SHORT).show();
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
        if(mSignature.contieneFirma())
        {
            cadenaFirmaResponsable = Utils.obtenerStringBase64(this.recuperarFirmaEnBase64());
            Log.d("Firma", cadenaFirmaResponsable);
        }
                else
        {
            esValido=false;
        }
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

    /*Seccion de Firma!!! No tocar ya sirve */
    private void preparaFuncionFirma() {

        tempDir = Environment.getExternalStorageDirectory() + "/" + "FirmaTemporal"  + "/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir( "FirmaTemporal"  , Context.MODE_PRIVATE);

        prepareDirectory();
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = uniqueId + ".png";
        mypath= new File(directory,current);


        mContent = (LinearLayout) findViewById(R.id.firmaLinearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        mClear = (Button)findViewById(R.id.clearButton);
        //mGetSign = (Button)findViewById(R.id.getsign);
        //mGetSign.setEnabled(false);
        //mCancel = (Button)findViewById(R.id.cancel);
        mView = mContent;

        //yourName = (EditText) findViewById(R.id.yourName);

        mClear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
            }
        });

    }

    private byte[] recuperarFirmaEnBase64() {
        Log.v("log_tag", "Panel Saved");
        String imagenBase64 = null;
        byte[] imageByteArray = null;
        mView.setDrawingCacheEnabled(true);
        imageByteArray = mSignature.recuperarImagenBase64(mView);

        return imageByteArray;
    }

    private void limpiarCache() {
        Log.v("log_tag", "Panel Saved");
        //boolean error = captureSignature();
        mView.setDrawingCacheEnabled(true);
        mSignature.recuperarImagenBase64(mView);
        Bundle b = new Bundle();
        b.putString("status", "done");
        Intent intent = new Intent();
        intent.putExtras(b);
        setResult(RESULT_OK,intent);
    }



    //TODO MAMM Volver una utileria
    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:",String.valueOf(todaysDate));
        return(String.valueOf(todaysDate));

    }

    //TODO MAMM Volver una utileria
    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:",String.valueOf(currentTime));
        return(String.valueOf(currentTime));

    }
    //TODO MAMM Volver una utileria
    private boolean prepareDirectory()
    {
        try
        {
            if (makedirs())
            {
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }


    //TODO MAMM Volver una utileria
    private boolean makedirs()
    {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory())
        {
            File[] files = tempdir.listFiles();
            for (File file : files)
            {
                if (!file.delete())
                {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1==1)
            {
                final Intent serviciosIntent = new Intent(CheckOut.this,ListadoServicios.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOut.this);
                builder.setMessage("Check Out Exitoso");
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
                Toast.makeText(CheckOut.this,"No se pudo realizar el check out".toString(), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    //INNER CLASS
    class signature extends View
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public byte[] recuperarImagenBase64(View v)
        {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            String imagenBase64 = null;
            byte[] byteArray = null;
            if(mBitmap == null)
            {
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);;
            }
            Canvas canvas = new Canvas(mBitmap);
            try
            {

                v.draw(canvas);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();


            }
            catch(Exception e)
            {
                Log.v("log_tag", e.toString());
            }
            return byteArray;
        }

        public void clear()
        {
            path.reset();
            invalidate();
        }


        public  boolean contieneFirma(){
            return !path.isEmpty();
        }


        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float eventX = event.getX();
            float eventY = event.getY();
            //mGetSign.setEnabled(true);

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++)
                    {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string){
        }

        private void expandDirtyRect(float historicalX, float historicalY)
        {
            if (historicalX < dirtyRect.left)
            {
                dirtyRect.left = historicalX;
            }
            else if (historicalX > dirtyRect.right)
            {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top)
            {
                dirtyRect.top = historicalY;
            }
            else if (historicalY > dirtyRect.bottom)
            {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY)
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
