package tractor.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import tractor.client.handlers.IOFactory;
import tractor.thirdparty.CloseableTabbedPane;

class CommandListener implements ActionListener {
	ClientView clientview;
	Client client;
	private JTextField chatLine;
	private CloseableTabbedPane chatTabs;
	private ChatPane console;
	private IOFactory io;
	CommandListener() {
		clientview = ClientView.getInstance();
		client = Client.getInstance();
		io = client.getIO();
		chatLine = clientview.getChatLine();
		chatTabs = clientview.getChatTabs();
		console = clientview.getConsole();
	}
			public void actionPerformed(ActionEvent e) {
				String s = chatLine.getText();
				if (!s.equals("")) {
					if(s.charAt(0)== '/') {
						int index = s.indexOf(" ");
						String cmd,args;
						if( index == -1) {
							cmd = s.substring(1).toUpperCase();
							args = "";
						} else {
							cmd = s.substring(1,index).toUpperCase();
							args = s.substring(index+1);
						}
						
						
						/*
						 * Massive list of if else
						 * Begin Command List
						 */
						if(cmd.equals("JOIN")) {
							io.write("JOIN "+args, IOFactory.CHATCMD);
						} else if(cmd.equals("PART")) {
							if(args.equals(""))
								args = clientview.getSelectedChatroomName();
							io.write("PART "+args, IOFactory.CHATCMD);
						} else if(cmd.equals("GCREATE")) {
							if(io == null) System.out.println("OH NOES");
							io.write("GCREATE", IOFactory.CHATCMD);
						} else if(cmd.equals("GPART")) {
							//io.write("GPART "+client.getGameID(), IOFactory.CHATCMD);
						} else if(cmd.equals("GHOOK")) {
							io.write("GHOOK "+args, IOFactory.CHATCMD);
						} else if(cmd.equals("QUIT")) {
							io.write("QUIT", IOFactory.CHATCMD);
						} else {
							System.out.println("no such command");
						}
						/*
						 * End Commands
						 */
						
						
					} else {
						ChatPane chat = (ChatPane)chatTabs.getSelectedComponent();
						if(chat != console) {
							io.write(chat.getName()+"|"+s, IOFactory.CHAT);
							chat.append(client.getUsername()+"> "+s);
						} else {
							chat.append("> "+s);
						}
					}
					chatLine.setText("");
				}
			}
		}