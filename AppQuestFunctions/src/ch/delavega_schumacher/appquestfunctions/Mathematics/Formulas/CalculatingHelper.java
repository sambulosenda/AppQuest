package ch.delavega_schumacher.appquestfunctions.Mathematics.Formulas;

public class CalculatingHelper {

private static CalculatingHelper instance = null;
	
	protected CalculatingHelper()
	{
		
	}
	
	public static CalculatingHelper getInstance()
	{
		if(instance == null) {
	         instance = new CalculatingHelper();
	      }
	      return instance;
	}
	
	public double getMagnetField(float[] sensorData)
	{
		double result = Math.sqrt(sensorData[0] * sensorData[0] 
				+ 	sensorData[1] * sensorData[1]
				+	sensorData[2] * sensorData[2]);
		
		return result;
	}
	
}
