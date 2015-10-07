package brackets;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Transmitter implements Runnable, ActionListener {
	private Bracket buffer;
	private Tunnel tunnel;
	private int session;
	private JFrame host;
	private ActionListener ear;

	public Transmitter(Bracket buffer, Tunnel tunnel, int session, ActionListener ear) {
		this.buffer=buffer;
		this.tunnel=tunnel;
		this.session=session;
		this.ear=ear;
	}

	@Override
	public void run() {

		this.buffer.assign(new NumericAssignmentSystem());
		String out = this.buffer.pack();
		tunnel.connect();
		tunnel.send("BRACKET::"+this.session);
		tunnel.rec();
		tunnel.send(out);
		tunnel.rec();
		
		host=new JFrame("");
		host.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		host.setLayout( new BoxLayout(host.getContentPane(), BoxLayout.Y_AXIS));
		host.getContentPane().setBackground(Color.WHITE);
		
		JTextPane hold = new JTextPane();
		hold.setEditable(false);
		StyledDocument doc = hold.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrs, 14);
		StyleConstants.setForeground(attrs, Color.BLUE);
		try {
			doc.insertString(doc.getLength(), "Bracket has been sent", attrs);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		host.add(hold);
		
		JButton back = new JButton("Continue");
		back.setActionCommand("bracketMenu");
		back.addActionListener(this.ear);
		back.addActionListener(this);
		host.add(back);
		
		host.pack();
		host.setLocationRelativeTo(null);
		host.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("bracketMenu"))
		{
			host.dispose();
		}
		
	}

}
