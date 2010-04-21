package tractor.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientView extends JFrame {

	private static final long serialVersionUID = -8991821814527274354L;
	private static ClientView instance;
	private Client client;
	private JLabel statusField;
	private JTextField statusColor;
	private JPanel statusBar;
	private JTextArea chatText;
	private JTextField chatLine;
	private JTextField nameField;
	private JButton connectButton;
	private JPanel loginPane;
	private JPanel mainPane;
	private JPanel chatPane;
	private JLabel errorLabel;
	private final static String statusMessages[] = {
		" Error! Could not connect!", " Disconnected",
		" Disconnecting...", " Connecting...", " Connected"
	};

	public ClientView() {
		ClientView.instance = this;
		this.client = Client.getInstance();
		initComponents();
	}
	public static ClientView getInstance() {
		return ClientView.instance;
	}
	public void updateStatusTS() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClientView.getInstance().updateStatus();
			}
		});
	}
	public void updateStatus() {
		mainPane.removeAll();
		switch(this.client.getConnectionStatus()) {

		//check errors
		case Client.DISCONNECTED:
			connectButton.setEnabled(true);
			nameField.setEnabled(true);
			chatLine.setText(""); chatLine.setEnabled(false);
			statusColor.setBackground(Color.red);
			loginPane.remove(errorLabel);
			if(this.client.getErrorCode() != ClientError.NO_ERROR) {
				errorLabel.setText(this.client.getErrorMessage());
				loginPane.add(errorLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			}
			mainPane.add(loginPane, BorderLayout.CENTER);
			mainPane.add(statusBar, BorderLayout.SOUTH);
			break;
		case Client.CONNECTED:
			connectButton.setEnabled(false);
			nameField.setEnabled(false);
			chatLine.setEnabled(true);
			statusColor.setBackground(Color.green);
			mainPane.add(chatPane, BorderLayout.CENTER);
			mainPane.add(statusBar, BorderLayout.SOUTH);
			break;

		case Client.BEGIN_CONNECT:
			connectButton.setEnabled(false);
			nameField.setEnabled(false);
			chatLine.setEnabled(false);
			chatLine.grabFocus();
			statusColor.setBackground(Color.orange);
			mainPane.add(loginPane, BorderLayout.CENTER);
			mainPane.add(statusBar, BorderLayout.SOUTH);
			break;

		}
		statusField.setText(statusMessages[this.client.getConnectionStatus()]);
		this.repaint();
	}

	public String getUsername() {
		//this.nameField.selectAll();
		return this.nameField.getText();
	}
	private void initComponents() {

		statusField = new JLabel();
		statusField.setText(statusMessages[Client.DISCONNECTED]);
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusColor, BorderLayout.WEST);
		statusBar.add(statusField, BorderLayout.CENTER);

		// Set up the chat pane
		chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		JScrollPane chatTextPane = new JScrollPane(chatText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(false);
		chatLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = chatLine.getText();
				if (!s.equals("")) {
					//cock goes here
				}
			}
		});
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(600, 200));

		// Create an options pane




		loginPane = new JPanel();

		GridBagLayout loginPaneLayout = new GridBagLayout();
		loginPaneLayout.rowWeights = new double[] {0.4, 0.1, 0.0, 0.0, 0.0, 0.5};
		loginPaneLayout.rowHeights = new int[] {7, 7, 7, 7, 7, 7};
		loginPaneLayout.columnWeights = new double[] {1.0};
		loginPaneLayout.columnWidths = new int[] {7};
		loginPane.setLayout(loginPaneLayout);
		loginPane.setPreferredSize(new Dimension(640,480));

		loginPane.add(new JLabel("TRACTORLOL"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		errorLabel = new JLabel();
		errorLabel.setForeground(Color.red);
		//loginPane.add(errorLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		ActionListener loginListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Client.getInstance().isConnected()) {
					Client.getInstance().login(true);
				} else {
					Client.getInstance().connect(true);
				}
				updateStatusTS();
			}
		};
		
		JPanel userPane = new JPanel();
		userPane.add(new JLabel("Username: "));
		nameField = new JTextField(10);
		nameField.setEnabled(true);
		nameField.addActionListener(loginListener);
		userPane.add(nameField);
		loginPane.add(userPane, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		
		connectButton = new JButton("Connect");
		connectButton.setMnemonic(KeyEvent.VK_C);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(loginListener);
		connectButton.setEnabled(true);
		loginPane.add(connectButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		mainPane = new JPanel(new BorderLayout());
		mainPane.add(loginPane, BorderLayout.CENTER);
		mainPane.add(statusBar, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(mainPane);
		this.setSize(this.getPreferredSize());
		this.setLocation(200, 200);
		this.pack();
	}

}
