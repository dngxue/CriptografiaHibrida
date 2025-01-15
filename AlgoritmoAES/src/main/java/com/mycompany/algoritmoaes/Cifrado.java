package com.mycompany.algoritmoaes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

public class Cifrado {
    public static void ejecutar(String user) {
        try {
            File archivo = seleccionarArchivo("Selecciona un archivo txt para cifrar");
            
            if (archivo != null) {
                byte[] contenidoArchivo = Files.readAllBytes(archivo.toPath());
                
                /* ------------------- GENERAR UN DIGESTO ------------------- */
                byte[] digesto = generarDigesto(contenidoArchivo, "SHA-256");
                System.out.println("Digesto con SHA-256: " + bytesAHex(digesto));
                
                /* ----------------- GENERAR FIRMA DIGITAL ----------------- */
                File archivoLlavePrivada = seleccionarArchivo("Seleccionar archivo de llave privada");
                
                if (archivoLlavePrivada != null) {
                    PrivateKey llavePrivada = cargarLlavePrivada(archivoLlavePrivada);
                    byte[] digestoCifrado = cifrarRSA(digesto, llavePrivada); // Firma digital
                    String firmaDigital = bytesAHex(digestoCifrado);
                    
                    System.out.println("Firma digital: " + firmaDigital);
                    
                    // guardarFirmaDigital(digestoCifrado);
                    
                    /* ----------------- CIFRAR MENSAJE CON AES CBC ----------------- */
                    File archivoVI = seleccionarArchivo("Seleccionar archivo de la llave secreta");
                    
                    if (archivoVI != null) {
                        byte[] llaveSecretaBytes = Files.readAllBytes(archivoVI.toPath());
                        
                        byte[] claveAES = derivarClaveAES(llaveSecretaBytes, 16);
                        SecretKeySpec llaveAES = new SecretKeySpec(claveAES, "AES");
                        System.out.println(Arrays.toString(claveAES));
                        byte[] vi = Arrays.copyOf(llaveSecretaBytes, 16);
                        
                        byte[] mensajeCifrado = cifrarAES(contenidoArchivo, llaveAES, vi);
                        
                        File folder = new File("mensajesCifrados");
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        
                        String filePath = "mensajesCifrados/MensajeCifrado_" + user + ".txt";
                        
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                            writer.write("FIRMA DIGITAL:\n");
                            writer.write(firmaDigital);
                            writer.write("\n\nMENSAJE CIFRADO:\n");
                            writer.write(bytesAHex(mensajeCifrado));
                            System.out.println("El mensaje cifrado se guardó correctamente en: " + filePath);
                        }
                        System.out.println("Mensaje cifrado (AES CBC): " + bytesAHex(mensajeCifrado));
                    }else {
                        System.out.println("No se selecciono el archivo del vector de inicialización.");
                    }
                    
                }else {
                    System.out.println("No se selecciono el archivo de llave privada.");
                }
                
            }else {
                System.out.println("No se selecciono el archivo txt a cifrar.");
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
    
    private static byte[] generarDigesto(byte[] contenidoArchivo, String algoritmo) throws Exception {
        MessageDigest mensajeDigesto = MessageDigest.getInstance(algoritmo);
        byte[] digesto = mensajeDigesto.digest(contenidoArchivo);
        
        return digesto;
    }
    
    private static PrivateKey cargarLlavePrivada(File archivoLlavePrivada) throws Exception {
        byte[] keyBytes = Files.readAllBytes(archivoLlavePrivada.toPath());
        String llaveBase64 = new String(keyBytes);

        try {
            byte[] llaveDecodificada = Base64.getDecoder().decode(llaveBase64);
            // System.out.println("La llave se decodifico correctamente.");

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
    
    private static void guardarFirmaDigital(byte[] firmaDigital) throws Exception {
        String nombreArchivo = "Firma_digital";

        try (FileOutputStream fos = new FileOutputStream(nombreArchivo)) {
            fos.write(firmaDigital);
        }
    }
    
    private static byte[] derivarClaveAES(byte[] llaveSecreta, int longitudClave) throws Exception {
        MessageDigest digesto = MessageDigest.getInstance("SHA-256");
        byte[] hash = digesto.digest(llaveSecreta);
        return Arrays.copyOf(hash, longitudClave);
    }
}
