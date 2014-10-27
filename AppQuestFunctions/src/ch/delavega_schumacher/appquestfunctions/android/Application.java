package ch.delavega_schumacher.appquestfunctions.android;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Application {

	private static Application instance = null;
	
	protected Application()
	{
		
	}
	
	public static Application getInstance()
	{
		if(instance == null) {
	         instance = new Application();
	      }
	      return instance;
	}
	
	public boolean isAppInstalled(Context context, String packageName)
	{
		Intent mIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		
		if(mIntent == null)
    	{
			return false;   		
    	}
		
		return false;
	}
	
	public boolean doesFileExist(String filepath, String filename)
	{
		File generatedFile = new File(filepath, filename);
		
		return generatedFile.exists();
	}
	
	public void showErrors(Context context, String errorText)
	{
		Toast.makeText(context, errorText, Toast.LENGTH_LONG).show(); 
	}
	
}
