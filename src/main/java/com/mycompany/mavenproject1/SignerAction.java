package com.mycompany.mavenproject1;

import dev.sigstore.KeylessSigner;
import dev.sigstore.KeylessSignerException;
import dev.sigstore.bundle.Bundle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

class SignerAction {
    public Path executeSigningProcess(File selectedFile) throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, KeylessSignerException {       
        
        // Signing actions
        Path artifact = Paths.get(selectedFile.getAbsolutePath());
        
        // Sign using the sigstore public instance
        var signer = KeylessSigner.builder().sigstorePublicDefaults().build();
        Bundle result = signer.signFile(artifact);
        
        // Sigstore bundle format (serialized as <artifact>.sigstore.json)
        String bundleJson = result.toJson();
        
        // Save the bundle to a file
        Path sigBundleFile = Paths.get(artifact.toString() + ".sigstore.json");
        Files.write(sigBundleFile, bundleJson.getBytes());
        
        // Log
        System.out.println("Signature bundle saved to: " + sigBundleFile);
        
        return sigBundleFile;
    }
}
