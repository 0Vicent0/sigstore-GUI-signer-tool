/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.mavenproject1;

import dev.sigstore.KeylessSignerException;
import dev.sigstore.bundle.BundleParseException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author vicent
 */
public class Mavenproject1 {

    public static void main(String[] args) throws KeylessSignerException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, BundleParseException {
        // Load image
        BufferedImage originalImage = null;

        try {
            originalImage = ImageIO.read(new File("/home/vicent/vict3r/VICT3R-logo_files/VICT3R-logo_files/png/VICT3R-squared-simple.png"));
        } catch (IOException e) {
            System.exit(1);
        }

        // Resize image to 50x50 px
        Image scaledImage = originalImage.getScaledInstance(60, 50, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);

        // Create frame
        JFrame frame = new JFrame("VICT3R signer tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create JLabel with the icon and center it
        JLabel label = new JLabel(icon);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        // Use BorderLayout to center the label in the frame
        frame.setLayout(new BorderLayout());
        frame.add(label, BorderLayout.NORTH);
        // File Selector Panel

        // Create the buttons
        JButton signButton = new JButton("Sign an artifact");
        JButton verifyButton = new JButton("Verify an artifact");

        /////////// Signing process
        signButton.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fc.getSelectedFile();

                    JOptionPane.showMessageDialog(frame,
                            "You selected: " + selectedFile.getAbsolutePath(),
                            "Artifact Selected",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Signing process
                    SignerAction signeraction = new SignerAction();
                    signeraction.executeSigningProcess(selectedFile);

                    // Inform the user
                    JOptionPane.showMessageDialog(frame,
                            "You signed: " + selectedFile.getAbsolutePath(),
                            "Artifact Signed",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (CertificateException | IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException | KeylessSignerException ex) {
                    Logger.getLogger(Mavenproject1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        /// Verify
       verifyButton.addActionListener(e -> {
            try {
                JFileChooser fc = new JFileChooser();

                // Choose artifact
                if (fc.showDialog(frame, "Select Artifact") != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                File selectedFile = fc.getSelectedFile();

                // Choose bundle
                if (fc.showDialog(frame, "Select Bundle") != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                File selectedBundleFile = fc.getSelectedFile();

                // Ask user for email + IdP
                JTextField emailField = new JTextField(20);
                JComboBox<String> idpCombo = new JComboBox<>(new String[]{"Google", "Github", "Microsoft"});
                JPanel panel = new JPanel(new GridLayout(2, 2));
                
                panel.add(new JLabel("Email:"));
                panel.add(emailField);
                panel.add(new JLabel("Identity Provider:"));
                panel.add(idpCombo);

                int option = JOptionPane.showConfirmDialog(frame, panel, "Enter Verification Info",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }

                String email = emailField.getText().trim();
                String idProvider = (String) idpCombo.getSelectedItem();

                // Call VerifierAction
                VerifierAction verification = new VerifierAction();
                verification.executeVerifying(selectedFile, selectedBundleFile, email, idProvider, frame);

            } catch (BundleParseException | HeadlessException | IOException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | CertificateException | InvalidKeySpecException ex) {
                Logger.getLogger(Mavenproject1.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        ////

        // Add it before making frame visible
        frame.setLayout(new FlowLayout()); // prevents full screen stretching
        frame.add(signButton);
        frame.add(verifyButton);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // center the frame on screen
        frame.setVisible(true);
    }
}
