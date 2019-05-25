package com.rightway.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rightway.myapplication.ANTPlusHeartRateWatcher;
import com.rightway.myapplication.Constantes.constantes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Activity_Main extends Activity {

	private static final String TAG = "Heart";
	static private ANTPlusHeartRateWatcher watcher;
	private String pulsos;
	private String file = "IP_Direccion.txt";
	private String file2 = "ID_Save.txt";

	private TextView connStatusTextView, recStatusTextView, elapsTimeTextView, currentPulseTextView;
	// TODO why are these public?
	public ImageButton connectButton, startStopButton;

	public RecordingListView getRecordingListView() {
		return recordingListView;
	}

	private RecordingListView recordingListView;



	private int REQUEST_PERMISSION =1;
	private int REQUEST_PERMISSION2 =2;
	private Timer multifuncion = new Timer();
	private int contadorPregunta;
	private ConnexionTCP sendData;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*******************************Para que La pantalla no se apague*********************/
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Permisos();

		findViews();


		// create and initialize new ANTPlusHeartRateWatcher instance
		watcher = new ANTPlusHeartRateWatcher(this);
		LeerIp();
		LeerId();
		startTimer();
	}



	/********/

	private void startTimer(){
		try {
			multifuncion.scheduleAtFixedRate(new SendMultifuncion(), 0, 1000);
		}catch (Exception e){
			stopTimer();
			ReStartTimer();
			e.printStackTrace();
		}

	}
	public void ReStartTimer(){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				startTimer();


			}
		}, 2000);
	}
	private void stopTimer(){
		multifuncion.cancel();
	}

	private class SendMultifuncion extends TimerTask {
		public void run() {
			contadorPregunta++;
			if(contadorPregunta > 1){
				contadorPregunta = 0;
//                Intent sendSocket = new Intent();
//                sendSocket.putExtra("CMD", "EnvioSocket3");
//                sendSocket.setAction(SocketServicio.ACTION_MSG_TO_SERVICE);
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sendSocket);

				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				DatosTransferDTO datosTransferDTO = new DatosTransferDTO();
				datosTransferDTO.setId(sharedPreferences.getString(constantes.IdConcursante, ""));
				datosTransferDTO.setValor(pulsos);
				Gson gson = new Gson();

				String json = gson.toJson(datosTransferDTO);

				Log.i(TAG, "=====DEbe enviar:  "  +  json);

				sendData = new ConnexionTCP(getApplicationContext());
				sendData.sendData(json);

			}
		}

	}

	/**********************************************************************************************/
	public void LeerIp(){
		String line = null;

		try {
			FileInputStream fileInputStream = new FileInputStream (new File(Environment.getExternalStorageDirectory() + "/Download/" + file));
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder stringBuilder = new StringBuilder();

			while ( (line = bufferedReader.readLine()) != null )
			{
				stringBuilder.append(line + System.getProperty("line.separator"));
			}
			fileInputStream.close();
			line = stringBuilder.toString();

			line = line.replace(" ", "");
			line = line.replace("\r\n", "");
			line = line.replace("\r", "");
			line = line.replace("\n", "");


			Log.i(TAG, "-----Lo q se lee es"  +  line + "-----");

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if(line.equalsIgnoreCase("1.1.1.1")){
				editor.putString(constantes.IPSocket, "192.168.122.100");
			}else{
				editor.putString(constantes.IPSocket, line);
			}
			editor.commit();

			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
			Log.d(TAG, ex.getMessage());
		}
		catch(IOException ex) {
			ex.printStackTrace();
			Log.d(TAG, ex.getMessage());
		}
	}
	public void LeerId(){
		String line = null;

		try {
			FileInputStream fileInputStream = new FileInputStream (new File(Environment.getExternalStorageDirectory() + "/Download/" + file2));
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder stringBuilder = new StringBuilder();

			while ( (line = bufferedReader.readLine()) != null )
			{
				stringBuilder.append(line + System.getProperty("line.separator"));
			}
			fileInputStream.close();
			line = stringBuilder.toString();

			line = line.replace(" ", "");
			line = line.replace("\r\n", "");
			line = line.replace("\r", "");
			line = line.replace("\n", "");


			Log.i(TAG, "-----Lo q se lee es"  +  line + "-----");

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(constantes.IdConcursante, line);

			editor.commit();

			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
			Log.d(TAG, ex.getMessage());
		}
		catch(IOException ex) {
			ex.printStackTrace();
			Log.d(TAG, ex.getMessage());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				MuestraAlertMensaje("Cerrar Aplicación.", "Desea cerrar la aplicación?");
				return true;
			case KeyEvent.KEYCODE_HOME:
				Log.i(TAG, "Se Oprimio el Boton de Back");
		}
		return super.onKeyDown(keyCode, event);
	}

	/*============================================================================================*/
	/*============================================================================================*/
	public void MuestraAlertMensaje(String titulo, String mensaje) {
		Log.i(TAG, " ================ SE MUESTRA MENSAJE EN ALERT : " + mensaje);
		ArrayList<Integer> idRoom = new ArrayList<>();
		try {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle(titulo);
			if(mensaje==null) alertBuilder.setMessage("timeout intente de nuevo.");
			else alertBuilder.setMessage(mensaje);
			alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
			alertBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					multifuncion.cancel();
					finish();

				}
			});

			alertBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			AlertDialog dialog = alertBuilder.create();
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Permisos(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					REQUEST_PERMISSION);

			//return;
		}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					REQUEST_PERMISSION2);

			return;
		}
	}

	/**********************************************************************************************/
	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission granted.
			} else {
				// User refused to grant permission.
			}
		}else  if (requestCode == REQUEST_PERMISSION2) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission granted.
			} else {
				// User refused to grant permission.
			}
		}
	}
	/***************/



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setConnStatus ( String s ) {
		connStatusTextView.setText(s);
	}

	public void setRecStatus ( String s ) {
		recStatusTextView.setText(s);
	}

	public void setPulse ( String s ) {
		pulsos = s;
		currentPulseTextView.setText(s);
	}

	public void setElapsTime ( String s ) { elapsTimeTextView.setText(s); }

	// connect-disconnect button onClick() handler
	public void connDisconnOnClickHandler(View view) {
		if ( watcher.isConnected() ) {
			watcher.disconnect(false);
		} else {
			watcher.connect();
		}
	}
	
	// start-stop button onClick() handler
	public void startStop(View view) {
		if (watcher.isRunning()) {
			RecordingListItem rli = watcher.stop();
			recordingListView.addItem(rli);
		} else {
			watcher.start();
		}
	}

	protected void findViews() {
		connectButton = (ImageButton) findViewById(R.id.connectButton);
		connStatusTextView = (TextView) findViewById(R.id.connStatusTextView);
		startStopButton = (ImageButton) findViewById(R.id.startStopButton);
		recStatusTextView = (TextView) findViewById(R.id.recStatusTextView);
		elapsTimeTextView = (TextView) findViewById(R.id.elapsTimeTextView);
		currentPulseTextView = (TextView) findViewById(R.id.currentPulseTextView);
		recordingListView = (RecordingListView) findViewById(R.id.recordingListView);
	}
	
}
