package com.mycompany.mavenproject1;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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

        Usage:

        The usage of the program should be almost trivial if you have at least read some of the sigstore official documentation.

        The first button "Hash an artifact" will execute a 256 bit hash on the selected file and paste it to the screen
        "Sign an artifact" will use sign the selected file and produce a 'bundle' that matches this signed file. To check the validity of the file you will need to have this bundle.
        "Verify and artifact" will verify the selected file with his correspondant bundle.
        "Check hash in reckor ledger" just checks the hash of the file with the hash recorded in rekor public ledger without the need of a bundle or if you lost this bundle.
        This is convenient if you do not want to manage bundles and want to work with hashes. Of course you should have signed the artifact before.
        """;

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Menu");
        JMenuItem licenseItem = new JMenuItem("License");

        licenseItem.addActionListener(e -> {
            // Show license info when clicked
            JOptionPane.showMessageDialog(frame,
                    licenseText,
                    "License",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        );

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
