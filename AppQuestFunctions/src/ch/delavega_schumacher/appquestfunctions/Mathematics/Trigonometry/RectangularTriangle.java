package ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry;

public class RectangularTriangle extends Triangle {
	
	/* Konstruktor/en */
	public RectangularTriangle(double side_A, double side_B, double side_C, double angle_Alpha, double angle_Beta)
	{
		super(0, side_A, side_B, side_C, angle_Alpha, angle_Beta, 90); // caused by the fact that it's always rectangular
	}
	
	/* Getters (und Setters) */
	
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
	
	public double checkValue(double side_A, TriangleSides triangleSide, boolean calculateAngle)
	{
		if(side_A == 0)
		{
			return calculate(triangleSide, calculateAngle);
		}
		
		return side_A;
	}
	
	/* Eigenschaften */
	
	public boolean isRectangular() {
		return true;
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
	
	/* Berechnungen */
	
	public double calculate(TriangleSides triangleSide, boolean calculateAngle)
	{
		if(calculateAngle)
		{
			return calculateSpecificAngle(triangleSide).getValue();
		}
		else
		{
			return calculateSpecificSide(triangleSide);
		}
	}
	
	public Angle calculateSpecificAngle(TriangleSides triangleSide)
	{
		if(this.hasTwoAngles())
		{
			return new Angle(totalAmountOfDegrees - this.angle_Alpha.getValue() - this.angle_Beta.getValue() - this.angle_Gamma.getValue(), 0);
		}
		
		double sideTwo = 0;
		SideAnglePairing CalculationPairing = new SideAnglePairing(this.angle_Gamma, this.side_C);
		
		switch(triangleSide)
		{
		case A:
			sideTwo = this.side_A;
			break;
		default:
			sideTwo = this.side_B;
			break;
		}
		
		return calculateSpecificAngle(CalculationPairing, sideTwo);
	}
	
	public Angle calculateSpecificAngle(SideAnglePairing CalculationPairing, double sideTwo)
	{
		Angle calculationAngle = new Angle(0, sideTwo * CalculationPairing.getAngle().getSinus() / CalculationPairing.getSide());
		// Wert der zurückgegeben werden muss
		
		 return new Angle(calculationAngle.getArcSinus(),0);
	}
	
	public double calculateSpecificSide(TriangleSides triangleSide)
	{
		SideAnglePairing CalculationPairing = new SideAnglePairing(this.angle_Alpha, this.side_A);
		
		while(!CalculationPairing.isValid())
		{
			if(CalculationPairing.getAngle() == this.angle_Alpha)
			{
				CalculationPairing = new SideAnglePairing(this.angle_Beta, this.side_B);
			}
			else
			{
				CalculationPairing = new SideAnglePairing(this.angle_Gamma, this.side_C);
			}
		}
		
		Angle AngleTwo = null;
		
		switch(triangleSide)
		{
		case A:
			AngleTwo = getAngle_Alpha();
			break;
		case B:
			AngleTwo = getAngle_Beta();
			break;
		case C:
			AngleTwo = this.angle_Gamma;
			break;
		}
		return calculateSpecificSide(CalculationPairing, AngleTwo);
	}
	
	public double calculateSpecificSide(SideAnglePairing CalculationPairing, Angle AngleTwo)
	{
		return CalculationPairing.getSide() / CalculationPairing.getAngle().getSinus() * AngleTwo.getSinus();
	}
	
}
