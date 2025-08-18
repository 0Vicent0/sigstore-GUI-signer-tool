/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import dev.sigstore.KeylessVerificationException;
import dev.sigstore.KeylessVerifier;
import dev.sigstore.VerificationOptions;
import dev.sigstore.VerificationOptions.CertificateMatcher;
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

/**
 *
 * @author vicent
 */
public class VerifierAction {

    public void executeVerifying(File selectedFile, File selectedBundleFile, String email, String idProvider) throws BundleParseException, IOException, InvalidAlgorithmParameterException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException {
        HashMap<String, String> accounts = new HashMap<>();
        
        accounts.put("Google", "https://accounts.google.com");
        accounts.put("Microsoft", "https://accounts.microsoft.com");
        accounts.put("Github", "https://accounts.github.com");
        
        // Look up the issuer of the selected Id Provider
        String issuerUrl = accounts.get(idProvider);
        
          if (issuerUrl == null) {
            throw new IllegalArgumentException("Unsupported identity provider: " + idProvider);
        }
 
        // Import the artifact
        Path artifact = Paths.get(selectedFile.getAbsolutePath());

        // Import a json formatted sigstore bundle
        Path bundleFile = Paths.get(selectedBundleFile.getAbsolutePath());
        Bundle bundle = Bundle.from(bundleFile, StandardCharsets.UTF_8);

        // add certificate policy to verify the identity of the signer
        VerificationOptions options = VerificationOptions.builder().addCertificateMatchers(
                CertificateMatcher.fulcio()
                        .subjectAlternativeName(StringMatcher.string(email))
                        .issuer(StringMatcher.string(issuerUrl))
                        .build()).build();
        try {
        // verify using the sigstore public instance
        var verifier = KeylessVerifier.builder().sigstorePublicDefaults().build();
        verifier.verify(artifact, bundle, options);
        
        } catch (KeylessVerificationException e) {
             System.err.println("❌ Verification failed: " + e.getMessage());
        }
    }
}
