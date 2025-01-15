package com.mycompany.algoritmoaes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
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
    public static void ejecutar(String user) {
        try {
            File firmaDigital = seleccionarArchivo("Selecciona el archivo de firma digital");
            
            if (firmaDigital != null) {
                byte[] contenidoFirma = Files.readAllBytes(firmaDigital.toPath());
                System.out.println("Firma digital: " + bytesAHex(contenidoFirma));
                
                File archivoLlavePublica = seleccionarArchivo("Selecciona el archivo de la llave publica");
               
                if (archivoLlavePublica != null) {
                    PublicKey llavePublica = cargarLlavePublica(archivoLlavePublica);
                    /* ------------------- GENERAR EL DIGESTO CON FIRMA DIGITAL Y LLAVE PÚBLICA  ------------------- */
                    byte[] digestoDescifrado = descifrarRSA(contenidoFirma, llavePublica);
                    System.out.println("Digesto con SHA-256: " + bytesAHex(digestoDescifrado));
                    /* Para verificar que los digestos coinciden. Debemos abrir el mensaje original */
                    // verificarFirmaDigital(mensaje, contenidoFirma, llavePublica);
                    
                    File archivoCifrado = seleccionarArchivo("Selecciona el archivo cifrado");
                    File archivoLlaveSecreta = seleccionarArchivo("Selecciona el archivo de la llave secreta");
                    
                    if (archivoCifrado != null && archivoLlaveSecreta != null) {
                        byte[] contenido = Files.readAllBytes(archivoCifrado.toPath());
                        String contenidoCifradoHexStr = new String(contenido, StandardCharsets.UTF_8);
                        byte[] contenidoCifrado = hexABytes(contenidoCifradoHexStr);
                        System.out.println("Contenido cifrado: " + contenidoCifradoHexStr);
                        
                        byte[] llaveSecretaBytes = Files.readAllBytes(archivoLlaveSecreta.toPath());
                        byte[] claveAES = derivarClaveAES(llaveSecretaBytes, 16);
                        SecretKeySpec llaveAES = new SecretKeySpec(claveAES, "AES");
                        System.out.println(Arrays.toString(claveAES));
                        byte[] vi = Arrays.copyOf(llaveSecretaBytes, 16);
                        
                        byte[] mensajeDescifrado = descifrarAES(contenidoCifrado, llaveAES, vi);
                        
                        // System.out.println("Contenido descifrado:");
                        // System.out.println(new String(mensajeDescifrado, StandardCharsets.UTF_8));

                        File folder = new File("mensajesDescifrados");
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        
                        String filePath = "mensajesDescifrados/MensajeDescifrado_" + user + ".txt";
                        
                        String mensajeDescifradoStr = new String(mensajeDescifrado, StandardCharsets.UTF_8);
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                            writer.write(mensajeDescifradoStr);
                            System.out.println("El mensaje descifrado se guardó correctamente en: " + filePath);
                            writer.flush();
                        }
                    }else {
                        System.out.println("No se selecciono ningun archivo");
                    }
                    
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
    
    private static byte[] descifrarAES(byte[] contenidoCifrado, SecretKeySpec llaveAES, byte[] viBytes) throws Exception {
        IvParameterSpec ivSpec = new IvParameterSpec(viBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, llaveAES, ivSpec);
        
        return cipher.doFinal(contenidoCifrado);
    }
    
    private static byte[] derivarClaveAES(byte[] llaveSecreta, int longitudClave) throws Exception {
        MessageDigest digesto = MessageDigest.getInstance("SHA-256");
        byte[] hash = digesto.digest(llaveSecreta);
        return Arrays.copyOf(hash, longitudClave);
    }
    // Método para convertir de Hex a bytes
public static byte[] hexABytes(String hex) {
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                           + Character.digit(hex.charAt(i+1), 16));
    }
    return data;
}

}
