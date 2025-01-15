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
import javax.swing.JOptionPane;

public class Descifrado {
    
    public static void ejecutar(String user) {
        try {
            File archivoEntrada = seleccionarArchivo("Selecciona el archivo que contiene la firma digital y el mensaje cifrado");
            
            if (archivoEntrada != null) {
                String contenidoArchivo = Files.readString(archivoEntrada.toPath(), StandardCharsets.UTF_8);

                String[] secciones = contenidoArchivo.split("\n\n");
                if (secciones.length < 2) {
                    throw new IllegalArgumentException("El archivo no tiene el formato esperado.");
                }

                // Extraer firma digital
                String firmaDigitalHex = secciones[0].replace("FIRMA DIGITAL:\n", "").trim();
                byte[] firmaDigital = hexABytes(firmaDigitalHex);

                // Extraer mensaje cifrado
                String mensajeCifradoHex = secciones[1].replace("MENSAJE CIFRADO:\n", "").trim();
                byte[] mensajeCifrado = hexABytes(mensajeCifradoHex);

                System.out.println("Firma digital: " + firmaDigitalHex);
                System.out.println("Mensaje cifrado: " + mensajeCifradoHex);

                File archivoLlavePublica = seleccionarArchivo("Selecciona el archivo de la llave pública");
                
                if (archivoLlavePublica != null) {
                    PublicKey llavePublica = cargarLlavePublica(archivoLlavePublica);
                    // --------------- DIGESTO DESCIFRADO CON FIRMA DIGITAL Y LLAVE PUBLICA  ---------------
                    byte[] digestoDescifrado = descifrarRSA(firmaDigital, llavePublica);
                    System.out.println("Digesto descifrado (SHA-256): " + bytesAHex(digestoDescifrado));
               
                    File archivoLlaveSecreta = seleccionarArchivo("Selecciona el archivo de la llave secreta");
                    
                    if (archivoLlaveSecreta != null) {
                        byte[] llaveSecretaBytes = Files.readAllBytes(archivoLlaveSecreta.toPath());
                        byte[] claveAES = derivarClaveAES(llaveSecretaBytes, 16);
                        SecretKeySpec llaveAES = new SecretKeySpec(claveAES, "AES");
                        byte[] vi = Arrays.copyOf(llaveSecretaBytes, 16);
                        // --------------- MENSAJE DESCIFRADO  ---------------
                        byte[] mensajeDescifrado = descifrarAES(mensajeCifrado, llaveAES, vi);
                        byte[] digesto = generarDigesto(mensajeDescifrado, "SHA-256");
                        System.out.println("Digesto (MENSAJE DESCIFRADO) con SHA-256: " + bytesAHex(digesto));
                        
                        if (Arrays.equals(digesto, digestoDescifrado)) {
                            // Mostrar cuadro de diálogo con el mensaje
                            JOptionPane.showMessageDialog(
                                null,
                                "Se ha verificado:\n- Confidencialidad\n- No repudio\n- Autenticación\n- Integridad",
                                "Verificación exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                null,
                                "Los digestos no coinciden. La verificación falló.",
                                "Error de verificación",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                        
                        File folder = new File("mensajesDescifrados");
                        if (!folder.exists()) {
                            folder.mkdir();
                        }

                        String filePath = "mensajesDescifrados/MensajeDescifrado_" + user + ".txt";
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                            writer.write(new String(mensajeDescifrado, StandardCharsets.UTF_8));
                            System.out.println("El mensaje descifrado se guardó correctamente en: " + filePath);
                        }
                    } else {
                        System.out.println("No se seleccionó el archivo de la llave secreta.");
                    }
                } else {
                    System.out.println("No se seleccionó el archivo de la llave pública.");
                }
            } else {
                System.out.println("No se seleccionó ningún archivo.");
            }
        } catch (Exception e) {
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
