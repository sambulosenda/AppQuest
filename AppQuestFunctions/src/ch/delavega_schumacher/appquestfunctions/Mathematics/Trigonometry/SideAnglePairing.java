package ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry;

public class SideAnglePairing {
	
	private Angle angle;
	private double side;
	
	SideAnglePairing(Angle angle, double side_B)
	{
		this.angle = angle;
		this.side = side_B;
	}
	
	public boolean isValid()
	{
		return angle.isValid() && side > 0;
	}
	
	public Angle getAngle()
	{
		return this.angle;
	}
	
	public double getSide()
	{
		return this.side;
	}
	
	
}
