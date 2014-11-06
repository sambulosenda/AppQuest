package ch.delavega_schumacher.metalldetektor;

import ch.delavega_schumacher.appquestfunctions.Logging.Logbook;
import ch.delavega_schumacher.appquestfunctions.Mathematics.Formulas.CalculatingHelper;
import ch.delavega_schumacher.appquestfunctions.android.Application;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Metalldetektor extends Activity implements SensorEventListener{

	private SensorManager SensorManager;
	private Sensor metalldetector = null;
	private ProgressBar pbMagneticField;
	private TextView tvMagneticField;
	
	private static Application application = Application.getInstance();
	private static CalculatingHelper calcHelper = CalculatingHelper.getInstance();
	private static Logbook Log = Logbook.getInstance();	
	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;
	
	/* Android Activity Functions */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.metalldetektor, menu);
		return true;
	}
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
 		   if (resultCode == RESULT_OK) {
 			   String qrCode = intent.getStringExtra("SCAN_RESULT");
 			   log(qrCode);
 		   }
 	   }
    }
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case(R.id.log):
				Intent barcodeScanner = Log.scan(this);
				startActivityForResult(barcodeScanner, SCAN_QR_CODE_REQUEST_CODE);
				break;
		}
   
		return super.onOptionsItemSelected(item);
	}
	
	protected  void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.activity_metalldetektor);
		
        SensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        metalldetector = SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorManager.registerListener(this, metalldetector, SensorManager.SENSOR_DELAY_NORMAL);    	
        
        pbMagneticField = (ProgressBar)findViewById(R.id.progressBar);
        pbMagneticField.setMax(getResources().getInteger(R.integer.progressbar_magnetic_field_maximum_show));
        
        tvMagneticField = (TextView)findViewById(R.id.textView1);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		SensorManager.registerListener(this, metalldetector, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onPause()
	{
		super.onPause();
		SensorManager.unregisterListener(this);
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		SensorManager.unregisterListener(this);
	}
	
	/* own Functions */
	
	private void log(String qrCode)
	{			
		try
		{
			String taskname, logMessage;
		
			taskname = "Metalldetektor";
			logMessage = qrCode;
			
			Intent Logger = Log.log(this, taskname, qrCode);
			startActivity(Logger);
		}
		catch(Exception ex)
		{
			application.showErrors(this, "The Logging didn't work for some reason, please contact the administrator.");
		}
	}
	
	public void updateView(double magneticValue)
	{
		int magneticValue_i = (int) magneticValue;
		tvMagneticField.setText(Integer.toString(magneticValue_i));
		pbMagneticField.setProgress(magneticValue_i);
	}
	
	/* Sensor Changings */

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent metallEvent) {
		
		float[] sensorValues = metallEvent.values;
		double metall_field = calcHelper.getVector(sensorValues);
		
		updateView(metall_field);
	}
}
