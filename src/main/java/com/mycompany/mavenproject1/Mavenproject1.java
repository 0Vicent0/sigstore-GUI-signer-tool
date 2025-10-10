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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author vicent
 */
public class Mavenproject1 {

    public static void main(String[] args) throws KeylessSignerException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, BundleParseException {
        System.out.println("App started...");
        // Deco image
        BufferedImage originalImage = null;
        try (var is = Mavenproject1.class.getResourceAsStream("/icon.png")) {
            if (is == null) {
                throw new IOException("Resource not found: icon.png");
            }
            originalImage = ImageIO.read(is);
        } catch (IOException e) {
            System.exit(1);
        }

        // Resize image to 50x50 px
        Image scaledImage = originalImage.getScaledInstance(60, 50, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);

        // Create frame
        JFrame frame = new JFrame("Sigstore GUI Signer Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add menu
        MenuDisplayer menu = new MenuDisplayer();
        menu.drawMenu(frame);

        // Create JLabel with the icon and center it
        JLabel label = new JLabel(icon);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        // Use BorderLayout to center the label in the frame
        frame.setLayout(new BorderLayout());
        frame.add(label, BorderLayout.NORTH);

        // Create the buttons
        JButton signButton = new JButton("Sign an artifact");
        JButton verifyButton = new JButton("Verify an artifact");
        JButton hashButton = new JButton("Hash an artifact");
        JButton checkHashButton = new JButton("Check hash in rekor ledger");

        //// Hash button
        hashButton.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                JOptionPane.showMessageDialog(frame,
                        "You selected: " + selectedFile.getAbsolutePath(),
                        "Artifact Selected",
                        JOptionPane.INFORMATION_MESSAGE);

                // To do hash operation
                try (FileInputStream fis = new FileInputStream(selectedFile)) {
                    String hashResult = DigestUtils.sha256Hex(fis);

                    JTextField textField = new JTextField(hashResult);
                    textField.setEditable(false);   // user can’t change it, but can copy
                    textField.setCaretPosition(0);  // start scroll at beginning

                    JOptionPane.showMessageDialog(frame,
                            textField,
                            "Hash of: " + selectedFile.getName(),
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Error computing hash: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //// Verify hash in rekor
        checkHashButton.addActionListener((var e) -> {
            try {
                String userHash = JOptionPane.showInputDialog(frame,
                        "Enter SHA-256 hash (hex):",
                        "Check Hash in Rekor",
                        JOptionPane.QUESTION_MESSAGE);
                if (userHash == null || userHash.trim().isEmpty()) {
                    return;
                }

                String payload = "{\"hash\":\"sha256:" + userHash.trim() + "\"}";
                String url = "https://rekor.sigstore.dev/api/v1/index/retrieve";

                try (var client = HttpClients.createDefault()) {
                    var post = new HttpPost(url);
                    post.setHeader("Content-Type", "application/json");
                    post.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));

                    try (var resp = client.execute(post)) {
                        String body = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                        System.out.println("Status: " + resp.getStatusLine());
                        System.out.println("Response: " + body);

                        if (body == null || body.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(frame,
                                    "No response received from Rekor.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (body.trim().equals("[]")) {
                            JOptionPane.showMessageDialog(frame,
                                    "Hash NOT FOUND in Rekor transparency log!",
                                    "Result",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else if (body.contains("\"code\":605")) {
                            JOptionPane.showMessageDialog(frame,
                                    "Invalid hash format (sha256)!",
                                    "Invalid Input",
                                    JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame,
                                    "Hash FOUND in Rekor transparency log!",
                                    "Result",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            } catch (HeadlessException | IOException | ParseException ex) {
                JOptionPane.showMessageDialog(frame,
                        "An error occurred: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        //// Signing process
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
                    Path sigBundleFile = signeraction.executeSigningProcess(selectedFile);

                    // Inform the user
                    JOptionPane.showMessageDialog(frame,
                            "You signed: " + selectedFile.getAbsolutePath() + " Bundle has been saved: " + sigBundleFile,
                            "Artifact Signed",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (CertificateException | IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException | KeylessSignerException ex) {
                    Logger.getLogger(Mavenproject1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //// Verify
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

        // Visual features config
        frame.setLayout(new FlowLayout());
        frame.add(hashButton);
        frame.add(signButton);
        frame.add(verifyButton);
        frame.add(checkHashButton);
        frame.setSize(400, 300);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
