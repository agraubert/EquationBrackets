package brackets;
import SupportPackage.ArgsProcessor;
public class SocketTesting {

	public static void main(String[] args)
	{
		Tunnel sock=new Tunnel("localhost", 4243);
		sock.connect();
		ArgsProcessor ap = new ArgsProcessor(args);
		boolean done=false;
		while(!done)
		{
			String msg=ap.nextString("Next");
			sock.send(msg);
			System.out.println(sock.rec());
			if(msg.equals("9Z9"))
			{
				sock.disconnect();
				done=true;
			}
		}
	}

}
