package ch.delavega_schumacher.schrittzaehler;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import ch.delavega_schumacher.appquestfunctions.Logging.Logbook;
import ch.delavega_schumacher.appquestfunctions.android.Application;
import ch.delavega_schumacher.schrittzaehler.R;
import ch.delvega_schumacher.appquestfunctions.android.stepcounter.StepCounter;
import ch.delvega_schumacher.appquestfunctions.android.stepcounter.StepListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Schrittzaehler extends Activity implements StepListener {

	public SensorEventListener stepCounter;
	public SensorManager mSensorManager;
	public StepManager mStepManager;
	public Sensor mAccelerometer;

	public TextView tvCommands, tvSteps;

	public boolean isWalkingOnGoing = false;

	public Application application = Application.getInstance();
	public Logbook Log = Logbook.getInstance();

	/* Daten für Applikation spezifisch */
	private int startstation = 0;
	private int endstation = 0;
	private JSONArray instructions;

	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schrittzaehler);

		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		stepCounter = new StepCounter(this);

		tvCommands = (TextView)findViewById(R.id.tvCommands);
		tvSteps = (TextView)findViewById(R.id.tvSteps);

		Button btnStart = (Button)findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				setStepCounter();
			}
		});

		Button btnStop = (Button)findViewById(R.id.btnStop);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schrittzaehler, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case(R.id.log):
			this.log();
		break;
		case(R.id.scan):
			Intent stationScanner = Log.scan(this); 
		startActivityForResult(stationScanner, SCAN_QR_CODE_REQUEST_CODE);
		break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String qrCode = intent.getStringExtra("SCAN_RESULT");
				try {
					saveStation(qrCode);
					setGui();
				} catch (JSONException e) {

				}
			}
		}
	}
	
	public void onResume()
	{
		super.onResume();

		if(isWalkingOnGoing)
		{
			setStepCounter();
		}
	}

	public void onPause()
	{
		super.onPause();
		unsetStepCounter();
	}

	/* eigene Funktionen*/
	public void setStepCounter()
	{
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(stepCounter, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	public void unsetStepCounter()
	{
		mSensorManager.unregisterListener(stepCounter); 
		mAccelerometer = null;
	}
	
	public void setGui() throws JSONException
	{
		try
		{
			if(mStepManager.isTourFinished() == false && mStepManager.isAboutToTurn() == false && mStepManager.hasStepsLeft() == false) // der StepManager hat zwar noch Instructions, wurde aber noch nicht initialisiert
			{
				mStepManager.setNextInstructions();
			}

			if(mStepManager.isAboutToTurn())
			{
				tvCommands.setText("please turn");
			}

			if(mStepManager.hasStepsLeft())
			{
				tvCommands.setText("make your steps");
				tvSteps.setText(mStepManager.getStepsLeft() + " steps left");
			}
			
			if(mStepManager.isTourFinished())
			{
				tvCommands.setText("You've finished your tour. Scan the endstation-QRCode");
			}
		}
		catch(Exception ex)
		{
			application.showErrors(this, getString(R.string.error_refreshing_gui_not_possible));
		}
	}

	public void log()
	{			
		try
		{
			String taskname, logMessage;

			JSONObject logObject = new JSONObject();
			logObject.put("endStation", endstation);
			logObject.put("startStation", startstation);

			taskname = "Schrittzähler";
			logMessage = logObject.toString();

			Intent Logger = Log.log(this, taskname, logMessage);
			startActivity(Logger);
		}
		catch(Exception ex)
		{
			application.showErrors(this, getResources().getString(R.string.error_logging_not_possible));
		}
	}

	public void saveStation(String qrCode) throws JSONException
	{
		JSONObject stationObject = new JSONObject(qrCode);

		if(startstation == 0)
		{
			startstation = Integer.parseInt(stationObject.getString("startStation"));
			instructions = stationObject.getJSONArray("input");
			
			mStepManager = new StepManager(instructions);
		}
		else
		{
			endstation = Integer.parseInt(stationObject.getString("endStation"));
		}	
	}

	/* implemented Listener Functions => some functions are only-used for the StepCounter */

	@Override
	public void onStep() {
		try {
			if(mStepManager.isAboutToTurn())
			{
				mStepManager.makeTurn();
			}
			else
			{
				mStepManager.makeStep();
			}
				
			setGui();
				
			if(mStepManager.isTourFinished())
			{
				unsetStepCounter();
			}
			
		} catch (JSONException e) {
			
		}
	}
}
