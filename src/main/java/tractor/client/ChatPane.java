package tractor.client;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/** Displays the chat window
 * @author 378250
 *
 */
public class ChatPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private static final int MAX_LINES = 100;
	private JTextArea textarea;
	private int lines;

	/** Constructor
	 * @param name
	 *
	 */
	ChatPane(String name) {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setName(name);
		this.textarea = new JTextArea();
		textarea.setLineWrap(true);
		textarea.setEditable(false);
		textarea.setForeground(Color.blue);
		textarea.setWrapStyleWord(true);
		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBackground( textarea.getBackground() );
		panel.setBorder( textarea.getBorder() );
		panel.add(textarea, BorderLayout.SOUTH);
		this.setViewportView(panel);
		//this.putClientProperty("isClosable",true);
		this.lines = 0;
	}
	/** Adds a string to the chat window
	 * @param s
	 */
	synchronized public void append(String s) {
		this.textarea.append("\n"+s);
		this.lines = this.textarea.getLineCount();
		if(this.lines > MAX_LINES) {
			int offset;
			try {
				offset = this.textarea.getLineEndOffset(this.lines - MAX_LINES - 1);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			this.textarea.replaceRange("",0,offset);
			this.lines = this.textarea.getLineCount();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scrollToBottom();
			}
		});
		
	}
	
	public void scrollToBottom() {
		getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
	}
}
