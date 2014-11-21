package ch.delavega_schumacher.schrittzaehler;

import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import ch.delavega_schumacher.appquestfunctions.Logging.Logbook;
import ch.delavega_schumacher.appquestfunctions.android.Application;
import ch.delavega_schumacher.schrittzaehler.R;
import ch.delvega_schumacher.appquestfunctions.android.stepcounter.StepCounter;
import ch.delvega_schumacher.appquestfunctions.android.stepcounter.StepListener;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Schrittzaehler extends Activity implements StepListener {

	public SensorEventListener stepCounter;
	public SensorManager mSensorManager;
	public StepManager mStepManager;
	public Sensor mAccelerometer;

	public TextView tvCommands, tvSteps;
	public ImageView imgDirection;

	public boolean isWalkingOnGoing = false;

	private TextToSpeech ttsstepspeaker;

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
		imgDirection = (ImageView)findViewById(R.id.ivDirection);
		imgDirection.setVisibility(View.INVISIBLE);

		setSpeaker();
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
		case(R.id.restart):
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
			setSpeaker();
			try {
				setGui();
			} catch (JSONException e) {

			}
		}
	}

	public void onPause()
	{
		super.onPause();

		unsetSpeaker();
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
			isWalkingOnGoing = false;
			imgDirection.setVisibility(View.INVISIBLE);
			if(mStepManager.isTourFinished() == false && mStepManager.isAboutToTurn() == false && mStepManager.hasStepsLeft() == false) // der StepManager hat zwar noch Instructions, wurde aber noch nicht initialisiert
			{
				mStepManager.setNextInstructions();
				isWalkingOnGoing = true;
			}

			if(mStepManager.hasStepsLeft())
			{
				tvCommands.setText("make your steps");
				tvSteps.setText(mStepManager.getStepsLeft() + " steps left");
				isWalkingOnGoing = true;
				talkDirtyToMe(String.valueOf(mStepManager.getStepsLeft()));
			}
			else
			{
				talkDirtyToMe(String.valueOf(mStepManager.getStepsLeft()));
				tvSteps.setText("0 steps left");
			}

			if(mStepManager.isAboutToTurn())
			{
				isWalkingOnGoing = true;
				imgDirection.setVisibility(View.VISIBLE);
				talkDirtyToMe("turn " + mStepManager.getTurningDirection());

				Drawable arrow;

				if(mStepManager.getTurningDirection().equals("left"))
				{
					arrow = getResources().getDrawable(R.drawable.arrow_left);
				}
				else
				{
					arrow = getResources().getDrawable(R.drawable.arrow_right);
				}

				imgDirection.setImageDrawable(arrow);

				tvCommands.setText("please turn");
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

	public void setSpeaker()
	{
		ttsstepspeaker=new TextToSpeech(getApplicationContext(), 
				new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR){
					ttsstepspeaker.setLanguage(Locale.UK);
				}				
			}
		});
	}

	public void unsetSpeaker()
	{
		if(ttsstepspeaker !=null){
			ttsstepspeaker.stop();
			ttsstepspeaker.shutdown();
		}
	}

	// "Talk dirty" - Jason Derulo: http://www.youtube.com/watch?v=RbtPXFlZlHg <= viel Spass :)
	public void talkDirtyToMe(String textToSpeak)
	{
		try
		{
			ttsstepspeaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
		}
		catch(Exception ex)
		{
			application.showErrors(this, getString(R.string.error_speaking_not_possible));
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
			application.showErrors(this, getString(R.string.error_counting_steps_not_possible));
		}
	}
}
