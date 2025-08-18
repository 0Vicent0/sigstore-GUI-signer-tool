/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import dev.sigstore.KeylessVerificationException;
import dev.sigstore.KeylessVerifier;
import dev.sigstore.VerificationOptions;
import dev.sigstore.bundle.Bundle;
import dev.sigstore.bundle.BundleParseException;
import dev.sigstore.strings.StringMatcher;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author vicent
 */
public class VerifierAction {

    public void executeVerifying(File selectedFile, File selectedBundleFile, String email, String idProvider, JFrame frame) throws BundleParseException, IOException, InvalidAlgorithmParameterException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Map IdP names to issuer URLs
            HashMap<String, String> accounts = new HashMap<>();
            
            accounts.put("Google", "https://accounts.google.com");
            accounts.put("Github", "https://github.com/login/oauth");
            accounts.put("Microsoft", "https://login.microsoftonline.com/common/v2.0");
       

            String issuerUrl = accounts.get(idProvider);
            
            if (issuerUrl == null) {
                JOptionPane.showMessageDialog(null,
                        "Unsupported identity provider: " + idProvider,
                        "Verification Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Load artifact and bundle
            Path artifact = Paths.get(selectedFile.getAbsolutePath());
            Path bundleFile = Paths.get(selectedBundleFile.getAbsolutePath());
            Bundle bundle = Bundle.from(bundleFile, StandardCharsets.UTF_8);

            // Configure verification options
            VerificationOptions options = VerificationOptions.builder()
                    .addCertificateMatchers(
                            VerificationOptions.CertificateMatcher.fulcio()
                                    .subjectAlternativeName(StringMatcher.string(email))
                                    .issuer(StringMatcher.string(issuerUrl))
                                    .build())
                    .build();

            try {
                var verifier = KeylessVerifier.builder().sigstorePublicDefaults().build();
                verifier.verify(artifact, bundle, options);

                JOptionPane.showMessageDialog(null,
                        "Verification passed for: " + selectedFile.getAbsolutePath(),
                        "File Verified",
                        JOptionPane.INFORMATION_MESSAGE);
                System.out.println("✅ Verification ok!");
            } catch (KeylessVerificationException ex) {
                JOptionPane.showMessageDialog(null,
                        "Verification failed: " + ex.getMessage(),
                        "Verification Error",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ Verification failed: " + ex);
            } catch (InvalidAlgorithmParameterException | CertificateException |
                     InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException ex) {
                JOptionPane.showMessageDialog(null,
                        "Internal verification error: " + ex.getMessage(),
                        "Verification Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

        } catch (BundleParseException | IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Failed to read bundle or artifact: " + ex.getMessage(),
                    "Verification Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
