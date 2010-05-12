package tractor.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.SlickException;

import tractor.client.game.TractorGame;
import tractor.client.handlers.IOFactory;
import tractor.thirdparty.CloseableTabbedPane;
import tractor.thirdparty.CloseableTabbedPaneListener;

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
	private CloseableTabbedPane chatTabs;
	private ChatPane console;
	private final static String statusMessages[] = {
		" Error! Could not connect!", " Disconnected",
		" Disconnecting...", " Connecting...", " Connected"
	};

	/**Constructor
	 * 
	 */
	public ClientView() {
		ClientView.instance = this;
		this.client = Client.getInstance();
		initComponents();
	}
	/**Returns an instance
	 * @return
	 * 
	 */
	public static ClientView getInstance() {
		return ClientView.instance;
	}
	/**It updates the status asynchronously
	 * 
	 */
	public void updateStatusTS() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClientView.getInstance().updateStatus();
			}
		});
	}
	/**It updates the status 
	 * 
	 */
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
			nameField.grabFocus();
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

	/**Returns the username
	 * @return
	 * 
	 */
	public String getUsername() {
		//this.nameField.selectAll();
		return this.nameField.getText();
	}
	/**Initializes the menu
	 * 
	 */
	private void initMenu() {
		JMenuBar jMenuBar1 = new JMenuBar();
		setJMenuBar(jMenuBar1);
		{
			JMenu jMenu1 = new JMenu();
			jMenuBar1.add(jMenu1);
			jMenu1.setText("File");
			{
				JMenuItem jMenuItem1 = new JMenuItem();
				jMenu1.add(jMenuItem1);
				jMenuItem1.setText("Disconnect");
				JMenuItem jMenuItem2 = new JMenuItem();
				jMenu1.add(jMenuItem2);
				jMenuItem2.setText("Exit");
				jMenuItem2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//TODO: clean up and disconnect
						Client.getInstance().getIO().kill();
						System.exit(0);
					}
				});
			}
		}
	}
	/**It focuses on the chat window
	 * 
	 */
	public void focusChat() {
		this.chatLine.grabFocus();
	}
	/**It initializes the components
	 * 
	 */
	private void initComponents() {

		this.initMenu();
		
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
		

		chatLine = new JTextField();
		chatLine.setEnabled(false);
		chatLine.addActionListener(new CommandListener());
		
		chatTabs = new CloseableTabbedPane();
		chatTabs.addCloseableTabbedPaneListener(new CloseableTabbedPaneListener() {
			public boolean closeTab(int tabIndexToClose) {
				client.getIO().write("PART "+chatTabs.getComponentAt(tabIndexToClose).getName(), IOFactory.CHATCMD);
				return true;
			}
		});
		chatTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ClientView.getInstance().focusChat();
			}
		});
		
		console = new ChatPane("Console");
		chatTabs.addTab(console, false);
		
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTabs, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(600, 200));

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
		
		/*
		 * test code
		 
		this.setVisible(true);
		CanvasGameContainer app = null;
		JPanel gamePanel = new JPanel();
		gamePanel.setSize(704,396);
		gamePanel.setPreferredSize(new Dimension(704,396));
		gamePanel.setMinimumSize(new Dimension(704,396));
		try {
			app = new CanvasGameContainer(new TractorGame());
			//gamePanel.add(app);
			app.getContainer().setAlwaysRender(true);
			app.setPreferredSize(new Dimension(704,396));
			app.setSize(704,396);
			app.setMinimumSize(new Dimension(704,396));
			app.setVisible(true);
			app.start();
			
		} catch (SlickException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //app.setDisplayMode(704,396,false);
        //app.setTargetFrameRate(30);
        mainPane.add(app, BorderLayout.CENTER);
        /*
         * test code
         */
        
		mainPane.add(loginPane, BorderLayout.CENTER);
		mainPane.add(statusBar, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(mainPane);
		this.setSize(this.getPreferredSize());
		this.setLocation(200, 200);
		this.pack();
	}
	
	/**It joins the new chatroom
	 * @param name
	 * 
	 */
	public void join(String name) {
		this.chatTabs.addTab(new ChatPane(name),true);
		this.chatTabs.setSelectedComponent(getChatroom(name));
	}
	
	/**It leaves the chatroom
	 * @param name
	 * 
	 */
	public void part(String name) {
		int index = this.chatTabs.indexOfTab(name);
		if(index != -1) {
			this.chatTabs.remove(index);
		}
	}
	/**It gets the particular chatroom name
	 * @return
	 * 
	 */
	public String getSelectedChatroomName() {
		return this.chatTabs.getSelectedComponent().getName();
	}
	/**Returns the chatroom
	 * @param name
	 * @return
	 * 
	 */
	public ChatPane getChatroom(String name) {
		int index = this.chatTabs.indexOfTab(name);
		if(index == -1)
			return null;
		return (ChatPane) this.chatTabs.getComponentAt(index);
	}
	
	public JTextField getChatLine() {
		return this.chatLine;
	}
	
	public CloseableTabbedPane getChatTabs() {
		return this.chatTabs;
	}
	
	public ChatPane getConsole() {
		return this.console;
	}

}
