package ch.delavega_schumacher.hoehenmesser;

import ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry.*;

public class HeightMeterObject {

	protected double distance, alpha, gamma, beta; // just for validating not for calculations => isCalculationPossible()
	
	protected GeneralTriangle MainTriangle;
	protected RectangularTriangle HelperTriangle;
	
	/* Konstruktor/en */
	/**
	 * Konstruktor konstruiert das Dreieck unter unserem "Höhen"dreieck und das "Höhen"dreieck selbst
	 * @param distance_A
	 * @param angle_Alpha gemessener Winkel 2
	 * @param angle_Gamma gemessener Winkel 1
	 */
	public HeightMeterObject(double distance, double beta, double alpha)
	{
		this.distance = distance;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = 180 - alpha - beta;
		
		HelperTriangle = new RectangularTriangle(distance, 0, 0, alpha, 0);
		
		if(isCalculatingPossible())
		{
			MainTriangle = new GeneralTriangle(0, 0, 0, HelperTriangle.getSide_C(), alpha, beta, gamma);
		}
		else
		{
			MainTriangle = new GeneralTriangle(0, 0, 0, 0, alpha, beta, gamma);
		}
		
	}
	
	/* Getter und Setter */
	
	public void setDistance(double newDistance_A)
	{
		this.distance = newDistance_A;
		HelperTriangle.setSide_A(newDistance_A);
		MainTriangle.setSide_C(HelperTriangle.getSide_C());
	}
	
	public void setAlpha(double newAngle_Alpha)
	{
		this.alpha = newAngle_Alpha;
		
		HelperTriangle.setAngle_Alpha(newAngle_Alpha);
		this.setGamma(180 - newAngle_Alpha - this.getBeta());
		MainTriangle.setAngle_Alpha(newAngle_Alpha);
		MainTriangle.setSide_C(HelperTriangle.getSide_C());
	}
	
	public void setGamma(double newAngle_Gamma)
	{
		this.gamma = newAngle_Gamma;
		MainTriangle.setAngle_Gamma(newAngle_Gamma);
	}
	
	public void setBeta(double newAngle_Beta)
	{
		this.beta = newAngle_Beta;
		this.setGamma(180 - newAngle_Beta - this.getAlpha());
		
		MainTriangle.setAngle_Beta(newAngle_Beta);
	}
	
	public double getDistance()
	{
		return this.HelperTriangle.getSide_A();
	}
	
	public double getAlpha()
	{
		return this.MainTriangle.getAngle_Alpha().getValue();
	}
	
	public double getGamma()
	{
		return this.MainTriangle.getAngle_Gamma().getValue();
	}
	
	public double getBeta()
	{
		return this.MainTriangle.getAngle_Beta().getValue();
	}
	
	public double getHeightofObject()
	{
		return this.MainTriangle.getSide_B();	
	}
	
	/* Eigenschafffffften */
	
	public boolean isCalculatingPossible()
	{
		if(this.alpha != 0 && this.beta != 0 && this.distance != 0)
		{
			if(this.alpha + this.gamma + this.beta > 180 == false)
			{
				return true;	
			}
		}
		
		return false;
	}
	
}
