package brackets;
import java.io.*;
import java.net.*;

import javax.swing.*;
public class Tunnel {
	private boolean connected=false;
	private String address;
	private int port;
	private PrintWriter output;
	private BufferedReader input;
	private Socket sock;
	public Tunnel(String address, int port) {
		this.address=address;
		this.port=port;
	}
	public void connect()
	{
		if(this.connected)
		{
			//if(this.marco()) return;
			this.disconnect();
		}
		this.connected=true;
		try
		{
			this.sock=new Socket();
			this.sock.connect(new InetSocketAddress(this.address, this.port), 5000);
			this.output=new PrintWriter(this.sock.getOutputStream());
			this.input=new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
			//this.sock.setSoTimeout(7500);

		}
		catch(UnknownHostException a)
		{
			JOptionPane.showMessageDialog(null, "Could not contact Server.\n"
					+ "Please try again later.", "Connection Error", JOptionPane.ERROR_MESSAGE);
			a.printStackTrace();
		}
		catch(IOException a)
		{
			JOptionPane.showMessageDialog(null, "Could not contact Server.\n"
					+ "Please try again later.", "Connection Error", JOptionPane.ERROR_MESSAGE);
			a.printStackTrace();
		}
	}
	public void send(String msg)
	{
		if(!this.connected) return;
		this.output.println(msg+"\\");
		this.output.flush();
	}
	public String rec()
	{
		
		if(!this.connected) return null;
		try {
			String out= "";
			while(!(out.contains("\\")||out.contains("\\\\")))
			{
				out+=this.input.readLine()+"\n";
			}
			//System.out.println(out);
			return out.trim().split("\\\\")[0];
		} 
		catch (SocketTimeoutException a)
		{
			a.printStackTrace();
			this.disconnect(false);
			return null;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			this.disconnect(false);
			return null;
		}
	}

	public void disconnect()
	{
		this.disconnect(true);
	}
	public void disconnect(boolean tell)
	{
		if(!this.connected) return;
		if(tell) this.send("9Z9");
		try {
			this.input.close();
			this.output.close();
			this.sock.close();
			this.connected=false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean marco()
	{
		if(!this.connected)return false;
		this.send("MARCO");
		String recieved = this.rec();
		if(!(recieved!=null && recieved.equals("POLO")))
		{
			this.disconnect(false);
			return false;
		}
		return true;
	}

}
