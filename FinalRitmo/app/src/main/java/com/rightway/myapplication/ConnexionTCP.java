package com.rightway.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.rightway.myapplication.Constantes.constantes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ConnexionTCP {

    private static final String TAG = "Heart";
    private static final String MODULO = "TCP";
    private final String ACTION_STRING_ACTIVITY = "ToActivity";
    private Socket socket;


    protected PrintWriter dataOutputStream;
    protected InputStreamReader dataInputStream;
    private String mensajeEncriptado;
    private Context context;
    public AlertDialog alert;



    public ConnexionTCP(Context _context) {
        try{
//			context = _context;
//			appState = (AppUsuario) _context;

            this.context = _context;



        }catch(Exception e){
            Log.e(TAG, MODULO + "  "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendData(final String data) {
        mensajeEncriptado = data;
        Log.i(TAG, MODULO + "================================Mensaje Enviado:      " + mensajeEncriptado);


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    Log.i(TAG, MODULO + "================================Io:" + sharedPreferences.getString(constantes.IPSocket, "")+ "------");
                    //String IP = sharedPreferences.getString(constantes.IPSocket, "");
                    String IP = "200.91.204.38";
                    int Puerto = constantes.PuertoSocket;
                    socket = new Socket(IP, Puerto);
                    socket.setSoTimeout(1000);


                    dataOutputStream = new PrintWriter(socket.getOutputStream(), true);


                    //dataOutputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_16), true);


                    dataInputStream = new InputStreamReader(socket.getInputStream(), "LATIN1");
                    Log.i(TAG, MODULO + "Socket y Flujos creados " + Puerto + "  "  +  IP);
                    dataOutputStream.println(mensajeEncriptado + "\n\r");


                    String dataSocket = new BufferedReader(dataInputStream).readLine();
                    String mensajeDesencriptado;
                    mensajeDesencriptado= dataSocket;
                    Log.i(TAG, MODULO  + "========================= SE RECIBE: "+ mensajeDesencriptado+"\n");
                    if (mensajeDesencriptado != null) {
                        ProcessRespuesta(mensajeDesencriptado);
                    }

                } catch (UnknownHostException e) {
                    Log.e(TAG, MODULO + "Error tipo: UnknownHostException");
                    e.printStackTrace();
                } catch (ConnectException e) {
                    Log.e(TAG, MODULO + "Error tipo: ConnectException");

                    sendData(data);

                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    Log.e(TAG, MODULO + "Error por SocketTimeoutException   " );
                    e.printStackTrace();
                    Intent error = new Intent();
                    error.putExtra("CMD", "Error");
                    error.putExtra("DATA", "SocketTimeoutException");
                    error.setAction(ACTION_STRING_ACTIVITY);
                    context.sendBroadcast(error);
                } catch (IOException e) {
                    Log.e(TAG, MODULO + "Error tipo: IOException");
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, MODULO + "Dando por terminada la tarea del Soket, se cierran los flujos y conexin");
                    if (socket != null) {
                        try {
                            if (dataOutputStream != null) {
                                dataOutputStream.close();
                            }
                            if (dataInputStream != null) {
                                dataInputStream.close();
                            }
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }).start();
    }

    private void ProcessRespuesta(String datos) {

        try {

            Gson gson=new Gson();
            DatosTransferDTO informacion = null;
            try {
                informacion = gson.fromJson(datos, DatosTransferDTO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.i("TaxistaPanama", "-------------Error-------------------   " + informacion.getError());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
