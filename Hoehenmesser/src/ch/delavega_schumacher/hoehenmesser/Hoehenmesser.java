package ch.delavega_schumacher.hoehenmesser;

import java.io.IOException;

import ch.delavega_schumacher.hoehenmesser.R;
import ch.delavega_schumacher.appquestfunctions.android.*;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Hoehenmesser extends Activity implements SensorEventListener, SurfaceHolder.Callback, Camera.PictureCallback{

	private Application application = Application.getInstance();
	
	private float[] magneticFieldData = new float[3];
	private float[] accelerationData = new float[3];
	
	private float angle_one = 0; // angle alpha
	private float angle_two = 0; // angle Two (alpha + beta)
	private float angle_temp = 0;
	
	private Camera camera;
	private Camera.PictureCallback cameraCallbackVorschau;
	private Camera.ShutterCallback cameraCallbackVerschluss;
	private SurfaceHolder cameraViewHolder;
	
	private TextView tvAlphaValue, tvAngleTwoValue;
	
	private SensorManager sensorManager;
	private Sensor metalldetector;
	private Sensor accelerationSensor;
	
	/* Android Activity Functions */
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoehenmesser);
        
        /* Sensoren */
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        metalldetector = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        sensorManager.registerListener(this, metalldetector, SensorManager.SENSOR_DELAY_NORMAL);  
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
             
        tvAlphaValue = (TextView)findViewById(R.id.tvAlphaValue);
        tvAngleTwoValue = (TextView)findViewById(R.id.tvAngleTwoValue);
        
        /* Controls */
        
        Button btnSaveAngle = (Button)findViewById(R.id.btnSave);
        btnSaveAngle.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				btnSaveAngles_Click();
			}
		});
        
        Button btnClearAngles = (Button)findViewById(R.id.btnClearAngles);
        btnClearAngles.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		btnClearAngles_Click();
        	}
        });

    }
    
    protected void onResume()
    {
    	super.onResume();
    	
    	resetGUI();
    	
    	initializeCamera();
    	
    }
    
    public void onPause() {
    	super.onPause();
	    
    	if (camera == null) {
    		return;
	    }
	    
    	camera.stopPreview();
    	camera.release();
    	camera = null;
	}
    
    public void onDestroy()
    {
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hoehenmesser, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId())
		{
			case(R.id.new_calculation):
			{
				startCalculationActivity();
				break;
			}
		}
    	
    	return true;
    }
    
    /* Listener */
    
    public void btnSaveAngles_Click()
    {
    	this.saveAngle();
    }
    
    public void btnClearAngles_Click()
    {
    	this.resetGUI();
    }

    /* Eigene Funktionen */

    public void initializeCamera()
    {
    	SurfaceView cameraView = (SurfaceView) findViewById(R.id.svCameraAngleView);
    	cameraViewHolder =cameraView.getHolder();
    	cameraViewHolder.addCallback(this);
    	cameraViewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    	
    	cameraCallbackVorschau = new Camera.PictureCallback() {
    		public void onPictureTaken(byte data[], Camera c)
    		{
    			
    		}
    	};
    	
    	cameraCallbackVerschluss = new Camera.ShutterCallback()
    	{
    		public void onShutter()
    		{
    			
    		}
    	};
    }
    
    public void resetGUI()
    {
    	tvAlphaValue.setText("0");
    	tvAngleTwoValue.setText("0");
    	
    	this.angle_one = 0;
    	this.angle_two = 0;
    }
    
    public void saveAngle()
    {       	    	
			if(angle_one == 0.0)
			{
				angle_one = angle_temp;
				tvAlphaValue.setText(String.valueOf(angle_one));
			}
			else
			{
				angle_two = angle_temp;
				tvAngleTwoValue.setText(String.valueOf(angle_two));
			}
			
			if(angle_one > 0 && angle_two > 0)
			{
				startCalculationActivity();
			}
    }
    
    protected void startCalculationActivity()
    {
    	Intent intentCalculator = new Intent(this, Hoehencalculator.class);
    	
    	intentCalculator.putExtra("Alpha", Float.parseFloat(tvAlphaValue.getText().toString()));
    	intentCalculator.putExtra("AngleTwo", Float.parseFloat(tvAngleTwoValue.getText().toString()));
    	
    	startActivity(intentCalculator);
    }
    
	public float getCurrentRotationValue()
	{
		float[] rotationMatrix = new float[16];
		
		if(SensorManager.getRotationMatrix(rotationMatrix, null, accelerationData, magneticFieldData))
		{
			float[] orientation = new float[4];
			
			SensorManager.getOrientation(rotationMatrix, orientation);
			
			double neigung = Math.toDegrees(orientation[2]);
			
			return (float)Math.abs(neigung);
		}
			return 0;
	}
	
	  public int getRotation(){
		  int angle;
          Display display = this.getWindowManager().getDefaultDisplay();
          switch (display.getRotation()) {
              case Surface.ROTATION_0: // This is display orientation
                  angle = 90; // This is camera orientation
                  break;
              case Surface.ROTATION_90:
                  angle = 0;
                  break;
              case Surface.ROTATION_180:
                  angle = 270;
                  break;
              case Surface.ROTATION_270:
                  angle = 180;
                  break;
              default:
                  angle = 90;
                  
          }
       return angle;
	  }
	  
	/* Sensoren und Listener */  

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			System.arraycopy(event.values, 0, accelerationData, 0, 3);
		}
		
		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
		{
			System.arraycopy(event.values, 0, magneticFieldData, 0, 3);
		}
		
		this.angle_temp = getCurrentRotationValue();
	}
	
	  
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		camera.stopPreview();
		camera.setDisplayOrientation(getRotation()); 
		
		Camera.Parameters params = camera.getParameters();
		Camera.Size vorschauGroesse = params.getPreviewSize();
		params.setPreviewSize(vorschauGroesse.width,
							  vorschauGroesse.height);
		camera.setParameters(params);
		
		try{
			camera.setPreviewDisplay(holder);
		} catch(IOException e)
		{
			application.showErrors(this,"It wasn't possible to show the preview for some reasons. Please contact the administrator.");
		}
		
		camera.startPreview();
		
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	
	}

	@Override
	public void onPictureTaken(byte[] size, Camera camera) {
			
	}
}
