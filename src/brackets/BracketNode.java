package brackets;

public class BracketNode {
	private String left, right, victor;
	private int id;
	private int status;
	protected BracketNode nodeLeft, nodeRight, nodeVic, next;
	protected Bracket bLeft, bRight, bVic;

	public BracketNode(String l, String r, String v, int status, int id)
	{
		this.left=l;
		this.right=r;
		this.victor=v;
		this.id=id;
		this.status=status;
	}

	public Bracket toBracket()
	{
		if(this.nodeLeft==null && this.nodeRight==null)
		{
			Bracket output= new Bracket(left, right);
			output.setVictor(this.victor);
			output.setStatus(this.status);
			return output;
		}
		if(this.nodeLeft!=null && this.bLeft==null)
		{
			this.bLeft=this.nodeLeft.toBracket();
		}
		if(this.nodeRight!=null && this.bRight==null)
		{
			this.bRight=this.nodeRight.toBracket();
		}
		if(this.nodeVic!=null && this.bVic==null)
		{
			this.bVic = this.nodeVic.toBracket();
		}
		Bracket output= new Bracket(this.bLeft, this.bRight);
		//output.setVictor(this.victor);
		output.setVictor(this.findVictor());
		output.setStatus(this.status);
		return output;
	}

	public void link(NodeList source)
	{
		int target;
		try
		{
			if(this.nodeLeft!=null)
			{
				throw new NumberFormatException();
			}
			target=Integer.parseInt(this.left);
			//System.out.println(target);
			this.nodeLeft=source.fetch(target);
			this.nodeLeft.link(source);
		}
		catch(NumberFormatException e)
		{
			
		}
		try
		{
			if(this.nodeRight!=null)
			{
				throw new NumberFormatException();
			}
			target=Integer.parseInt(this.right);
			//System.out.println(target);
			this.nodeRight=source.fetch(target);
			this.nodeRight.link(source);
		}
		catch(NumberFormatException e)
		{
			
		}
		try
		{
			if(this.nodeVic!=null)
			{
				throw new NumberFormatException();
			}
			target=Integer.parseInt(this.victor);
			//System.out.println(target);
			this.nodeVic=source.fetch(target);
			this.nodeVic.link(source);
		}
		catch(NumberFormatException e)
		{
			
		}
	}
	
	public BracketNode fetch(int num)
	{
		if(this.id==num) return this;
		//System.out.println(""+num+"!="+this.id+"\n");
		if(this.next==null)
		{
			//System.out.println("NOT FOUND");
			return null;
		}
		return this.next.fetch(num);
	}
	
	public void setNext(BracketNode obj)
	{
		this.next=obj;
	}
	
	public BracketNode getNext()
	{
		return this.next;
	}
	
	public int getNum()
	{
		return this.id;
	}
	
	public void add(BracketNode thing)
	{
		if(this.next==null) this.next=thing;
		else this.next.add(thing);
	}
	
	public String findVictor()
	{
		if(this.nodeVic==null) return this.victor;
		return this.nodeVic.findVictor();
	}
}
