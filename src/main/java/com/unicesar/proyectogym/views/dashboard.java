


 
package com.unicesar.proyectogym.views;




 
public class dashboard extends javax.swing.JFrame {

    

 
    public dashboard() {
        initComponents();
        agregarMenuListado();
        agregarMenusProgreso();
    }

    



 
    private void agregarMenuListado() {
        javax.swing.JMenuItem itemListado = new javax.swing.JMenuItem("Listado de usuarios");
        itemListado.addActionListener(evt -> {
            UserListFrame lista = new UserListFrame();
            lista.setVisible(true);
        });
        jMenu1.add(itemListado);
    }

    


 
    private void agregarMenusProgreso() {
        javax.swing.JMenu menuProgreso = new javax.swing.JMenu("Progreso Físico");

        javax.swing.JMenuItem itemEval = new javax.swing.JMenuItem("Evaluación física");
        itemEval.addActionListener(evt -> new PhysicalEvaluationFrame().setVisible(true));
        menuProgreso.add(itemEval);

        javax.swing.JMenuItem itemHist = new javax.swing.JMenuItem("Historial de progreso");
        itemHist.addActionListener(evt -> new HistoryProgressFrame().setVisible(true));
        menuProgreso.add(itemHist);

        javax.swing.JMenuItem itemMetas = new javax.swing.JMenuItem("Metas físicas");
        itemMetas.addActionListener(evt -> new PhysicalGoalFrame().setVisible(true));
        menuProgreso.add(itemMetas);

        javax.swing.JMenu menuAsistencias = new javax.swing.JMenu("Asistencias");
        javax.swing.JMenuItem itemAsist = new javax.swing.JMenuItem("Ver asistencias");
        itemAsist.addActionListener(evt -> new AsistenciasFrame().setVisible(true));
        menuAsistencias.add(itemAsist);

        int idx = Math.max(0, jMenuBar1.getComponentCount() - 1);
        jMenuBar1.add(menuProgreso, idx);
        jMenuBar1.add(menuAsistencias, idx + 1);

        jMenu3.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.exit(0);
            }
        });
    }

    



 
    @SuppressWarnings("unchecked")
    
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu1.setText("Usuarios");

        jMenuItem1.setText("Gestión de usuarios");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Membresias");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Inventario");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Salir");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1006, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 461, Short.MAX_VALUE)
        );

        pack();
    }

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        
        IdentityValidationFrame validacion = new IdentityValidationFrame();
        validacion.setVisible(true);
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        
        RegisterUserFrame registro = new RegisterUserFrame();
        registro.setVisible(true);
    }

    

 
    public static void main(String args[]) {
         
        
        

 
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        

         
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new dashboard().setVisible(true);
            }
        });
    }

    
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    
}
