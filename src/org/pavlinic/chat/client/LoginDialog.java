/* 
 * LoginDialog.java
 * Copyright (C) 2013  ipavl <https://www.github.com/ipavl/javachat>
 * 
 * This class provides the interface to allow for the entry of login
 * credentials in the GUI client.
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

package org.pavlinic.chat.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
 
public class LoginDialog extends JDialog {
 
    private static final long serialVersionUID = 5945996530946583488L;
    private JButton btnLogin;
    private JButton btnCancel;

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    
    private JLabel lbPassHelp;
    private JLabel lbUsername;
    private JLabel lbPassword;
 
    public LoginDialog(Frame parent) {
        super(parent, "Login Credentials", true);
        setAlwaysOnTop(true);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0};
        JPanel panel = new JPanel(gbl_panel);
 
        panel.setBorder(new LineBorder(Color.GRAY));
 
        btnLogin = new JButton("Save");
 
        btnLogin.addActionListener(new ActionListener() {
 
            // Okay button
            public void actionPerformed(ActionEvent e) {
                if(getUsername().length() == 0)
                    JOptionPane.showMessageDialog(LoginDialog.this, "You must enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
                else
                    setVisible(false);
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
 
            // Cancel button
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        
        lbPassHelp = new JLabel("(Leave password blank if not registered)");
        bp.add(lbPassHelp);
        bp.add(btnCancel);
 
        getContentPane().add(panel, BorderLayout.CENTER);
        
        lbUsername = new JLabel("Username:");
        GridBagConstraints gbc_lbUsername = new GridBagConstraints();
        gbc_lbUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lbUsername.anchor = GridBagConstraints.EAST;
        gbc_lbUsername.gridx = 0;
        gbc_lbUsername.gridy = 0;
        panel.add(lbUsername, gbc_lbUsername);
        
        tfUsername = new JTextField();
        GridBagConstraints gbc_tfUsername = new GridBagConstraints();
        gbc_tfUsername.gridwidth = 3;
        gbc_tfUsername.insets = new Insets(0, 0, 5, 0);
        gbc_tfUsername.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfUsername.gridx = 1;
        gbc_tfUsername.gridy = 0;
        panel.add(tfUsername, gbc_tfUsername);
        tfUsername.setColumns(10);
        
        lbPassword = new JLabel("Password:");
        GridBagConstraints gbc_lbPassword = new GridBagConstraints();
        gbc_lbPassword.insets = new Insets(0, 0, 0, 5);
        gbc_lbPassword.anchor = GridBagConstraints.WEST;
        gbc_lbPassword.gridx = 0;
        gbc_lbPassword.gridy = 1;
        panel.add(lbPassword, gbc_lbPassword);
        
        pfPassword = new JPasswordField();
        pfPassword.setToolTipText("If you do not have a registered account on the server you are connecting to, leave this blank.");
        GridBagConstraints gbc_pfPassword = new GridBagConstraints();
        gbc_pfPassword.gridwidth = 3;
        gbc_pfPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_pfPassword.gridx = 1;
        gbc_pfPassword.gridy = 1;
        panel.add(pfPassword, gbc_pfPassword);
        pfPassword.setColumns(10);
        getContentPane().add(bp, BorderLayout.PAGE_END);
 
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }
 
    public String getUsername() {
        return tfUsername.getText().trim();
    }
 
    public String getPassword() {
        return new String(pfPassword.getPassword());
    }
}