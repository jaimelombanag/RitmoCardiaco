package com.rightway.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rightway.myapplication.ANTPlusHeartRateWatcher;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_Main extends Activity {

	private static final String TAG = "Activity_Main";
	static private ANTPlusHeartRateWatcher watcher;

	private TextView connStatusTextView, recStatusTextView, elapsTimeTextView, currentPulseTextView;
	// TODO why are these public?
	public ImageButton connectButton, startStopButton;

	public RecordingListView getRecordingListView() {
		return recordingListView;
	}

	private RecordingListView recordingListView;



	private Timer multifuncion = new Timer();
	private int contadorPregunta;
	private ConnexionTCP sendData;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();

		// create and initialize new ANTPlusHeartRateWatcher instance
		watcher = new ANTPlusHeartRateWatcher(this);

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
				datosTransferDTO.setFuncion("");
				datosTransferDTO.setIdConcursante("");
				Gson gson = new Gson();

				String json = gson.toJson(datosTransferDTO);
				sendData = new ConnexionTCP(getApplicationContext());
				sendData.sendData(json);

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
