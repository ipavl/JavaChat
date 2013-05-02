/* 
 * ClientGUI.java
 * 
 * This class is used in conjunction with Client.java to run a client in GUI mode.
 */

package org.pavlinic.chat.client;

import javax.swing.*;

import org.pavlinic.chat.AeSimpleSHA1;
import org.pavlinic.chat.PacketHandler;

import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
    final JFrame frame = new JFrame("JDialog Demo");
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	
	private String defaultHost;
	
	private JTextArea lstChat;
	
	private JTextField txtServer;
	private JTextField txtPort;
	private JTextField txtMessage;
	
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnUserlist;
	private JScrollPane scrollPane;
	
	Random r = new Random();
	int randint = r.nextInt(50);
	String username = "Guest" + randint;
	String password = null;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("Chat");
		defaultPort = port;
		defaultHost = host;

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(590, 480);
		setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel lblServer = new JLabel("Server:");
		lblServer.setBounds(10, 11, 46, 14);
		getContentPane().add(lblServer);
		
		txtServer = new JTextField();
		txtServer.setText("localhost");
		txtServer.setBounds(58, 8, 91, 20);
		txtServer.setColumns(10);
		getContentPane().add(txtServer);
		
		JLabel lblPort = new JLabel(":");
		lblPort.setBounds(153, 11, 4, 14);
		getContentPane().add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setText("1500");
		txtPort.setBounds(159, 8, 40, 20);
		txtPort.setColumns(10);
		getContentPane().add(txtPort);
		
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(267, 7, 89, 23);
		btnConnect.addActionListener(this);
		getContentPane().add(btnConnect);
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setEnabled(false);
		btnDisconnect.setBounds(366, 7, 109, 23);
		btnDisconnect.addActionListener(this);
		getContentPane().add(btnDisconnect);
		
		btnUserlist = new JButton("Userlist");
		btnUserlist.setEnabled(false);
		btnUserlist.setBounds(485, 7, 89, 23);
		btnUserlist.addActionListener(this);
		getContentPane().add(btnUserlist);
		
		txtMessage = new JTextField();
		txtMessage.setText("Click here to set your login credentials.");
		txtMessage.setBounds(0, 423, 584, 29);
		txtMessage.setColumns(10);
		txtMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!connected && e.getButton() == MouseEvent.BUTTON1) {
                    // Open login window
				    LoginDialog loginDlg = new LoginDialog(frame);
                    loginDlg.setVisible(true);
                    
                    // Get username from login dialog
                    if (loginDlg.getUsername().length() != 0) {
                        username = loginDlg.getUsername();
                        // Get and hash password from login dialog
                        try {
                            password = AeSimpleSHA1.SHA1(loginDlg.getPassword());
                        } catch (NoSuchAlgorithmException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (UnsupportedEncodingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        append("Set username to: " + username + "\n");
                    }
                }
			}
		});
		txtMessage.addKeyListener(new KeyAdapter() {	// set username when not connected
			@Override
			public void keyPressed(KeyEvent e) {
				if(!connected && e.getKeyCode() == KeyEvent.VK_ENTER) {
					String msg = txtMessage.getText();
					// empty message ignore it
					if(msg.length() == 0)
						return;
					username = msg;
					txtMessage.setText("");
					append("Set username to: " + username + "\n");
					return;
				}
			}
		});
		getContentPane().add(txtMessage);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0, 36, 584, 383);
		getContentPane().add(scrollPane);
		
		lstChat = new JTextArea("", 0, 0);
		lstChat.setEditable(false);
		scrollPane.setViewportView(lstChat);
		lstChat.setLineWrap(true);
		setVisible(true);

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		lstChat.append(str);
		lstChat.setCaretPosition(lstChat.getText().length() - 1);
	}
	
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		btnUserlist.setEnabled(false);
		//txtMessage.setText("");
		// reset port number and host name as a construction time
		txtPort.setText("" + defaultPort);
		txtServer.setText(defaultHost);
		// let the user change them
		txtServer.setEditable(true);
		txtPort.setEditable(true);
		// don't react to a <CR> after the username
		txtMessage.removeActionListener(this);
		connected = false;
	}
		
	void connect() {
		// connection request
		/*Random r = new Random();
		int randint = r.nextInt(50);
		String username = "Guest" + randint;*/
	    
		// empty username ignore it
		if(username.length() == 0)
			return;
		
		// empty serverAddress ignore it
		String server = txtServer.getText().trim();
		if(server.length() == 0)
			return;
		
		// empty or invalid port number, ignore it
		String portNumber = txtPort.getText().trim();
		if(portNumber.length() == 0)
			return;
		int port = 0;
		
		try {
			port = Integer.parseInt(portNumber);
		}
		catch(Exception en) {
			return;   // nothing I can do if port number is not valid
		}

		// try creating a new Client with GUI
		client = new Client(server, port, username, password, Client.getClientVer(), this);
		// test if we can start the Client
		if(!client.start()) 
			return;
		txtMessage.setText("");
		connected = true;
		
		// disable login button
		btnConnect.setEnabled(false);
		// enable the 2 buttons
		btnDisconnect.setEnabled(true);
		btnUserlist.setEnabled(true);
		// disable the Server and Port JTextField
		txtServer.setEditable(false);
		txtPort.setEditable(false);
		// Action listener for when the user enter a message
		txtMessage.addActionListener(this);
	}

	
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		// if it is the disconnect button
		if(o == btnDisconnect) {
			client.sendMessage(new PacketHandler(PacketHandler.LOGOUT, ""));
			return;
		}
		
		// if it the userlist button
		if(o == btnUserlist) {
			client.sendMessage(new PacketHandler(PacketHandler.LISTUSERS, ""));
			return;
		}		
		
		// if it is the connect button 
		if(o == btnConnect) {
			connect();
		}

		// coming from the JTextField
		if(connected) {
			// TODO: Data entry checks
			String msg = txtMessage.getText();
			// empty message ignore it
			if(msg.length() == 0)
				return;
			// just have to send the message
			client.sendMessage(new PacketHandler(PacketHandler.MESSAGE, txtMessage.getText()));				
			txtMessage.setText("");
			txtMessage.requestFocus();
			return;
		}
	}
	
	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);
	}
}

