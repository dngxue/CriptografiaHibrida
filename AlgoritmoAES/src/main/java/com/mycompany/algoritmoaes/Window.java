package com.mycompany.algoritmoaes;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends javax.swing.JFrame {
    public static String nombreUsuario;
    
    public Window() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGenerarParametro = new javax.swing.JButton();
        btnSecretoCompartido = new javax.swing.JButton();
        btnLlaveParcial = new javax.swing.JButton();
        btnCifrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnGenerarParametro.setText("Generar parámetros");
        btnGenerarParametro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarParametroActionPerformed(evt);
            }
        });

        btnSecretoCompartido.setText("Generar secreto compartido");
        btnSecretoCompartido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecretoCompartidoActionPerformed(evt);
            }
        });

        btnLlaveParcial.setText("Generar llave parcial");
        btnLlaveParcial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLlaveParcialActionPerformed(evt);
            }
        });

        btnCifrar.setText("Cifrar");
        btnCifrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCifrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCifrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSecretoCompartido, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnLlaveParcial, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                        .addComponent(btnGenerarParametro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(89, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(btnGenerarParametro, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLlaveParcial, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSecretoCompartido, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCifrar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarParametroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarParametroActionPerformed
        JFrame ventanaInicio = new JFrame("Ingresa tu nombre");
        
        ventanaInicio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaInicio.setSize(300, 200);
        ventanaInicio.setLocationRelativeTo(null);
        ventanaInicio.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel panelInicio = new JPanel();
        panelInicio.setLayout(new GridLayout(2,1,10,10));
        JLabel lblInicio = new JLabel("Ingresa tu nombre de usuario:");
        JTextField campoNombre = new JTextField();

        panelInicio.add(lblInicio);
        panelInicio.add(campoNombre);
        
        JButton btnConectar = new JButton("Conectar");
        btnConectar.setPreferredSize(new Dimension(100, 40));

        btnConectar.addActionListener(e -> {
            nombreUsuario = campoNombre.getText().trim();
            if (!nombreUsuario.isEmpty()) {
                try {
                    DiffieHellman.generarParametros(nombreUsuario);
                    ventanaInicio.setVisible(false);
                } catch (Exception ex) {
                    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(ventanaInicio, "Por favor, ingresa un nombre de usuario válido.");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        ventanaInicio.add(panelInicio, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        ventanaInicio.add(btnConectar, gbc);
        ventanaInicio.setVisible(true);
    }//GEN-LAST:event_btnGenerarParametroActionPerformed

    private void btnSecretoCompartidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecretoCompartidoActionPerformed
        try {
            BigInteger[] parametros = DiffieHellman.leerParametros();
            BigInteger p = parametros[0];
            
            String titulo = "Seleccionar archivo de llave privada";
            BigInteger llavePrivada = DiffieHellman.leerLlave(titulo);
            titulo = "Seleccionar archivo de llave compartida";
            BigInteger llaveParcial = DiffieHellman.leerLlave(titulo);
            
            BigInteger llaveSecreta = DiffieHellman.calcularLlaveCompartida(p, llaveParcial, llavePrivada, nombreUsuario);
            System.out.println("Secreto compartido: " + llaveSecreta);
        } catch (Exception ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_btnSecretoCompartidoActionPerformed

    private void btnLlaveParcialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLlaveParcialActionPerformed
        try {
            BigInteger[] parametros = DiffieHellman.leerParametros();
            BigInteger p = parametros[0];
            BigInteger g = parametros[1];
            BigInteger llavePrivada = DiffieHellman.leerLlave("Selecciona la llave privada");
            BigInteger llaveParcial = DiffieHellman.generarLlaveParcial(p, g, llavePrivada, nombreUsuario);
            
            System.out.println("Llave parcial: " + llaveParcial);
        } catch (Exception ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLlaveParcialActionPerformed

    private void btnCifrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCifrarActionPerformed
        Cifrado.ejecutar();
    }//GEN-LAST:event_btnCifrarActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Window().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCifrar;
    private javax.swing.JButton btnGenerarParametro;
    private javax.swing.JButton btnLlaveParcial;
    private javax.swing.JButton btnSecretoCompartido;
    // End of variables declaration//GEN-END:variables
}
