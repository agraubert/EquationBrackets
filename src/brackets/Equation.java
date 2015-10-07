package brackets;

public class Equation {
	private String name;
	private String eq;
	public Equation(String name, String eq)
	{
		this.name=name;
		this.eq=eq;
	}
	public String toString()
	{
		return this.eq;
	}
	public String getEQ()
	{
		return this.name;
	}

}
