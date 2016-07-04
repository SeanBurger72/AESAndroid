package com.example.akntom.seguridadinformatica;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.pubnub.api.*;
import org.json.*;


public class MainActivity extends AppCompatActivity {


    TextView ventanaMensajeEncriptado, ventanaMensajeDesencriptado;
    EditText editTextMensaje;
    String  decryptText = "";
    String llave = "01234567890123456789012345602048";

    Pubnub pubnub = new Pubnub("pub-c-d72b53a1-b830-4b5e-b55f-9ddeb66e72e6", "sub-c-7d21fdee-114e-11e6-b422-0619f8945a4f");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMensaje = (EditText) findViewById(R.id.edit_mensaje);
        ventanaMensajeEncriptado = (TextView) findViewById(R.id.ventanaMensajeEncriptado);
        ventanaMensajeDesencriptado = (TextView) findViewById(R.id.ventanaMensajeDesencriptado);

        try {
            pubnub.subscribe("SeguridadInformatica", new Callback() {

                        @Override
                        public void connectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            String mensaje;
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            mensaje = message.toString();
                            System.out.println("Mensaje a desencriptar: " + mensaje);
                            onReceivedMessage(mensaje);

                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    public void onClickEnviar(View view)
    {
        String mensaje = "", mensajeEnc = "";
        AES aes;
        Callback callback = new Callback(){};
        try
        {
            mensaje = editTextMensaje.getText().toString();
            aes = new AES();
            aes.addKey(llave);
            mensajeEnc = aes.encryptComplete(mensaje);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println();
        pubnub.publish("SeguridadInformatica", mensajeEnc, callback);
    }

    public void onReceivedMessage(final String mensaje)
    {
        AES aes = new AES();

        try
        {

            aes.addKey(llave);
            decryptText = aes.decryptComplete(mensaje);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ventanaMensajeEncriptado.setText(mensaje);
                    ventanaMensajeDesencriptado.setText(decryptText);
                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }


}
