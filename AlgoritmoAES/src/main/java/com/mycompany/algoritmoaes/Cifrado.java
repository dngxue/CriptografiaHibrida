package com.mycompany.algoritmoaes;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

public class Cifrado {
    public static void ejecutar() {
        try {
            File archivo = seleccionarArchivo("Selecciona un archivo txt para cifrar");
            
            if (archivo != null) {
                String contenidoArchivo = new String(Files.readAllBytes(archivo.toPath()));
                
                /* ------------------- GENERAR UN DIGESTO ------------------- */
                String digesto = generarDigesto(contenidoArchivo, "SHA-256");
                System.out.println("Digesto con SHA-256: " + digesto);
                
                /* ----------------- GENERAR FIRMA DIGITAL ----------------- */
                File archivoLlavePrivada = seleccionarArchivo("Seleccionar archivo de llave privada");
                
                if (archivoLlavePrivada != null) {
                    PrivateKey llavePrivada = cargarLlavePrivada(archivoLlavePrivada);
                    byte[] digestoCifrado = cifrarRSA(digesto.getBytes(), llavePrivada); // Firma digital
                    String firmaDigital = bytesAHex(digestoCifrado);
                    
                    System.out.println("Firma digital: " + firmaDigital);
                    
                    /* ----------------- CIFRAR MENSAJE CON AES CBC ----------------- */
                    // También dijo que se podía usar CRC, pero lo dejo en CBC
                    File archivoVI = seleccionarArchivo("Seleccionar archivo del vector de inicialización");
                    
                    if (archivoVI != null) {
                        byte[] vectorInicializacion = Files.readAllBytes(archivoVI.toPath());
                        SecretKey llaveAES = new SecretKeySpec(vectorInicializacion, 0, 16, "AES");
                        byte[] mensajeCifrado = cifrarAES(contenidoArchivo.getBytes(), llaveAES, vectorInicializacion);
                        
                        System.out.println("Mensaje cifrado (AES CBC): " + bytesAHex(mensajeCifrado));
                    }else {
                        System.out.println("No se seleccionó el archivo del vector de inicialización.");
                    }
                    
                }else {
                    System.out.println("No se seleccionó el archivo de llave privada.");
                }
                
            }else {
                System.out.println("No se seleccionó el archivo txt a cifrar.");
            }
            
        }catch (Exception e) {
            System.err.println("Error durante la ejecución: " + e.getMessage());
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
    
    private static String generarDigesto(String contenidoArchivo, String algoritmo) throws Exception {
        MessageDigest digesto = MessageDigest.getInstance(algoritmo);
        byte[] hashBytes = digesto.digest(contenidoArchivo.getBytes());
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hashBytes) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1)
                hexString.append('0');
            hexString.append(h);
        }
        return hexString.toString();
    }
    
    private static PrivateKey cargarLlavePrivada(File archivoLlavePrivada) throws Exception {
        byte[] keyBytes = Files.readAllBytes(archivoLlavePrivada.toPath());
        String llaveBase64 = new String(keyBytes);

        try {
            byte[] llaveDecodificada = Base64.getDecoder().decode(llaveBase64);
            System.out.println("La llave se decodificó correctamente.");

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(llaveDecodificada);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);

        } catch (IllegalArgumentException e) {
            System.err.println("Error al decodificar la llave: " + e.getMessage());
            throw e;
        }     
    }
    
    private static byte[] cifrarRSA(byte[] datos, PrivateKey llavePrivada) throws Exception {
        Cipher cifrar = Cipher.getInstance("RSA");
        cifrar.init(Cipher.ENCRYPT_MODE, llavePrivada);
        return cifrar.doFinal(datos);
    }
    
    private static byte[] cifrarAES(byte[] contenidoArchivo, SecretKey llaveAES, byte[] vectorInicializacion) throws Exception {
        Cipher cifrar = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(vectorInicializacion);
        cifrar.init(Cipher.ENCRYPT_MODE, llaveAES, ivSpec);
        return cifrar.doFinal(contenidoArchivo);
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
}
