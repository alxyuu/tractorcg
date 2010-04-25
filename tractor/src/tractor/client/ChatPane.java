package tractor.client;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class ChatPane extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MAX_LINES = 3;
	private JTextArea textarea;
	private int lines;
	ChatPane(String name) {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setName(name);
		this.textarea = new JTextArea(10, 20);
		textarea.setLineWrap(true);
		textarea.setEditable(false);
		textarea.setForeground(Color.blue);
		this.setViewportView(textarea);
		//this.putClientProperty("isClosable",true);
		this.lines = 0;
	}
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
	}
}
