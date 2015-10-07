package brackets;

public class NumericAssignmentSystem {
	private int last=-1;

	public NumericAssignmentSystem() {
		// TODO Auto-generated constructor stub
	}
	
	public int next()
	{
		this.last+=1;
		return this.last;
	}

}
