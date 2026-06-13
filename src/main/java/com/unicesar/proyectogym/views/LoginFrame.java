package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.LoginController;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int MAX_INTENTOS = 3;

    private final LoginController controller;
    private int intentos = 0;

    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);

    public LoginFrame() {
        this.controller = AppContext.get().newLoginController();
        initUI();
    }

    private void initUI() {
        setTitle("Sensory Fit - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ModernComponents.BG_DARK_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("ACCESO AL SISTEMA", SwingConstants.CENTER);
        ModernComponents.styleLabel(titulo, Font.BOLD, 22f, ModernComponents.CYAN_NEON);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(titulo, c);
        c.gridwidth = 1;

        JLabel lblUser = new JLabel("Usuario:");
        ModernComponents.styleLabel(lblUser, Font.BOLD, 14f, ModernComponents.TEXT_WHITE);
        c.gridx = 0; c.gridy = 1;
        panel.add(lblUser, c);

        c.gridx = 1;
        ModernComponents.styleTextField(txtUser);
        panel.add(txtUser, c);

        JLabel lblPass = new JLabel("Contraseña:");
        ModernComponents.styleLabel(lblPass, Font.BOLD, 14f, ModernComponents.TEXT_WHITE);
        c.gridx = 0; c.gridy = 2;
        panel.add(lblPass, c);

        c.gridx = 1;
        ModernComponents.styleTextField(txtPassword);
        panel.add(txtPassword, c);

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnSalir = new JButton("Salir");
        ModernComponents.styleButton(btnIngresar, true);
        ModernComponents.styleButton(btnSalir, false);

        btnIngresar.setPreferredSize(new Dimension(120, 35));
        btnSalir.setPreferredSize(new Dimension(120, 35));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        botones.add(btnSalir);
        botones.add(btnIngresar);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        panel.add(botones, c);

        btnIngresar.addActionListener(e -> attemptLogin());
        btnSalir.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnIngresar);
        txtPassword.addActionListener(e -> attemptLogin());

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        txtUser.requestFocusInWindow();
    }

    private void attemptLogin() {
        String usuario = txtUser.getText();
        char[] password = txtPassword.getPassword();

        if (controller.login(usuario, password)) {
            java.util.Arrays.fill(password, '\0');
            openDashboard();
        } else {
            intentos++;
            txtPassword.setText("");
            if (intentos >= MAX_INTENTOS) {
                JOptionPane.showMessageDialog(this,
                        "Demasiados intentos fallidos. La aplicación se cerrará.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuario o contraseña incorrectos. Intento " + intentos
                        + " de " + MAX_INTENTOS + ".",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                txtPassword.requestFocusInWindow();
            }
        }
    }

    private void openDashboard() {
        MainDashboard dash = new MainDashboard();
        dash.setLocationRelativeTo(null);
        dash.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
