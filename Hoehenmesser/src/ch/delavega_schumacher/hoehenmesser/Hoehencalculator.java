package ch.delavega_schumacher.hoehenmesser;

import ch.delavega_schumacher.appquestfunctions.Logging.*;
import ch.delavega_schumacher.appquestfunctions.android.Application;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Hoehencalculator extends Activity implements TextWatcher 
{
	private static Logbook Log = Logbook.getInstance();
	private static Application application = Application.getInstance();
	private HeightMeterObject hmo;
	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;
	EditText txtAlpha, txtBeta, txtB, txtA;
	
	/* Android Activity States */
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoehencalculator);
	
		// zwei Winkel sollten schon vorhanden sein		
		
		double alpha = 0, angletwo = 0, beta = 0;
		
		if(getIntent().hasExtra("Alpha") && getIntent().hasExtra("AngleTwo"))
		{
			alpha = getIntent().getExtras().getFloat("Alpha");
			angletwo = getIntent().getExtras().getFloat("AngleTwo");
			
			beta = angletwo - alpha;
		}
			
		// HeightMeterObject instanzieren		
		hmo = new HeightMeterObject(0.0, beta, alpha);	
				
		// alle Controls den Variabeln zuweisen	
		txtAlpha = (EditText)findViewById(R.id.txtAlphaCalc);	
		txtAlpha.setText(String.valueOf(alpha));	
		txtAlpha.addTextChangedListener(this);
					
		txtBeta = (EditText)findViewById(R.id.txtBetaCalc);		
		txtBeta.setText(String.valueOf(beta));	
		txtBeta.addTextChangedListener(this);
				
		txtA = (EditText)findViewById(R.id.txtACalc);	
		txtA.addTextChangedListener(this);
		
		txtB = (EditText)findViewById(R.id.txtBCalc);
		
		Button btnDelete = (Button)findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				resetGUI();
			}
		});
	}
	
	protected void onPause()
	{
		super.onPause();
	}
	
	protected void onResume()
	{
		super.onResume();
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		
		txtAlpha = null;
		txtBeta = null;

		txtA = null;
		txtB = null;
		
		hmo = null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hoehencalculator, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case(R.id.log):
				Intent barcodeScanner = Log.scan(this);
				startActivityForResult(barcodeScanner, SCAN_QR_CODE_REQUEST_CODE);
				break;
			case(R.id.new_angles):
				Intent intentMeasureApp = new Intent(this, Hoehenmesser.class);
	    		startActivity(intentMeasureApp);				
				break;
		}
   
		return super.onOptionsItemSelected(item);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String qrCode = intent.getStringExtra("SCAN_RESULT");
				log(qrCode);
			}
		}
	}

	/* Eigene Funktionen */
	
	public void log(String qrCode)
	{			
		try
		{
			String taskname, logMessage;
			double height = hmo.getHeightofObject();
		
			taskname = "Grössenmesser";
			logMessage = qrCode + ": " + height;
			
			Intent Logger = Log.log(this, taskname, logMessage);
			startActivity(Logger);
		}
		catch(Exception ex)
		{
			application.showErrors(this, "The Logging didn't work for some reason, please contact the administrator.");
		}
	}
	
	public void calculate()
	{
		try 
		{
			double a, alpha, beta;
			
			a = Float.parseFloat(txtA.getText().toString());
			alpha = Float.parseFloat(txtAlpha.getText().toString());
			beta = Float.parseFloat(txtBeta.getText().toString());
			
			
		    hmo = new HeightMeterObject(a, beta, alpha);

			if(hmo.isCalculatingPossible())
			{
				txtB.setText(String.valueOf(hmo.getHeightofObject()));
			}
			else
			{
				txtB.setText("0");
			}
		}
		catch(Exception ex)
		{
			application.showErrors(this, "The calculation didn't work for some reason. Make sure that you have filled in alpha, beta and a.");
		}
	}
	
	public void resetGUI()
	{
		txtA.setText("0");
		txtB.setText("0");
		txtAlpha.setText("0");
		txtBeta.setText("0");
	}

    /* Textevents */	
	
	@Override
	public void afterTextChanged(Editable sender) {		
		this.calculate();
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
}
