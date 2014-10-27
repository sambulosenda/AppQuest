package ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry;

public class Angle {
	
	private double value = 0;
	
	/* Konstruktor */
	Angle(double angle_Beta, double d)
	{
		if(angle_Beta == 0)
		{
			this.value = d;
		}
		else
		{
			this.value = angle_Beta;
		}
		
	}
	
	public float getSinus()
	{
		return (float)Math.sin(Math.toRadians(this.value));
	}
	
	public double getCosinus()
	{
		return (float)Math.cos(Math.toRadians(this.value));
	}
	
	public double getArcSinus()
	{
		return Math.toDegrees(Math.asin(Math.toRadians(this.value)));
	}
	
	public double getArcCosinus()
	{
		return Math.toDegrees(Math.acos(Math.toRadians(this.value)));
	}
	
	public double getValue()
	{
		return (float) this.value;
	}
	
	public boolean isValid()
	{
		return this.getValue() > 0;
	}
}
