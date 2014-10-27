package ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry;

public abstract class Triangle {

	protected final float totalAmountOfDegrees = 180;
	
	protected double height;
	protected double side_A;
	protected double side_B;
	protected double side_C; 
	protected Angle angle_Alpha;
	protected Angle angle_Beta;
	protected Angle angle_Gamma;
	
	/* Konstruktoren */
	
	public Triangle(double height, double side_A, double side_B, double side_C, double angle_Alpha, double angle_Beta, double angle_Gamma)
	{
		this.height = height;
		this.side_A = side_A;
		this.side_B = side_B;
		this.side_C = side_C;
		
		this.angle_Alpha = new Angle(angle_Alpha, 0);
		this.angle_Beta = new Angle(angle_Beta, 0);
		this.angle_Gamma = new Angle(angle_Gamma, 0);
	}

	/* Getter und Setter */
	
	public double getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public double getSide_A() {
		return side_A;
	}

	public void setSide_A(double newDistance_A) {
		this.side_A = newDistance_A;
	}

	public double getSide_B() {
		return side_B;
	}

	public void setSide_B(float side_B) {
		this.side_B = side_B;
	}

	public double getSide_C() {
		return side_C;
	}

	public void setSide_C(float side_C) {
		this.side_C = side_C;
	}

	public Angle getAngle_Alpha() {
		return angle_Alpha;
	}

	public void setAngle_Alpha(double newAngle_Alpha) {
		this.angle_Alpha = new Angle(newAngle_Alpha, 0);
	}

	public Angle getAngle_Beta() {
		return angle_Beta;
	}

	public void setAngle_Beta(float angle_Beta) {
		this.angle_Beta = new Angle(angle_Beta, 0);
	}

	public Angle getAngle_Gamma() {
		return angle_Gamma;
	}

	public void setAngle_Gamma(float angle_Gamma) {
		this.angle_Gamma = new Angle(angle_Gamma, 0);
	}
	
	/* Eigenschaften */
	
	public abstract boolean isRectangular();
	
	protected abstract boolean hasTwoAngles();
	
	
	
}
