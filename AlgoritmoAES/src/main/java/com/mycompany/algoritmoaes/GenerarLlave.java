package com.mycompany.algoritmoaes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class GenerarLlave {
    public void generateAndSaveKeys(String name) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Tamaño de la clave (2048 bits)
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        saveKeyToFile(name + "_publicKey", publicKey.getEncoded());
        saveKeyToFile(name + "_privateKey", privateKey.getEncoded());
    }
    
    private void saveKeyToFile(String fileName, byte[] keyBytes) throws IOException {
        String keyBase64 = Base64.getEncoder().encodeToString(keyBytes);
        String keyPEM = "";
        
        if (fileName.contains("privateKey")) {
            keyPEM = "-----BEGIN PRIVATE KEY-----\n" + keyBase64 + "\n-----END PRIVATE KEY-----\n";
        } else if (fileName.contains("publicKey")) {
            keyPEM = "-----BEGIN PUBLIC KEY-----\n" + keyBase64 + "\n-----END PUBLIC KEY-----\n";
        }
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(keyPEM.getBytes());
        }
    }
}