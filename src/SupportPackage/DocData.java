package SupportPackage;

import java.io.*;
import java.util.Arrays;

public class DocData {

	public piece[] fingerprints;
	public int[] hashprints;
	public final int docsize;
	private String normalized;
	public static String normalize(String bad)
	{
		String lower = bad.toLowerCase();
		lower=lower.replaceAll(" ", "");
		lower=lower.replaceAll("\n", "");
		return lower;
	}
	public static String[] namezipper(piece[] pieces)
	{
		String [] names = new String[pieces.length];
		for(int i=0; i<pieces.length; i++) names[i]=new String(pieces[i].val);
		return names;
	}
	public static int[] loczipper(piece[] pieces)
	{
		int [] locs =new int[pieces.length];
		for(int i=0; i<pieces.length; i++) locs[i]=pieces[i].loc;
		return locs;
	}
	public static int[] hashzipper(piece[] pieces)
	{
		int[] hashes = new int[pieces.length];
		for(int i=0; i<hashes.length; i++) hashes[i]= new Integer(pieces[i].hashCode());
		return hashes;
	}
	public static piece[] unduplicate(piece[] first)
	{
		piece[] intermediate = new piece [first.length];
		int []namezip = hashzipper(first);
		int rems=0;
		intermediate[first.length-1]= new piece();
		intermediate[first.length-1]=first[first.length-1];
		for(int i=0; i<first.length-1; i++)
		{
			boolean unique=Arrays.asList(Arrays.copyOfRange(namezip, i+1, first.length)).contains(first[i].hashCode());
			unique=!unique;
			if(unique) intermediate[i]=new piece(first[i]);
			else
			{
				intermediate[i]=new piece();
				rems++;
			}
		}
		int reindex=0;
		piece[] result = new piece[first.length-rems];
		for(int i=0; i<result.length; i++)
		{
			//System.out.println(i);
			while(intermediate[i+reindex].val=="") reindex++;
			result[i]=new piece(intermediate[i+reindex]);
			//System.out.println(result[i].val);

		}
		//System.out.println(result.length);
		return result;

	}


	public DocData(String path, int ksize, int windowsize) throws IOException
	{
		BufferedReader in =null;
		String raw="";
		try
		{
			in=new BufferedReader(new FileReader(path));
			String line="";
			line=in.readLine();
			while(line!=null)
			{
				raw+="\n"+line;
				line=in.readLine();
			}
		}
		finally
		{
			in.close();
		}
		if(windowsize==0) windowsize= (int) (0.5*ksize)+1;
		this.normalized=normalize(raw);
		this.docsize = this.normalized.length();
		String [] grams = new String[this.normalized.length() - ksize];
		for(int i =0; i<grams.length; i++)
		{
			char [] gram  = new char [ksize];
			for(int j=0; j<ksize; j++) gram[j]=this.normalized.charAt(i+j);
			grams[i]=new String(gram);
		}
		piece [] temp = new piece[grams.length - windowsize];
		int shortener=0;
		for(int i=0; i<temp.length; i++)
		{
			temp[i]= new piece();
			int myindex=((int) (Math.random()*windowsize))+i;
			//System.out.println(""+myindex+"  :  "+grams[myindex]);
			if(grams[myindex]=="") 
			{
				shortener++;
				continue;
			}
			temp[i].val=grams[myindex];
			grams[myindex]="";
			temp[i].loc=myindex;
		}
		this.fingerprints = new piece[temp.length-shortener];
		int reindex=0;
		for(int i=0; i<this.fingerprints.length; i++)
		{
			while(temp[i+reindex].val=="") reindex++;
			this.fingerprints[i]=new piece(temp[i+reindex]);
		}
		this.fingerprints = unduplicate(this.fingerprints);
		this.hashprints = hashzipper(this.fingerprints);
		Arrays.sort(this.hashprints);
		//for(int i=0; i<this.fingerprints.length; i++) this.hashprints[i]=this.fingerprints[i].hashCode();
		System.out.println("Created "+this.fingerprints.length+" unique ingerprints.");
	}
	public DocData(DocData parent)
	{
		this.fingerprints=new piece[parent.fingerprints.length];
		for(int i=0; i<parent.fingerprints.length; i++)
		{
			this.fingerprints[i]=new piece(parent.fingerprints[i]);
		}
		this.hashprints=hashzipper(parent.fingerprints);
		Arrays.sort(this.hashprints);
		this.docsize=parent.docsize;
		this.normalized=new String(parent.normalized);
	}

}
