package tractor.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientView extends JFrame {

	private static final long serialVersionUID = -8991821814527274354L;
	private JLabel statusField;
	private JTextField statusColor;
	private JPanel statusBar;
	private JTextArea chatText;
	private JTextField chatLine;
	private JTextField nameField;
	private JButton connectButton;
	public final static int NULL = 0;
	public final static int DISCONNECTED = 1;
	public final static int DISCONNECTING = 2;
	public final static int BEGIN_CONNECT = 3;
	public final static int CONNECTED = 4;
	public final static String statusMessages[] = {
		" Error! Could not connect!", " Disconnected",
		" Disconnecting...", " Connecting...", " Connected"
	};

	public static void main(String ...bobby) {
		new ClientView();
	}

	public ClientView() {
		initComponents();
	}
	private void initComponents() {

		statusField = new JLabel();
		statusField.setText(statusMessages[DISCONNECTED]);
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusColor, BorderLayout.WEST);
		statusBar.add(statusField, BorderLayout.CENTER);

		/*// Set up the chat pane
		JPanel chatPane = new JPanel(new BorderLayout());
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
		chatPane.setPreferredSize(new Dimension(600, 200));*/

		// Create an options pane




		JPanel loginPane = new JPanel();

		GridBagLayout loginPaneLayout = new GridBagLayout();
		loginPaneLayout.rowWeights = new double[] {0.4, 0.1, 0.0, 0.0, 0.5};
		loginPaneLayout.rowHeights = new int[] {7, 7, 7, 7, 7};
		loginPaneLayout.columnWeights = new double[] {1.0};
		loginPaneLayout.columnWidths = new int[] {7};
		loginPane.setLayout(loginPaneLayout);
		loginPane.setPreferredSize(new Dimension(640,480));

		loginPane.add(new JLabel("TRACTORLOL"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		JPanel userPane = new JPanel();
		userPane.add(new JLabel("Username: "));
		nameField = new JTextField(10);
		nameField.setEnabled(true);
		nameField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				/*nameField.selectAll();
	               // Should be editable only when disconnected
	               if (connectionStatus != DISCONNECTED) {
	                  changeStatusNTS(NULL, true);
	               }
	               else {
	                  screenname = nameField.getText();
	               }*/
			}
		});
		userPane.add(nameField);
		loginPane.add(userPane, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		ActionListener buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*// Request a connection initiation
               if (e.getActionCommand().equals("connect")) {
                  changeStatusNTS(BEGIN_CONNECT, true);
               }
               // Disconnect
               else {
                  changeStatusNTS(DISCONNECTING, true);
               }*/
			}
		};
		connectButton = new JButton("Connect");
		connectButton.setMnemonic(KeyEvent.VK_C);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);
		loginPane.add(connectButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(loginPane, BorderLayout.CENTER);
		mainPane.add(statusBar, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(mainPane);
		this.setSize(this.getPreferredSize());
		this.setLocation(200, 200);
		this.pack();
		this.setVisible(true);
	}

}
