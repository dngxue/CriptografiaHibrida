package com.mycompany.algoritmoaes;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

public class Descifrado {
    public static void ejecutar() {
        try {
            File firmaDigital = seleccionarArchivo("Selecciona el archivo de firma digital");
            
            if (firmaDigital != null) {
                byte[] contenidoFirma = Files.readAllBytes(firmaDigital.toPath());
                System.out.println("Firma digital: " + bytesAHex(contenidoFirma));
                
                File archivoLlavePublica = seleccionarArchivo("Selecciona el archivo de la llave publica");
               
                if (archivoLlavePublica != null) {
                    PublicKey llavePublica = cargarLlavePublica(archivoLlavePublica);
                     byte[] digestoDescifrado = descifrarRSA(contenidoFirma, llavePublica);
                    System.out.println("Digesto con SHA-256: " + bytesAHex(digestoDescifrado));
                    /* Para verificar que los digestos coinciden. Debemos abrir el mensaje original */
                    // verificarFirmaDigital(mensaje, contenidoFirma, llavePublica);                     
                }else {
                    System.out.println("No se selecciono ningun archivo");
                }
                
            }else {
                System.out.println("No se selecciono ningún archivo");
            }
                
        }catch (Exception e) {
            System.err.println("Error durante la ejecucion: " + e.getMessage());
        }
    }
    
    private static File seleccionarArchivo(String titulo) {
        JFileChooser archivo = new JFileChooser();
        archivo.setDialogTitle(titulo);
        int resultado = archivo.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return archivo.getSelectedFile();
        }
        return null;
    }
    
    public static void verificarFirmaDigital(byte[] mensajeOriginal, byte[] firmaDigital, PublicKey llavePublica) throws Exception {
        byte[] digestoDescifrado = descifrarRSA(firmaDigital, llavePublica);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digestoMensajeOriginal = md.digest(mensajeOriginal);
        System.out.println("Digesto mensaje original: " + bytesAHex(digestoMensajeOriginal));
        
        if (Arrays.equals(digestoDescifrado, digestoMensajeOriginal)) {
            System.out.println("La firma es valida.");
        } else {
            System.out.println("La firma no es valida.");
        }
    }
    
    private static byte[] descifrarRSA(byte[] datos, PublicKey llavePublica) throws Exception {
        Cipher cifrar = Cipher.getInstance("RSA");
        cifrar.init(Cipher.DECRYPT_MODE, llavePublica);
        return cifrar.doFinal(datos);
    }
    
    private static PublicKey cargarLlavePublica(File archivoLlavePublica) throws Exception {
        byte[] keyBytes = Files.readAllBytes(archivoLlavePublica.toPath());
        String llaveBase64 = new String(keyBytes);

        try {
            byte[] llaveDecodificada = Base64.getDecoder().decode(llaveBase64);
            // System.out.println("La llave se decodifico correctamente.");

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(llaveDecodificada);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            
            return keyFactory.generatePublic(keySpec);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error al decodificar la llave: " + e.getMessage());
            throw e;
        }     
    }
    
    private static String bytesAHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b: bytes) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1)
                hexString.append('0');
            hexString.append(h);
        }
        return hexString.toString();
    }
    
    private static byte[] descifrarAES(byte[] viBytes, byte[] contenidoCifrado) throws Exception {
        SecretKeySpec llaveAES = new SecretKeySpec(viBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(viBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, llaveAES, ivSpec);
        
        return cipher.doFinal(contenidoCifrado);
    }
}
