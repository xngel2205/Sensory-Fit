package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.ModernComponents;
import com.unicesar.proyectogym.views.util.ProgresoChartFactory;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.jfree.chart.ChartPanel;

public class StatisticsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ProgresoController controller;
    private final String document;

    public StatisticsPanel(String document) {
        this.controller = AppContext.get().newProgresoController();
        this.document = document;
        initUI();
    }

    private void initUI() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        HistoryProgress h = controller.getHistory(document);
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.", "Estadísticas", JOptionPane.WARNING_MESSAGE);
            closePanel();
            return;
        }

        JLabel titulo = new JLabel("EVOLUCIÓN DE " + h.getMember().getFullName().toUpperCase());
        ModernComponents.styleLabel(titulo, Font.BOLD, 24f, ModernComponents.CYAN_NEON);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        if (!h.hasEvaluations()) {
            JLabel vacio = new JLabel("El usuario no tiene evaluaciones registradas.");
            ModernComponents.styleLabel(vacio, Font.BOLD, 18f, ModernComponents.TEXT_GRAY);
            vacio.setHorizontalAlignment(SwingConstants.CENTER);
            add(vacio, BorderLayout.CENTER);

            JButton btnVolver = new JButton("Volver");
            ModernComponents.styleButton(btnVolver, false);
            btnVolver.addActionListener(e -> closePanel());
            JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
            sur.setOpaque(false);
            sur.add(btnVolver);
            add(sur, BorderLayout.SOUTH);
            return;
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ModernComponents.BG_CARD);
        tabs.setForeground(ModernComponents.TEXT_WHITE);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        addChartTab(tabs, "Peso", new ChartPanel(ProgresoChartFactory.peso(h.getEvaluations())));
        addChartTab(tabs, "IMC", new ChartPanel(ProgresoChartFactory.imc(h.getEvaluations())));
        addChartTab(tabs, "Grasa", new ChartPanel(ProgresoChartFactory.grasa(h.getEvaluations())));
        addChartTab(tabs, "Masa muscular", new ChartPanel(ProgresoChartFactory.masaMuscular(h.getEvaluations())));

        add(tabs, BorderLayout.CENTER);

        JPanel mainSouth = new JPanel(new BorderLayout(10, 10));
        mainSouth.setOpaque(false);
        mainSouth.add(indicatorPanel(h), BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver");
        ModernComponents.styleButton(btnVolver, false);
        btnVolver.setPreferredSize(new Dimension(140, 40));
        btnVolver.addActionListener(e -> closePanel());

        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botPanel.setOpaque(false);
        botPanel.add(btnVolver);
        mainSouth.add(botPanel, BorderLayout.SOUTH);

        add(mainSouth, BorderLayout.SOUTH);
    }

    private void addChartTab(JTabbedPane tabs, String title, ChartPanel chartPanel) {
        chartPanel.setBackground(ModernComponents.BG_DARK_BLUE);
        chartPanel.setOpaque(false);
        tabs.addTab(title, chartPanel);
    }

    private JPanel indicatorPanel(HistoryProgress h) {
        JPanel p = new JPanel(new GridLayout(0, 2, 12, 8));
        ModernComponents.stylePanel(p, true);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(35, 50, 75)),
                        "Indicadores de Rendimiento",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        ModernComponents.CYAN_NEON
                ),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        addIndicatorRow(p, "Diferencia de peso:", colored(sign(h.getDifferenceWeight()) + " kg", h.getDifferenceWeight() <= 0));
        addIndicatorRow(p, "Variación de IMC:", colored(sign(h.getVariationImc()), h.getVariationImc() <= 0));
        addIndicatorRow(p, "Diferencia de grasa:", colored(sign(h.getDifferenceFat()) + " %", h.getDifferenceFat() <= 0));
        addIndicatorRow(p, "Diferencia masa muscular:", colored(sign(h.getDifferenceMuscleMass()) + " kg", h.getDifferenceMuscleMass() >= 0));

        JLabel lblTend = new JLabel("Tendencia:");
        ModernComponents.styleLabel(lblTend, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        p.add(lblTend);

        JLabel tend = new JLabel(h.getTrend().getLabel());
        ModernComponents.styleLabel(tend, Font.BOLD, 13f, ModernComponents.CYAN_NEON);
        p.add(tend);

        if (h.getActiveGoal() != null) {
            JLabel lblGoalW = new JLabel("Avance hacia peso objetivo:");
            ModernComponents.styleLabel(lblGoalW, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
            p.add(lblGoalW);

            JLabel valGoalW = new JLabel(h.getAdvanceWeightPorc() + " %");
            ModernComponents.styleLabel(valGoalW, Font.BOLD, 13f, ModernComponents.BLUE_NEON);
            p.add(valGoalW);

            JLabel lblGoalF = new JLabel("Avance hacia grasa objetivo:");
            ModernComponents.styleLabel(lblGoalF, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
            p.add(lblGoalF);

            JLabel valGoalF = new JLabel(h.getAdvanceFatPorc() + " %");
            ModernComponents.styleLabel(valGoalF, Font.BOLD, 13f, ModernComponents.BLUE_NEON);
            p.add(valGoalF);
        }
        return p;
    }

    private void addIndicatorRow(JPanel panel, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        ModernComponents.styleLabel(label, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        panel.add(label);
        panel.add(valueLabel);
    }

    private JLabel colored(String texto, boolean positivo) {
        JLabel l = new JLabel(texto);
        ModernComponents.styleLabel(l, Font.BOLD, 13f, positivo ? ModernComponents.CYAN_NEON : new Color(255, 100, 100));
        return l;
    }

    private String sign(double v) {
        return (v > 0 ? "+" : "") + v;
    }

    private void closePanel() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new HistoryProgressPanel(document));
        }
    }
}
