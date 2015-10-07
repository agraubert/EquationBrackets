package brackets;
//import SupportPackage.*;

import java.awt.event.*;

import javax.swing.text.*;

import java.awt.Color;
import java.awt.Point;

import javax.swing.*;

public class Main implements ActionListener{
	private boolean signedIn=false;
	public JFrame window;
	//private JFrame anchor;
	public Point anchor;
	private int session=0;
	private Tunnel tunnel= new Tunnel(Main.ADDRESS, Main.PORT);
	public static final String ADDRESS="24.171.122.135";
	//public static final String ADDRESS="localhost";
	//public static final String ADDRESS= "172.17.7.62";
	//public static final String ADDRESS = "7.34.245.187";
	public static final int PORT=4242;
	private JTextField[] textboxes;
	private JPasswordField[] passwords;
	@SuppressWarnings("rawtypes")
	private JComboBox box;
	public Bracket buffer;
	private boolean locked;
	private boolean skipAnchor=false;
	private String myName;
	public static final double ver=1.02;
	//private Thread wait;



	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main prime = new Main();
				prime.go();
			}
		});
	}

	public Main()
	{
		/*this.anchor = new JFrame("");
		this.anchor.setLocationRelativeTo(null);*/
	}

	public void go()
	{
		tunnel.connect();
		tunnel.send("LOCKED");
		String result = tunnel.rec();
		double ver = Double.parseDouble(tunnel.rec());
		boolean outdated = (ver>Main.ver);
		this.locked = result.equals("LOCKED");
		if(outdated)
		{
			String notes = "This version is out of date!\n"
					+ "Please visit www.jrootstudios.com/commissioned-applications/\n"
					+ "And download the latest Equation Brackets version";
			window = new JFrame("Brackets");
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
			window.getContentPane().setBackground(Color.WHITE);
			
			JTextPane info = new JTextPane();
			info.setEditable(false);
			StyledDocument doc = info.getStyledDocument();
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			StyleConstants.setFontSize(attrs, 12);
			StyleConstants.setForeground(attrs, Color.RED);
			try {
				doc.insertString(doc.getLength(), notes, attrs);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			window.add(info);
			
			JButton ok = new JButton("ok");
			ok.setActionCommand("STOP");
			ok.addActionListener(this);
			window.add(ok);
			
			window.pack();
			window.setLocationRelativeTo(null);
			window.setVisible(true);
			
		}
		else if(!locked)
		{
			String notes = 
					"Please note that the locking date is Tuesday April 8 at 9:30pm\n"
							+ "The server will be offline between then and the morning of April 9\n"
							+ "After that time no further submissions will be accepted by the server.\n"
							+ "Additionally, other people's brackets will become visible after the locking date.\n"
							+ "Finally, note that you may only submit one bracket. Submitting a second\n"
							+ "bracket will override the first one.";
			window = new JFrame("Brackets");
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
			window.getContentPane().setBackground(Color.WHITE);

			JTextPane info = new JTextPane();
			info.setEditable(false);
			StyledDocument doc = info.getStyledDocument();
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			StyleConstants.setFontSize(attrs, 12);
			StyleConstants.setForeground(attrs, Color.RED);
			try {
				doc.insertString(doc.getLength(), notes, attrs);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			window.add(info);

			JButton next = new JButton("Continue");
			next.setActionCommand("MAIN");
			next.addActionListener(this);
			window.add(next);

			window.pack();
			window.setLocationRelativeTo(null);
			window.setVisible(true);
			this.anchor=window.getLocationOnScreen();

		}
		else menu();
	}

	public void menu()
	{

		if(true)
		{
			if(!signedIn)
			{
				signin();
			}
			else
			{
				bracketMenu();

			}
		}
	}

	public void signin()
	{

		window = new JFrame("Brackets");
		window.getContentPane().setBackground(Color.WHITE);

		//window.setSize(512, 512);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		JTextPane text = new JTextPane();
		text.setEditable(false);
		StyledDocument doc = text.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs,  24);
		StyleConstants.setBold(attrs,  true);
		StyleConstants.setForeground(attrs,  Color.RED);
		try
		{
			doc.insertString(doc.getLength(), "    Sign-in Menu    \n", attrs);
		}
		catch(BadLocationException e)
		{
			e.printStackTrace();
		}
		window.add(text);
		//window.pack();


		JButton signinButton = new JButton("Sign In   ");
		signinButton.addActionListener(this);
		signinButton.setActionCommand("SigninSubMenu");
		window.add(signinButton);

		JButton registerButton = new JButton("Register");
		registerButton.setActionCommand("RegistrationSubMenu");
		registerButton.addActionListener(this);
		window.add(registerButton);

		window.add(this.tagline());

		window.pack();
		window.setVisible(true);
		if(anchor!=null)
		{
			window.setLocation(anchor);
		}
		else
		{
			window.setLocationRelativeTo(null);
		}

	}

	public void bracketMenu()
	{
		if(!skipAnchor) this.anchor=window.getLocationOnScreen();
		skipAnchor=false;
		window.dispose();
		window = new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane title = new JTextPane();
		title.setEditable(false);
		StyledDocument doc = title.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 24);
		StyleConstants.setBold(attrs, true);
		StyleConstants.setForeground(attrs, Color.RED);
		try {
			doc.insertString(doc.getLength(), "    Equation Brackets    \n", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		title.setEditable(false);
		SimpleAttributeSet nameset=new SimpleAttributeSet();
		StyleConstants.setFontSize(nameset, 12);
		try {
			doc.insertString(doc.getLength(), "\nWelcome, "+myName, nameset);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.add(title);

		JButton viewBrackets = new JButton("     View Brackets      ");
		viewBrackets.setActionCommand("viewBrackets");
		viewBrackets.addActionListener(this);
		if(!this.locked && !(myName.equals("@admin")))
		{
			viewBrackets.setToolTipText("View the most recent bracket you've submitted.  "
					+ "You'll be able to see everyone's brackets after the locking date");
		}
		else viewBrackets.setToolTipText("View all players' brackets");
		window.add(viewBrackets);

		JButton createBracket = new JButton("Submit Your Bracket");
		createBracket.setActionCommand("submitBracket");
		createBracket.addActionListener(this);
		createBracket.setEnabled(!this.locked);
		if(this.locked) createBracket.setToolTipText("You can't submit brackets after the locking date"); 
		if(myName.equals("@admin")) 
		{
			createBracket.setEnabled(false);
			createBracket.setToolTipText("As an admin, you cannot submit a bracket");
		}
		window.add(createBracket);

		JButton leaders = new JButton(" View Leaderboards ");
		leaders.setActionCommand("leaderboards");
		leaders.addActionListener(this);
		leaders.setEnabled(this.locked);
		if(!this.locked) leaders.setToolTipText("This will be enabled after the locking date");
		window.add(leaders);

		if(myName.equals("@admin"))
		{
			JButton tick = new JButton("        Tick Server         ");
			tick.setActionCommand("TICK");
			tick.addActionListener(this);
			tick.setEnabled(this.locked);
			if(!this.locked) tick.setToolTipText("You can't tick the server until after the locking date"); 
			window.add(tick);

			JButton shutdown = new JButton("          Shutdown          ");
			shutdown.setActionCommand("SHUTDOWN");
			shutdown.addActionListener(this);
			shutdown.setToolTipText("FOR EMERGENCIES ONLY");
			window.add(shutdown);
			
			JButton update = new JButton("     Update Version     ");
			update.setActionCommand("UPDATE");
			update.addActionListener(this);
			update.setToolTipText("Change the server version number");
			window.add(update);
		}

		JButton signout = new JButton("           Sign Out            ");
		signout.setActionCommand("signout");
		signout.addActionListener(this);
		window.add(signout);

		window.add(this.tagline());

		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);


	}

	public void submitBracket()
	{
		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window = new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane title = new JTextPane();
		title.setEditable(false);
		StyledDocument doc = title.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 20);
		try {
			doc.insertString(doc.getLength(), "    Building Bracket    \n", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.add(title);

		tunnel.connect();
		tunnel.send("KEY");
		String key = tunnel.rec();
		JTextPane text = new JTextPane();
		text.setEditable(false);
		StyledDocument doc2 = text.getStyledDocument();
		SimpleAttributeSet attrs2 = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 16);
		try {
			doc2.insertString(doc2.getLength(), "Please fill out the prompts to build your bracket.\nThe program will display the bracket afterwards.\n"
					+ "The key displayed below can also be found at\n"
					+ "www.jrootstudios.com/commissioned-applications  \n\n", attrs2);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleAttributeSet attrs4 = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs4,12);
		try {
			doc2.insertString(doc2.getLength(), key, attrs4);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		window.add(text);


		window.pack();
		//window.setLocation(anchor);
		window.setVisible(true);

		tunnel.send("SRC");
		String intake = tunnel.rec();
		//System.out.println(intake);
		String[] sourceString = intake.replaceAll("\n\n", "\n").split("\n");
		//System.out.println(sourceString[0]);
		Equation[] source = new Equation[sourceString.length];
		for(int i = 0; i<sourceString.length; i+=1)
		{
			/*System.out.println(sourceString[i]);
			if(!sourceString[i].contains("::"))
			{
				System.out.println("BAD--->"+sourceString[i]);
			}*/
			String[] stuff=sourceString[i].split("::");
			/*System.out.println(stuff.length);
			for(int j=0; j<stuff.length; j++) System.out.println("SOURCE-->"+stuff[i]+"\n");*/
			//System.out.println(stuff.length);
			//System.out.println(stuff[0]);
			String name = stuff[0];
			String eq = stuff[1];
			source[i]=new Equation(name, eq);
			//System.out.println("END");
		}
		Bracket head = Bracket.setTourney(source);
		try {
			head.setBracket();
		} catch (BadLocationException e1) {
			bracketMenu();
			return;
		}
		this.buffer= head;

		//display the new bracket
		window.dispose();
		window = new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane brak = new JTextPane();
		brak.setEditable(false);
		StyledDocument doc3 = brak.getStyledDocument();
		SimpleAttributeSet attrs3 = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs3, 8);
		StyleConstants.setBold(attrs3, true);
		try {
			doc3.insertString(doc3.getLength(), head.toString(), attrs3);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.add(brak);

		JButton confirm = new JButton("        Submit        ");
		confirm.setActionCommand("sendBracket");
		confirm.addActionListener(this);
		window.add(confirm);

		JButton deny = new JButton("Remake Braket");
		deny.setActionCommand("submitBracket");
		deny.addActionListener(this);
		window.add(deny);

		window.pack();
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setLocation(anchor);
		window.setVisible(true);
		this.skipAnchor=true;

	}

	public void viewBrackets()
	{
		tunnel.connect();
		tunnel.send("LIST::"+session);
		String[] names = tunnel.rec().split("\n");
		boolean bad = (names.length==1 && names[0].equals("@NO AVALIABLE USERS"));

		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window = new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane title = new JTextPane();
		title.setEditable(false);
		StyledDocument doc = title.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 20);
		try {
			doc.insertString(doc.getLength(), "  Choose a username  ", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bad)
		{
			SimpleAttributeSet badattrs = new SimpleAttributeSet();
			StyleConstants.setFontSize(badattrs, 12);
			StyleConstants.setForeground(badattrs, Color.RED);
			try {
				doc.insertString(doc.getLength(), "\n\nNo brackets are avaliable at this time\n"
						+"Until the locking date, the only avaliable bracket will be yours\n"
						+"You can submit your bracket from the main menu", badattrs);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		window.add(title);
		if(!bad)
		{
			if(!locked)
			{
				SimpleAttributeSet attrs2 = new SimpleAttributeSet();
				StyleConstants.setFontSize(attrs2, 12);
				try {
					doc.insertString(doc.getLength(), "\n\nOnly your bracket will be avaliable\nuntil after the locking date\n", attrs2);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@SuppressWarnings({ "rawtypes", "unchecked" })
			JComboBox choices= new JComboBox(names);
			this.box = choices;
			window.add(choices);

			JButton submit = new JButton("View");
			submit.setActionCommand("loadBracket");
			submit.addActionListener(this);
			window.add(submit);

		}
		JButton back = new JButton("Back");
		back.setActionCommand("bracketMenu");
		back.addActionListener(this);
		window.add(back);

		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);
	}

	public void leaderboards()
	{
		tunnel.connect();
		tunnel.send("LEAD");
		String[] names = tunnel.rec().split("\n");

		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window = new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane board = new JTextPane();
		board.setEditable(false);
		StyledDocument doc = board.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 18);
		try {
			doc.insertString(doc.getLength(), "       Leaderboards       \n", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleAttributeSet newAttrs= new SimpleAttributeSet();
		StyleConstants.setFontSize(newAttrs, 12);
		for(int i =0; i<names.length; i++)
		{
			//System.out.println(""+i+"--->  "+names[i]);
			String[] stuff = names[i].split("::");
			String name = stuff[0];
			String score = stuff[1];
			try {
				doc.insertString(doc.getLength(), ""+(i+1)+".  "+name+"  ("+score+")\n", newAttrs);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		window.add(board);

		JButton back = new JButton("Back");
		back.setActionCommand("bracketMenu");
		back.addActionListener(this);
		window.add(back);

		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);
	}

	public void signout()
	{
		tunnel.connect();
		tunnel.send("OUT::"+this.session);
		/*if(!tunnel.rec().equals("GOOD"))
		{
			System.out.println("Error signing out");
		}
		System.out.println("Signed out");*/
		this.signedIn=false;
		this.session=0;
		this.anchor=window.getLocationOnScreen();
		window.dispose();
		signin();
	}

	public void SigninSubMenu()
	{
		SigninSubMenu(new String[0]);
	}

	public void SigninSubMenu(String[] errors)
	{
		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window = new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout( new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane title = new JTextPane();
		title.setEditable(false);
		StyledDocument doc = title.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs,  24);
		StyleConstants.setBold(attrs,  true);
		try {
			doc.insertString(doc.getLength(), "    Please Sign In    \n", attrs);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		window.add(title);

		if(errors.length >0)
		{
			JTextPane problems = new JTextPane();
			problems.setEditable(false);
			StyledDocument newdoc = problems.getStyledDocument();
			SimpleAttributeSet newattrs = new SimpleAttributeSet();
			StyleConstants.setForeground(newattrs, Color.RED);
			for(int i =0; i<errors.length; i++)
			{
				try {
					newdoc.insertString(newdoc.getLength(), errors[i], newattrs);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			window.add(problems);
		}

		JTextField username = new JTextField("");
		username.setBorder(BorderFactory.createTitledBorder("Username"));
		username.setActionCommand("SigninSubmission");
		username.addActionListener(this);
		window.add(username);

		JPasswordField password = new JPasswordField("");
		password.setBorder(BorderFactory.createTitledBorder("Password"));
		password.setActionCommand("SigninSubmission");
		password.addActionListener(this);
		window.add(password);

		JButton submit = new JButton("Sign in ");
		submit.addActionListener(this);
		submit.setActionCommand("SigninSubmission");
		window.add(submit);

		JButton cancel = new JButton("  Back  ");
		cancel.addActionListener(this);
		cancel.setActionCommand("MAIN");
		window.add(cancel);


		textboxes=new JTextField[1];
		textboxes[0]=username;

		passwords= new JPasswordField[1];
		passwords[0]=password;
		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);
	}

	public void SigninSubmission()
	{
		String username = textboxes[0].getText();
		String[] remover = username.split("\\\\");
		if(remover.length>1)
		{
			username=remover[0];
			for(int i =1; i<remover.length; i++) username+=remover[i];
		}
		else
		{
			username=new String(remover[0]);
		}
		//System.out.println(username);
		String password = new String(passwords[0].getPassword());
		//System.out.println(password);
		remover = password.split("\\\\");
		if(remover.length>1)
		{
			password=remover[0];
			for(int i =1; i<remover.length; i++) password+=remover[i];
		}
		else
		{
			password = new String(remover[0]);
		}
		//System.out.println(password);
		tunnel.connect();
		tunnel.send("SIGNIN::"+username+"::"+password);
		String intake = tunnel.rec();
		session = Integer.parseInt(intake.split("::")[0]);
		myName=intake.split("::")[1];
		if(session<0)
		{
			String[] probs = {"That username does not exist\nOr your password was invalid"};
			SigninSubMenu(probs);
			return;
		}
		bracketMenu();
	}

	public void RegistrationSubMenu(String[] errors, String preUser)
	{
		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window=new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout( new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane title = new JTextPane();
		title.setEditable(false);
		StyledDocument doc = title.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs,  24);
		StyleConstants.setBold(attrs,  true);
		try {
			doc.insertString(doc.getLength(), "    Please Register Below    \n", attrs);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		window.add(title);

		if(errors.length >0)
		{
			JTextPane problems = new JTextPane();
			problems.setEditable(false);
			StyledDocument newdoc = problems.getStyledDocument();
			SimpleAttributeSet newattrs = new SimpleAttributeSet();
			StyleConstants.setForeground(newattrs, Color.RED);
			for(int i =0; i<errors.length; i++)
			{
				try {
					newdoc.insertString(newdoc.getLength(), errors[i], newattrs);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			window.add(problems);
		}

		JTextField username = new JTextField(preUser);
		//username.setEditable(false);
		username.setBorder(BorderFactory.createTitledBorder("Username"));
		username.setActionCommand("RegistrationSubmission");
		username.addActionListener(this);
		window.add(username);

		JPasswordField password1 = new JPasswordField("");
		password1.setBorder(BorderFactory.createTitledBorder("Password"));
		password1.setActionCommand("RegistrationSubmission");
		password1.addActionListener(this);
		window.add(password1);

		JPasswordField password2 = new JPasswordField("");
		password2.setBorder(BorderFactory.createTitledBorder("Confirm Password"));
		password2.setActionCommand("RegistrationSubmission");
		password2.addActionListener(this);
		window.add(password2);

		JTextField email = new JTextField("");
		email.setBorder(BorderFactory.createTitledBorder("Email"));
		email.setActionCommand("RegistrationSubmission");
		email.addActionListener(this);
		window.add(email);

		JButton submit = new JButton("Submit");
		submit.addActionListener(this);
		submit.setActionCommand("RegistrationSubmission");
		window.add(submit);

		JButton cancel = new JButton("  Back  ");
		cancel.addActionListener(this);
		cancel.setActionCommand("MAIN");
		window.add(cancel);

		textboxes=new JTextField[2];
		textboxes[0]=username;
		textboxes[1]=email;

		passwords=new JPasswordField[2];
		passwords[0]=password1;
		passwords[1]=password2;

		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);
	}

	public void RegistrationSubmission()
	{
		String username=textboxes[0].getText();
		String email = textboxes[1].getText();
		String password1 = new String(passwords[0].getPassword());
		String password2 = new String(passwords[1].getPassword());
		if(!password1.equals(password2))
		{
			String[] issue = {"* Passwords Did Not Match"};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(username.length()<5)
		{
			String[] issue = {"* Username must be at least 5 characters long"};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(username.contains(" ") || username.contains("\n"))
		{
			String[] issue = {"* Username cannot contain whitespace"};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(username.equals(password1))
		{
			String[] issue = {"* Username and Password must be different"};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(username.contains("@"))
		{
			String[] issue = {"* Username cannot contain \"@\""};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(username.contains(":"))
		{
			String[] issue = {"* Username cannot contain \":\""};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(username.contains("\\"))
		{
			String[] issue = {"* Username cannot contain \"\\\""};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(password1.length()<5)
		{
			String[] issue = {"* Password Must Be At Least 5 Characters Long"};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(password1.contains("\\"))
		{
			String[] issue = {"* Password Cannot Contain \"\\\""};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(password1.contains(":"))
		{
			String[] issue = {"* Password Cannot Contain \":\""};
			RegistrationSubMenu(issue, username);
			return;
		}
		if(!(email.contains("@") && email.split("@")[1].contains(".")))
		{
			String[] issue = {"* Email must be a valid email"};
			RegistrationSubMenu(issue, username);
			return;
		}
		tunnel.connect();
		tunnel.send("REG");
		tunnel.rec();
		//System.out.println("REG SENT");
		tunnel.send(username);
		//System.out.println("NAME SENT");
		String response = tunnel.rec();
		//System.out.println("Confirmation recieved");
		if(!response.equals("GOOD"))
		{
			tunnel.disconnect();
			String[] issue = {"* Username was already taken"};
			RegistrationSubMenu(issue, "");
			return;
		}
		tunnel.send(password1);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tunnel.send(email);
		this.anchor=window.getLocationOnScreen();
		window.dispose();
		signin();
	}

	public void loadBracket()
	{
		String target = (String)this.box.getSelectedItem();
		tunnel.connect();
		tunnel.send("VIEW::"+target);
		String score= tunnel.rec();
		String ppr = tunnel.rec();
		String stuff = tunnel.rec();
		//System.out.println("Recieved bracket");
		Bracket view = Bracket.unpack(stuff);

		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window=new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout( new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		StyledDocument doc = text.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 18);
		try {
			doc.insertString(doc.getLength(), "    "+target+"'s Bracket    \n    Score: "+score+"\tPossible Points Remaining: "+ppr+"\n\n", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			view.advancedDisplay(doc);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.add(text);

		JButton backwards = new JButton("Back");
		backwards.setActionCommand("bracketMenu");
		backwards.addActionListener(this);
		window.add(backwards);

		window.pack();
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setLocation(anchor);
		window.setVisible(true);
		this.skipAnchor=true;
	}

	public void sendBracket()
	{
		//System.out.println("Packing bracket");
		//this.anchor=window.getLocationOnScreen();
		window.dispose();
		window=new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout( new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane hold = new JTextPane();
		hold.setEditable(false);
		StyledDocument doc = hold.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 14);
		StyleConstants.setForeground(attrs, Color.BLUE);
		try {
			doc.insertString(doc.getLength(), "Sending Bracket (this may take a while)\nDo not close this window\nPlease wait...", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.add(hold);

		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);
		skipAnchor=true;
	}



	public void tickServer()
	{
		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window=new JFrame("Brackets");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout( new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane hold = new JTextPane();
		StyledDocument doc = hold.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 14);
		StyleConstants.setForeground(attrs, Color.BLUE);
		try {
			doc.insertString(doc.getLength(), "Ticking server.  Please wait...", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.add(hold);

		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);

		tunnel.connect();
		tunnel.send("TICK::"+session);
		tunnel.rec();
		bracketMenu();
	}

	public void shutdownServer()
	{
		tunnel.connect();
		tunnel.send("SHUTDOWN::"+session);
		tunnel.disconnect();
		tunnel.connect();
		tunnel.disconnect();

		this.anchor=window.getLocationOnScreen();
		window.dispose();
		window=new JFrame("");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout( new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
		window.getContentPane().setBackground(Color.WHITE);

		JTextPane hold = new JTextPane();
		hold.setEditable(false);
		StyledDocument doc = hold.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 16);
		StyleConstants.setForeground(attrs, Color.BLUE);
		try {
			doc.insertString(doc.getLength(), "  Server Stopped.\n  Close program now  ", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.add(hold);

		window.pack();
		window.setLocation(anchor);
		window.setVisible(true);

	}
	
	public void updateServer()
	{
		tunnel.connect();
		tunnel.send("UPDATE::"+session+"::"+ver);
		tunnel.rec();
		tunnel.disconnect();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("SigninSubMenu")) SigninSubMenu();
		else if(command.equals("SigninSubmission")) SigninSubmission();
		else if(command.equals("RegistrationSubMenu")) RegistrationSubMenu(new String[0], "");
		else if(command.equals("RegistrationSubmission")) RegistrationSubmission();
		else if(command.equals("viewBrackets")) viewBrackets();
		else if(command.equals("submitBracket")) submitBracket();
		else if(command.equals("leaderboards")) leaderboards();
		else if(command.equals("signout")) signout();
		else if(command.equals("MAIN")) 
		{
			this.anchor=window.getLocationOnScreen();
			window.dispose();
			menu();
		}
		else if(command.equals("bracketMenu")) bracketMenu();
		else if(command.equals("loadBracket")) loadBracket();
		else if(command.equals("sendBracket"))
		{
			Transmitter signal = new Transmitter(this.buffer, this.tunnel, this.session, this);
			new Thread(signal).start();
			//wait = new Thread(signal);
			//wait.start();
			sendBracket();
		}
		else if(command.equals("TICK")) tickServer();
		else if(command.equals("SHUTDOWN")) shutdownServer();
		else if(command.equals("STOP")) window.dispose();
		else if(command.equals("UPDATE")) updateServer();


	}

	public JTextPane tagline()
	{
		JTextPane out = new JTextPane();
		out.setEditable(false);
		StyledDocument doc = out.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 12);
		StyleConstants.setForeground(attrs, Color.BLACK);
		try {
			doc.insertString(doc.getLength(), "This program was developed by Jroot Studios", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

}
