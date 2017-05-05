package com.grocus.grocustotalservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devazt.networking.HttpClient;
import com.devazt.networking.OnHttpRequestComplete;
import com.devazt.networking.Response;
import com.grocus.grocustotalservice.Utils.METHOD;
import com.grocus.grocustotalservice.Utils.Preferences;

import org.json.JSONObject;

import java.util.List;

public class IniciarSesion extends AppCompatActivity implements OnHttpRequestComplete{

    //Declaramos los controles
    EditText txtusuario,
             txtpassword;

    Button btnIniciarSesion;

    //Crear el Intent Para la lista de visitas
    Intent ListadoVisitasI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        txtusuario = (EditText) findViewById(R.id.txtUsuario);
        txtpassword = (EditText) findViewById(R.id.txtPassword);

        btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        ListadoVisitasI = new Intent(this, ListadoServicios.class);

        final HttpClient client = new HttpClient(this);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {

                    String URLServicio = METHOD.URL_BASE + METHOD.VALIDATE_LOGIN_TECNICO;
                    if(txtusuario.equals("") || txtpassword.equals(""))
                    {
                        Toast usuario = Toast.makeText(getApplicationContext(),"Debe introducir el nombre de usuario", Toast.LENGTH_SHORT);
                        usuario.show();
                    }
                    else
                    {
                        URLServicio = URLServicio+"usuarioTecnico="+txtusuario.getText().toString() + "&" + "passwordTecnico="+txtpassword.getText().toString();
                        client.excecute(URLServicio);
                    }

                }
                catch (Exception e)
                {
                    Toast.makeText(IniciarSesion.this, "No se puede comunicar con el servidor, Favor de comunicarse con el administrador", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    public void onComplete(Response status) {

        if(status.isSuccess())
        {

            try
            {
                JSONObject jsonObject = new JSONObject(status.getResult());
                boolean LoginExitoso = jsonObject.getBoolean("seEjecutoConExito");
                if(LoginExitoso)
                {

                    Preferences.savePreference(this,"nombreUsuario", jsonObject.getJSONObject("tecnicoVisita").getString("nombreCompleto"));
                    Preferences.savePreference(this, "usuarioTecnico",jsonObject.getJSONObject("tecnicoVisita").getString("nombreUsuario") );
                    startActivity(ListadoVisitasI);
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
}
