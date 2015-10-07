package SupportPackage;

public class piece {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		piece other = (piece) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	public String val;
	public int loc;
	
	public piece()
	{
		val="";
		loc=0;
	}
	public piece(piece parent)
	{
		val=new String(parent.val);
		loc=parent.loc;
	}
}
