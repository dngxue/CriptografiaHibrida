package com.mycompany.algoritmoaes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class DiffieHellman {
    private static final int BIT_LENGTH = 2048;

    public static void generarParametros(String nombre) throws Exception {
        KeyPairGenerator generarLlaves = KeyPairGenerator.getInstance("DH");
        generarLlaves.initialize(BIT_LENGTH);
        KeyPair llaves = generarLlaves.generateKeyPair();

        DHPrivateKey llavePrivada = (DHPrivateKey) llaves.getPrivate();
        DHPublicKey llavePublica = (DHPublicKey) llaves.getPublic();

        BigInteger p = llavePrivada.getParams().getP();
        BigInteger g = llavePublica.getParams().getG();
        /* Archivo con parámetros p y g */
        String nombreArchivo = "parametros.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(p.toString(16) + "\n");
            writer.write(g.toString(16));
        }
        /* Archivo con llave privada */
        String archivoLlavePrivada = "privateKey_" + nombre + ".txt";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(archivoLlavePrivada))) {
            writer.write(llavePrivada.getX().toString(16));
        }

        System.out.println("Parametros generados correctamente.");
    }
  
    public static BigInteger[] leerParametros() throws Exception {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo de parametros");
        int seleccion = chooser.showOpenDialog(frame);
        
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            String archivo = chooser.getSelectedFile().getAbsolutePath();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String pStr = reader.readLine();
                String gStr = reader.readLine();

                if (pStr == null || gStr == null) {
                    throw new IllegalArgumentException("El archivo no contiene parametros validos.");
                }

                BigInteger p = new BigInteger(pStr, 16);
                BigInteger g = new BigInteger(gStr, 16);

                return new BigInteger[]{p, g};
            }catch (Exception e) {
                throw new Exception("Error al leer el archivo de parametros: " + e.getMessage(), e);
            }
        } else {
            throw new Exception("Seleccion de archivo cancelada por el usuario.");
        }
    }

    public static BigInteger leerLlave(String titulo) throws Exception {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(titulo);
        int seleccion = chooser.showOpenDialog(frame);

        if(seleccion == JFileChooser.APPROVE_OPTION) {
            String archivo = chooser.getSelectedFile().getAbsolutePath();

            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String llaveStr = reader.readLine();
                BigInteger llave = new BigInteger(llaveStr, 16);
                return llave;
            }
        }
        return null;
    }

    public static BigInteger generarLlaveParcial(BigInteger p, BigInteger g, BigInteger llavePrivada, String nombre) throws IOException {
        /* Archivo con llave parcial */
        String nombreArchivo = "llaveparcial_" + nombre + ".txt";
        BigInteger llaveParcial = g.modPow(llavePrivada, p);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(llaveParcial.toString(16));
        }
        return g.modPow(llavePrivada, p);
    }

    public static BigInteger calcularLlaveCompartida(BigInteger p, BigInteger llaveParcial, BigInteger llavePrivada, String nombre) throws IOException {
        String carpeta = "llaveSecreta";

        File directorio = new File(carpeta);
        if (!directorio.exists()) {
            if (!directorio.mkdir()) {
                System.err.println("No se pudo crear la carpeta: " + carpeta);
                return null;
            }
        }
        
        /* Archivo con llave compartida */
        String nombreArchivo = carpeta + File.separator + "llaveSecreta" + nombre + ".txt";
        BigInteger llaveCompartida = llaveParcial.modPow(llavePrivada, p);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(llaveCompartida.toString(16));
        }
        
        return llaveParcial.modPow(llavePrivada, p);
    }
}
