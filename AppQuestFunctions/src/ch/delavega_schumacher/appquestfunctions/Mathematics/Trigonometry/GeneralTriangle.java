package ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry;

public class GeneralTriangle extends Triangle {
	
	protected RectangularTriangle BottomTriangle, TopTriangle;
	
	/* Konstruktoren */
	
	public GeneralTriangle(double height, double side_A, double side_B, double d, double alpha, double beta, double gamma)
	{
		super(height, side_A, side_B, d, alpha, beta, gamma);
		
		BottomTriangle = new RectangularTriangle(this.getHeight(), 0, this.side_C, this.angle_Alpha.getValue(), 0);
		TopTriangle = new RectangularTriangle(this.getHeight(), 0, this.side_A, this.angle_Gamma.getValue(), 0);
	}
	
	/* Getter und Setter */
	
	public void setSide_A(float side_A)
	{
		this.side_A = side_A;
	}
	
	public void setSideB(float side_B)
	{
		this.side_B = side_B;
	}
	
	public void setSide_C(double d)
	{
		this.side_C = d;
	}
	
	public void setAngle_Alpha(double newAngle_Alpha)
	{
		this.angle_Alpha = new Angle(newAngle_Alpha, 0);
	}
	
	public void setAngle_Beta(double newAngle_Beta)
	{
		this.angle_Beta = new Angle(newAngle_Beta, 0);
	}
	
	public void setAngle_Gamma(double newAngle_Gamma)
	{
		this.angle_Gamma = new Angle(newAngle_Gamma, 0);
	}
	
	public double getSide_A() {
		return checkValue(side_A, TriangleSides.A, false);
	}

	public double getSide_B() {
		return checkValue(side_B, TriangleSides.B, false);
	}

	public double getSide_C() {
		return checkValue(side_C, TriangleSides.C, false);
	}

	public Angle getAngle_Alpha() {
		return new Angle(checkValue(angle_Alpha.getValue(), TriangleSides.A, true), 0);
	}

	public Angle getAngle_Beta() {
		return new Angle(checkValue(angle_Beta.getValue(), TriangleSides.B, true), 0);
	}
	
	public Angle getAngle_Gamma() {
		return new Angle(checkValue(angle_Gamma.getValue(), TriangleSides.C, true), 0);
	}
	
	private double checkValue(double d, TriangleSides triangleSide, boolean calculateAngle)
	{
		if(d == 0)
		{
			switch(triangleSide)
			{
			case A:
				return this.getTopTriangle().checkValue(d, TriangleSides.C, calculateAngle);
			case B:
				return this.getBottomTriangle().checkValue(d, TriangleSides.B, calculateAngle) + this.getTopTriangle().checkValue(d, TriangleSides.B, calculateAngle);
			case C:
				return this.getBottomTriangle().checkValue(d, TriangleSides.C, calculateAngle);
			}
			return 0;
		}	
		return d;
	}
	
	/* berechnete Getter */
	
	public double getHeight()
	{	
		if(this.height != 0)
		{
			return this.height;
		}
		
		// wenn nicht eingetragen, dann berechnen
		SideAnglePairing CalculationPairing = new SideAnglePairing(this.angle_Gamma, this.side_A);
	
		if(!CalculationPairing.isValid())
		{
			CalculationPairing = new SideAnglePairing(this.angle_Alpha, this.side_C);
		}
		
		if(CalculationPairing.isValid())
		{
			RectangularTriangle tempRTriangleOne = new RectangularTriangle(0, 0, CalculationPairing.getSide(), CalculationPairing.getAngle().getValue(), 0);
			
			return tempRTriangleOne.getSide_A();
		}
		
		// Wenn Berechnung nicht möglich, dann Rückgabe eines leeren Wertes
		
		return 0;
	}
	
	private RectangularTriangle getTopTriangle()
	{
		return TopTriangle;
	}
	
	private RectangularTriangle getBottomTriangle()
	{
		return BottomTriangle;
	}
	
	/* Eigenschaften */
	
	public boolean isRectangular()
	{
		if(this.angle_Gamma.getValue() == 90 || this.angle_Beta.getValue() == 90 || this.angle_Alpha.getValue() == 90)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean hasTwoAngles()
	{
		int amountOfAngles = 0;
		
		if(this.angle_Alpha.getValue() > 0)
		{
			amountOfAngles++;
		}
		
		if(this.angle_Beta.getValue() > 0)
		{
			amountOfAngles++;
		}
		
		if(this.angle_Gamma.getValue() > 0)
		{
			amountOfAngles++;
		}
		
		return amountOfAngles > 1;
	}
	
}
