package brackets;
import java.awt.Color;

import javax.swing.*;
import javax.swing.text.*;
public class Bracket {
	private Bracket left;
	private Bracket right;
	private boolean raw=false;
	private String Sleft;
	private String Sright;
	private boolean set=false;
	private String victor;
	private int status=0;
	//private boolean mapped=false;
	//private Bracket mappedTo=null;
	private int num, packLeft, packRight, packVic;

	public Bracket(Bracket left, Bracket right) {
		this.left=left;
		this.right=right;
	}
	public Bracket(String left, String right)
	{
		this.raw=true;
		this.Sleft=left;
		this.Sright=right;
	}
	public void setBracket() throws BadLocationException
	{
		if(set) return;
		if(!raw)
		{
			this.left.setBracket();
			this.right.setBracket();
			String[] choices={this.left.getVictor(), this.right.getVictor()};
			this.victor = (String) JOptionPane.showInputDialog(null,
					"", "Choose victor from this matchup",
					JOptionPane.QUESTION_MESSAGE, null,
					choices, choices[0]);
		}
		else
		{
			String[] choices={this.Sleft, this.Sright};
			this.victor = (String) JOptionPane.showInputDialog(null,
					"", "Choose victor from this matchup",
					JOptionPane.QUESTION_MESSAGE, null,
					choices, choices[0]);
			
		}
		if(this.victor==(String) null) throw new BadLocationException("", 0);
		this.set=true;
	}
	public String getVictor()
	{
		return this.victor;
	}
	public void setVictor(String victor)
	{
		this.victor=victor;
	}
	public void setStatus(int stats)
	{
		this.status=stats;
	}

	public void advancedDisplay(StyledDocument doc) throws BadLocationException
	{
		advancedDisplay(doc, 0);
	}

	public void advancedDisplay(StyledDocument doc, int tablevel) throws BadLocationException
	{
		String tabs="";
		for(int i=0; i<tablevel; i++) tabs+="\t\t";
		if(this.right!=null) this.right.advancedDisplay(doc, tablevel+1);
		else
		{
			SimpleAttributeSet attrsRight= new SimpleAttributeSet();
			StyleConstants.setFontSize(attrsRight, 8);
			StyleConstants.setForeground(attrsRight, Color.BLACK);
			StyleConstants.setBold(attrsRight, true);
			doc.insertString(doc.getLength(), tabs+"\t\t"+this.Sright, attrsRight);
		}
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 8);
		StyleConstants.setBold(attrs, true);
		switch(this.status)
		{
		case 0:
			StyleConstants.setForeground(attrs, Color.BLACK);
			break;
		case 1:
			StyleConstants.setForeground(attrs, new Color(0,128,0));
			break;
		case -1:
			StyleConstants.setForeground(attrs, Color.RED);
		}
		doc.insertString(doc.getLength(), "\n"+tabs+"("+this.getVictor()+")\n", attrs);
		if(this.left!=null) this.left.advancedDisplay(doc, tablevel+1);
		else
		{
			SimpleAttributeSet attrsLeft= new SimpleAttributeSet();
			StyleConstants.setFontSize(attrsLeft, 8);
			StyleConstants.setForeground(attrsLeft, Color.BLACK);
			StyleConstants.setBold(attrsLeft, true);
			doc.insertString(doc.getLength(), tabs+"\t\t"+this.Sleft, attrsLeft);
		}
	}

	public String makeString(int tablevel)
	{
		String output="";
		String tabs="";
		for(int i=0; i<tablevel; i++) tabs+="\t\t";
		if(this.right!=null) output+=this.right.makeString(tablevel+1);
		else output+=tabs+"\t\t"+this.Sright;
		output+="\n"+tabs+"(";
		output+=""+this.getVictor()+")";
		output+="\n";
		if(this.left!=null) output+=this.left.makeString(tablevel+1);
		else output+=tabs+"\t\t"+this.Sleft;
		return output;
	}
	public String toString()
	{
		return this.makeString(0);
	}

	public int getBaseSize()
	{
		if(this.raw)
		{
			return 2;
		}
		return (this.left.getBaseSize())+(this.right.getBaseSize());
	}




	public static Bracket setTourney(Equation[] source)
	{
		if(source.length%2!=0) return null;
		if(source.length==2)
		{
			return new Bracket(source[0].toString(), source[1].toString());
		}
		int first = (source.length)/2;
		Equation[] sourceLeft = new Equation[first];
		Equation[] sourceRight = new Equation[first];
		for(int i = 0; i<first; i++)
		{
			sourceLeft[i]=source[i];
			sourceRight[i]=source[i+first];
		}
		return new Bracket(Bracket.setTourney(sourceLeft), Bracket.setTourney(sourceRight));
	}
	public void setNum(int num)
	{
		this.num=num;
	}

	public static Bracket unpack(String source)
	{
		String[] lines=source.split("\n");
		NodeList nodes = new NodeList();
		for(int i =0; i<lines.length; i++)
		{
			String[] data = lines[i].split("::");
			nodes.add(new BracketNode(data[0], data[1], data[2], (Integer.parseInt(data[3])), Integer.parseInt(data[4])));
		}
		BracketNode ordered = nodes.fetch(0);
		ordered.link(nodes);
		return ordered.toBracket();
	}
	
	public void assign(NumericAssignmentSystem nums)
	{
		this.num=nums.next();
		if(this.left!=null) this.left.assign(nums);
		if(this.right!=null) this.right.assign(nums);
		if(!raw)
		{
			this.packLeft=this.left.getNum();
			this.packRight=this.right.getNum();
			this.packVic=(this.victor.equals(this.left.getVictor()))? this.left.getNum():this.right.getNum();
		}
		
	}
	
	public String selfPack()
	{
		if(raw)
		{
			return this.Sleft+"::"+this.Sright+"::"+this.victor+"::"+this.num+"\n";
		}
		return ""+this.packLeft+"::"+this.packRight+"::"+this.packVic+"::"+this.num+"\n";
	}
	
	public int getNum()
	{
		return this.num;
	}
	
	public String pack()
	{
		String out=this.selfPack();
		if(!raw)
		{
			out+=this.left.pack();
			out+=this.right.pack();
		}
		return out;
		
	}
}
