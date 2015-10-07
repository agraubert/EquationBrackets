package brackets;

public class NodeList {
	private BracketNode head;
	public NodeList() {
	}
	
	public void add(BracketNode next)
	{
		if(this.head==null) this.head=next;
		else
		{
			this.head.add(next);
		}
	}
	
	public BracketNode fetch(int num)
	{
		return this.head.fetch(num);
	}
	
	public static NodeList createList(String[] source)
	{
		NodeList out = new NodeList();
		for(int i = 0; i<source.length; i++)
		{
			out.add(new BracketNode(source[0], source[1], source[2], Integer.parseInt(source[3]), Integer.parseInt(source[4])));
		}
		return out;
	}

}
