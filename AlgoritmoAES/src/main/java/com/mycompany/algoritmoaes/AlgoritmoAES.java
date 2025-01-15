package com.mycompany.algoritmoaes;

import java.math.BigInteger;
import java.util.Scanner;

public class AlgoritmoAES {
    public static void main(String[] args) throws Exception {
        Window.main(args);
    }
    /*public static BigInteger[] parametros;
    public static BigInteger p;
    public static BigInteger g;
    public static BigInteger llavePrivada;
    public static BigInteger llaveParcial;
    public static String titulo;
    
    public static void main(String[] args) throws Exception {
        int opcion;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escribe tu nombre");
        String nombre = scanner.nextLine();
        
        do {
            System.out.println("Diffie Hellman");
            System.out.println("1. Generar parametros y llave privada");
            System.out.println("2. Generar llave parcial");
            System.out.println("3. Generar secreto");
            System.out.println("4. Salir\n");
            System.out.println("Seleccione una opción: ");
            opcion = scanner.nextInt();
            
            switch (opcion) {
                // ------------------- GENERAR PARAMETROS  ------------------- 
                case 1:
                    DiffieHellman.generarParametros(nombre);
                    break;
                // ------------------- GENERAR LLAVE PARCIAL  -------------------
                case 2:
                    parametros = DiffieHellman.leerParametros();
                    p = parametros[0];
                    g = parametros[1];
                    titulo = "Seleccionar archivo de llave privada";
                    llavePrivada = DiffieHellman.leerLlave(titulo);
                    llaveParcial = DiffieHellman.generarLlaveParcial(p, g, llavePrivada, nombre);
                    System.out.println("Llave parcial: " + llaveParcial);
                    break;
                // ------------------- GENERAR SECRETO COMPARTIDO  -------------------
                case 3:
                    parametros = DiffieHellman.leerParametros();
                    p = parametros[0];
                    titulo = "Seleccionar archivo de llave privada";
                    llavePrivada = DiffieHellman.leerLlave(titulo);
                    titulo = "Seleccionar archivo de llave compartida";
                    llaveParcial = DiffieHellman.leerLlave(titulo);
                    BigInteger llaveSecreta = DiffieHellman.calcularLlaveCompartida(p, llaveParcial, llavePrivada, nombre);
                    String secretKey = llaveSecreta.toString(16);
                    System.out.println("Secreto compartido: " + llaveSecreta);
                    System.out.println("Secreto compartido a string: " + secretKey);
                    break;
                case 4:
                    break;
            }
        }while (opcion != 4);
    }*/
}
