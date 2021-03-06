/* 
 * ServerGUI.java
 * Copyright (C) 2013  ipavl <https://www.github.com/ipavl/javachat>
 * 
 * This class is used in conjunction with Server.java to run a server in GUI mode.
 * -----------------------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pavlinic.chat.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class ServerGUI extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    // the stop and start buttons
    private JButton stopStart;

    // JTextArea for the chat room and the events
    private JTextArea chat, event;

    // the port number and command entry text areas
    private JTextField tPortNumber;
    private JTextField sendCommand;

    // my server
    private Server server;

    // server constructor that receives the port to listen on for connection as parameter
    ServerGUI(int port) {
        super("jChat Server (Build " + Server.sVersion + " -- https://www.github.com/ipavl/javachat)");
        server = null;

        // in the NorthPanel the PortNumber the Start and Stop buttons
        JPanel north = new JPanel();
        north.add(new JLabel("Port number: "));
        tPortNumber = new JTextField("1500");
        tPortNumber.setColumns(4);
        north.add(tPortNumber);

        // to stop or start the server, we start with "Start"
        stopStart = new JButton("Start");
        stopStart.addActionListener(this);
        north.add(stopStart);
        getContentPane().add(north, BorderLayout.NORTH);

        sendCommand = new JTextField();
        sendCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String cmd = sendCommand.getText();
                    if (cmd.startsWith("/"))	// accept both / and non-/ commands
                        cmd = cmd.substring(1);

                    // empty message ignore it
                    if(cmd.length() == 0)
                        return;

                    CommandHandler.processCommand("Console", cmd);	// process command
                    sendCommand.setText("");
                    return;
                }
            }
        });

        sendCommand.addMouseListener(new MouseAdapter() {	// clear box on mouse click
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    sendCommand.setText("");
                }
            }
        });

        sendCommand.setEditable(false);
        sendCommand.setText("Execute commands here when the server is running.");
        north.add(sendCommand);
        sendCommand.setColumns(30);

        // the event and chat room
        JPanel center = new JPanel(new GridLayout(2,1));
        chat = new JTextArea(0,0);
        chat.setEditable(false);

        // Chat logging area
        JScrollPane scrollPane = new JScrollPane(chat);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        center.add(scrollPane);
        event = new JTextArea(0,0);
        event.setEditable(false);

        // Event logging area
        JScrollPane scrollPane_1 = new JScrollPane(event);
        scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        center.add(scrollPane_1);	
        getContentPane().add(center);

        // need to be informed when the user click the close button on the frame
        addWindowListener(this);
        setSize(560, 423);
        setVisible(true);
    }		

    // append message to the two JTextAreas, scroll afterwards
    void appendRoom(String str) {
        chat.append(Server.dateFormat.format(new Date()) + " " + str);
        chat.setCaretPosition(chat.getText().length() - 1);   // scroll on update
    }

    void appendEvent(String str) {
        event.append(Server.dateFormat.format(new Date()) + " " + str);
        event.setCaretPosition(event.getText().length() - 1); // scroll on update
    }

    // start or stop where clicked
    public void actionPerformed(ActionEvent e) {
        // if running we have to stop
        if(server != null) {
            server.stop();
            server = null;
            tPortNumber.setEditable(true);
            sendCommand.setEditable(false);
            sendCommand.setText("Execute commands here when the server is running.");
            stopStart.setText("Start");
            return;
        }

        // OK start the server	
        int port;
        try {
            port = Integer.parseInt(tPortNumber.getText().trim());
        }
        catch(Exception er) {
            appendEvent("Invalid port number");
            return;
        }

        // create a new Server
        server = new Server(port, this);

        // and start it as a thread
        new ServerRunning().start();
        stopStart.setText("Stop");
        tPortNumber.setEditable(false);
        sendCommand.setEditable(true);
        sendCommand.setText("Enter a command here and press enter.");
    }

    // entry point to start the Server
    public static void main(String[] arg) {
        // start server default port 1500
        new ServerGUI(1500);
    }

    /*
     * If the user click the X button to close the application
     * I need to close the connection with the server to free the port
     */
    public void windowClosing(WindowEvent e) {
        // if my Server exist
        if(server != null) {
            try {
                server.stop();			// ask the server to close the connection
            }
            catch(Exception eClose) {
            }
            server = null;
        }

        // dispose the frame
        dispose();
        System.exit(0);
    }
    // I can ignore the other WindowListener method
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /*
     * A thread to run the Server
     */
    class ServerRunning extends Thread {
        public void run() {
            server.start();         // should execute until it fails
            // the server failed
            stopStart.setText("Start");
            tPortNumber.setEditable(true);
            appendEvent("Server crashed or was shutdown\n");
            server = null;
        }
    }

}