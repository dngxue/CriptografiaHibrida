package com.mycompany.algoritmoaes;

import java.io.File;
import java.nio.file.Files;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

public class Descifrado {
    public static void ejecutar() {
        try {
            File archivoCifrado = seleccionarArchivo("Selecciona un archivo cifrado");
            
            if (archivoCifrado != null) {
                byte[] contenidoCifrado = Files.readAllBytes(archivoCifrado.toPath());
                
                File archivoVI = seleccionarArchivo("Selecciona el archivo del vector de inicialización");
                
                if (archivoVI != null) {
                    byte[] viBytes = Files.readAllBytes(archivoVI.toPath());
                    
                    if (viBytes.length != 16) {
                        throw new IllegalArgumentException("La llave debe ser de 16 bytes");
                    }
                    
                    byte[] contenidoDescrifrado = descifrarAES(viBytes, contenidoCifrado);
                    String mensajeDescifrado = new String(contenidoDescrifrado, "UTF-8");
                    
                    System.out.println("Mensaje descifrado:");
                    System.out.println(mensajeDescifrado);
                    
                }else {
                    System.out.println("No se seleccionó ningun archivo");
                }
                
            }else {
                System.out.println("No se seleccionó ningún archivo");
            }
                
        }catch (Exception e) {
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
    
    private static byte[] descifrarAES(byte[] viBytes, byte[] contenidoCifrado) throws Exception {
        SecretKeySpec llaveAES = new SecretKeySpec(viBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(viBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, llaveAES, ivSpec);
        
        return cipher.doFinal(contenidoCifrado);
    }
}
