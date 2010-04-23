package tractor.client;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatPane extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textarea;
	ChatPane(String name) {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setName(name);
		this.textarea = new JTextArea(10, 20);
		textarea.setLineWrap(true);
		textarea.setEditable(false);
		textarea.setForeground(Color.blue);
		this.setViewportView(textarea);
		//this.putClientProperty("isClosable",true);
	}
	synchronized public void append(String s) {
		this.textarea.append(s+"\n");
	}
}
