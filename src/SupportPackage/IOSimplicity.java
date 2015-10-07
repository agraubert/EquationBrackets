package SupportPackage;
import java.io.*;
public class IOSimplicity {
	private char mode;
	private PrintWriter writer;
	private BufferedReader reader;
	private String currentFile="";
	private String outBuffer="";
	private String inBuffer="";

	/**
	 * Creates a fresh IOSimplicity object
	 */
	public IOSimplicity() {
		this.mode='N';
		this.writer=null;
		this.reader=null;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return true if the IOSimplicity object was successfully set to read mode
	 */
	public boolean readMode(String filePath)
	{
		if(this.mode=='W') this.writer.close();
		if(this.mode=='R') return (this.currentFile.equals(filePath));
		try
		{
			this.reader = new BufferedReader(new FileReader(filePath));
			this.mode='R';
			this.currentFile=filePath;
		}
		catch(FileNotFoundException bad)
		{
			this.mode='N';
			
			return false;
		}
		return true;
	}
	
	
	/**
	 * 
	 * @return A string containing the entire contents of the file opened in read mode
	 */
	public String read()
	{
		if(this.mode!='R') return "";
		String output="";
		try
		{
			String line="";
			line=this.reader.readLine();
			while(line!=null)
			{
				output+="\n"+line;
				line=this.reader.readLine();
			}
			this.reader.close();
			this.mode='N';
		}
		catch(IOException bad)
		{
			bad.printStackTrace();
		}
		return output;
	}
	
	/**
	 * 
	 * @return The next line in the file specified in read mode
	 */
	public String readLine()
	{
		if(this.mode!='R') return "";
		try
		{
			String line = this.reader.readLine();
			return line;
		}
		catch(IOException bad)
		{
			bad.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Clears the buffers, saves output files, and resets the object to Null mode
	 */
	public void reset()
	{
		switch(this.mode)
		{
		case 'R':
			try
			{
				this.reader.close();
			}
			catch(IOException bad)
			{
				
				bad.printStackTrace();
			}
			break;
		case 'W':
			this.writer.close();
			break;
		}
		this.mode='N';
		this.currentFile="";
		this.outBuffer="";
		this.inBuffer="";
	}
	/**
	 * 
	 * @param text
	 * @return true if text was written to the file
	 * <p> note that no text is saved to the file until the .reset() method is used
	 */
	public boolean write(String text)
	{
		if(this.mode!='W') return false;
		this.writer.write(text);
		return true;
	}
	
	/**
	 * Appends text to the output buffer
	 * @param text
	 */
	public void bufferedWrite(String text)
	{
		if(this.mode!='W') return;
		this.outBuffer+=text;
	}
	
	/**
	 * 
	 * @return true if the object's output buffer was written to the file
	 * <p> note that no text is saved to the file until the .reset()  method is used
	 */
	public boolean writeBuffer()
	{
		if(this.mode!='W') return false;
		this.writer.write(this.outBuffer);
		return true;
		
	}
	
	/**
	 * 
	 * @return the contents of the output buffer
	 */
	public String getOut()
	{
		return this.outBuffer;
	}
	
	

	
	
	/**
	 * Empties the output buffer without writing to a file
	 */
	public void clearOut()
	{
		this.outBuffer="";
	}
	
	/**
	 * 
	 * @param filePath
	 * @return true if the IOSimplicity object opened the specified file in write mode
	 * <p> Note that if the file already exists, its current contents are saved to the input buffer
	 */
	public boolean writeMode(String filePath)
	{
		if(this.mode=='R')
		{
			try
			{
				this.reader.close();
			}
			catch(IOException bad)
			{
				bad.printStackTrace();
			}
		}
		if(this.mode=='W') return (this.currentFile.equals(filePath));
		try
		{
			if(this.readMode(filePath))
			{
				this.inBuffer=this.read();
			}
			this.writer= new PrintWriter(new FileWriter(filePath));
			this.mode='W';
			this.currentFile=filePath;
		}
		catch(IOException bad)
		{
			
			return false;
		}
		return true;
	}
	
	/**
	 * Moves the previous contents of the current file from the input buffer to the output buffer
	 * The output buffer must still be written to the file using writeBuffer() to effectively save the contents
	 */
	public void saveOldContents()
	{
		if(this.mode!='W') return;
		this.outBuffer=new String(this.inBuffer);
		this.inBuffer="";
	}
	
	

}
