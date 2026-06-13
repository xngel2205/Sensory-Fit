package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.views.util.ModernComponents;
import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("BIENVENIDO A SENSORY FIT");
        ModernComponents.styleLabel(title, Font.BOLD, 36f, ModernComponents.CYAN_NEON);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Control de Acceso Biométrico y Evolución Física");
        ModernComponents.styleLabel(subtitle, Font.PLAIN, 20f, ModernComponents.TEXT_WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(subtitle);

        add(centerPanel);
    }
}
