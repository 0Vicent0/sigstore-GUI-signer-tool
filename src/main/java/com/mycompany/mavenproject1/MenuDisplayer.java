/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author vicent
 */
public class MenuDisplayer {

    public void drawMenu(JFrame frame) {

    String licenseText = """
    Sigstore GUI Signer Tool
    Copyright (C) 2025 Vicent Monsonís López

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    """;
    
    String aboutText = """
    Sigstore GUI Signer Tool is a simple yet powerful application. 
    It allows you to sign and verify artifacts using a convenient, portable 
    GUI interface for Sigstore.
    """;
    
        // --- Add Menu Bar ---
        JMenuBar menuBar = new JMenuBar();

        // Create "Help" menu (or "About" menu, as you prefer)
        JMenu helpMenu = new JMenu("Help");

        // Add "License" item
        JMenuItem licenseItem = new JMenuItem("License");

        licenseItem.addActionListener(e -> {
            // Show license info when clicked
            JOptionPane.showMessageDialog(frame,
                    licenseText,
                    "License",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        );
        
        // Add "License" item
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> {
            // Show about info when clicked
            JOptionPane.showMessageDialog(frame,
                    aboutText,
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        );
        
        

        helpMenu.add(licenseItem);
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        // Attach menu bar to frame
        frame.setJMenuBar(menuBar);
    }
}
