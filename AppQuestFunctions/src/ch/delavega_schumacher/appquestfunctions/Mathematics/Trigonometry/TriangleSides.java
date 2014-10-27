package ch.delavega_schumacher.appquestfunctions.Mathematics.Trigonometry;

public enum TriangleSides {
	A(0),
	B(1),
	C(2);
	
	private int Index;
	
	TriangleSides(int Index){this.Index = Index;}

	public int getIndex(){return this.Index;}
}
