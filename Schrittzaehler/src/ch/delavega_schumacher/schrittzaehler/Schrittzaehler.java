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
import ch.delvega_schumacher.appquestfunctions.android.stepcounter.StepCounter;
import ch.delvega_schumacher.appquestfunctions.android.stepcounter.StepListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Schrittzaehler extends Activity implements StepListener {

	public SensorEventListener stepCounter;
	public SensorManager mSensorManager;
	public StepManager mStepManager;
	public Sensor mAccelerometer;
	
	public boolean isWalkingStarted = false;
	
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
       
        mSensorManager.registerListener(stepCounter, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
			} catch (JSONException e) {
				
			}
 		   }
 	   }
    }
    
    /* eigene Funktionen*/
	public void log()
	{			
		try
		{
			String taskname, logMessage;
		
			JSONObject logObject = new JSONObject();
			logObject.put("startStation", startstation);
			logObject.put("endStation", endstation);
			
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
		/* sobald der SensorEventListener aka StepCounter merkt, dass ein 
		 * Schritt getätigt wurde, wird dies an die laufende Activity gemeldet
		 * Danach kann heruntergezählt werden oder sowas
		 */
	}
}
