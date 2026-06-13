package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.views.panels.AsistenciasPanel;
import com.unicesar.proyectogym.views.panels.HistoryProgressPanel;
import com.unicesar.proyectogym.views.panels.IdentityValidationPanel;
import com.unicesar.proyectogym.views.panels.PhysicalEvaluationPanel;
import com.unicesar.proyectogym.views.panels.PhysicalGoalPanel;
import com.unicesar.proyectogym.views.panels.UserListPanel;
import com.unicesar.proyectogym.views.panels.WelcomePanel;
import com.unicesar.proyectogym.views.util.AnimatedBackground;
import com.unicesar.proyectogym.views.util.ModernComponents;
import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private final JPanel contentPanel;
    private final JButton btnInicio = new JButton("Inicio");
    private final JButton btnUsuarios = new JButton("Usuarios");
    private final JButton btnControl = new JButton("Control de Acceso");
    private final JButton btnAsistencias = new JButton("Asistencias");
    private final JButton btnEvaluacion = new JButton("Evaluación Física");
    private final JButton btnHistorial = new JButton("Historial y Progreso");
    private final JButton btnMetas = new JButton("Metas Físicas");
    private final JButton btnSalir = new JButton("Salir");

    public MainDashboard() {
        setTitle("Sensory Fit - Panel de Control");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1024, 680));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        AnimatedBackground background = new AnimatedBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(ModernComponents.BG_CARD);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(35, 50, 75)));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("SENSORY FIT");
        ModernComponents.styleLabel(logo, Font.BOLD, 22f, ModernComponents.CYAN_NEON);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        sidebar.add(logo);

        JButton[] buttons = {btnInicio, btnUsuarios, btnControl, btnAsistencias, btnEvaluacion, btnHistorial, btnMetas, btnSalir};
        for (JButton btn : buttons) {
            ModernComponents.styleSidebarButton(btn);
            btn.setMaximumSize(new Dimension(260, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        background.add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        background.add(contentPanel, BorderLayout.CENTER);

        btnInicio.addActionListener(e -> showPanel(new WelcomePanel()));
        btnUsuarios.addActionListener(e -> showPanel(new UserListPanel()));
        btnControl.addActionListener(e -> showPanel(new IdentityValidationPanel()));
        btnAsistencias.addActionListener(e -> showPanel(new AsistenciasPanel()));
        btnEvaluacion.addActionListener(e -> showPanel(new PhysicalEvaluationPanel()));
        btnHistorial.addActionListener(e -> showPanel(new HistoryProgressPanel()));
        btnMetas.addActionListener(e -> showPanel(new PhysicalGoalPanel()));
        btnSalir.addActionListener(e -> System.exit(0));

        showPanel(new WelcomePanel());
    }

    public void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
