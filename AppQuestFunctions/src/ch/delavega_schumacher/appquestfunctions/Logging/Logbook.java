package ch.delavega_schumacher.appquestfunctions.Logging;

import ch.delavega_schumacher.appquestfunctions.android.Application;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

// TODO: auslagern nach appquestfunctions.logging
public class Logbook extends Activity{
	
	private Application application = Application.getInstance();
	private static Logbook instance = null;
	
	protected Logbook()
	{
		
	}
	
	public static Logbook getInstance()
	{
		if(instance == null) {
	         instance = new Logbook();
	      }
	      return instance;
	}	
	
	public Intent log(Context context, String taskname, String logMessage) // String logMessage)
	{
    	if(application.isAppInstalled(context, "ch.appquest.intent.LOG"))
    	{
    		application.showErrors(this, "Logbook App not installed");    		
    		return null;
    	}
    
    	Intent intent = new Intent("ch.appquest.intent.LOG");
    	
    	intent.putExtra("ch.appquest.taskname", taskname);
    	
    	intent.putExtra("ch.appquest.logmessage", logMessage);
    	
    	return intent;
	}
	
	public Intent scan(Context context)
	{
		if(application.isAppInstalled(context, "com.google.zxing.client.android.SCAN"))
    	{
    		application.showErrors(this, "Zxing Barcode Scanner App not installed");    		
    		return null;
    	}
		
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		
		return intent;
	}

}
