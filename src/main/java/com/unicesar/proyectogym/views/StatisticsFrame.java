package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.HistoryProgress;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartPanel;
import com.unicesar.proyectogym.views.util.ProgresoChartFactory;


public class StatisticsFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ProgresoController controller;
    private final String document;

    public StatisticsFrame(String document) {
        this.controller = AppContext.get().newProgresoController();
        this.document = document;
        initUI();
    }

    private void initUI() {
        setTitle("Estadísticas y Evolución");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        HistoryProgress h = controller.getHistory(document);
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.",
                    "Estadísticas", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        JLabel titulo = new JLabel("Evolución de " + h.getMember().getFullName(),
                SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        add(titulo, BorderLayout.NORTH);

        if (!h.hasEvaluations()) {
            JLabel vacio = new JLabel("El usuario no tiene evaluaciones registradas.",
                    SwingConstants.CENTER);
            add(vacio, BorderLayout.CENTER);
            setSize(500, 200);
            setLocationRelativeTo(null);
            return;
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Peso", new ChartPanel(ProgresoChartFactory.peso(h.getEvaluations())));
        tabs.addTab("IMC", new ChartPanel(ProgresoChartFactory.imc(h.getEvaluations())));
        tabs.addTab("Grasa", new ChartPanel(ProgresoChartFactory.grasa(h.getEvaluations())));
        tabs.addTab("Masa muscular",
                new ChartPanel(ProgresoChartFactory.masaMuscular(h.getEvaluations())));
        add(tabs, BorderLayout.CENTER);
        add(indicatorPanel(h), BorderLayout.SOUTH);
        setSize(720, 560);
        setLocationRelativeTo(null);
    }

    private JPanel indicatorPanel(HistoryProgress h) {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 4));
        p.setBorder(BorderFactory.createTitledBorder("Indicadores"));
        p.add(new JLabel("Diferencia de peso:"));
        p.add(colored(sign(h.getDifferenceWeight()) + " kg", h.getDifferenceWeight() <= 0));
        p.add(new JLabel("Variación de IMC:"));
        p.add(colored(sign(h.getVariationImc()), h.getVariationImc() <= 0));
        p.add(new JLabel("Diferencia de grasa:"));
        p.add(colored(sign(h.getDifferenceFat()) + " %", h.getDifferenceFat() <= 0));
        p.add(new JLabel("Diferencia masa muscular:"));
        p.add(colored(sign(h.getDifferenceMuscleMass()) + " kg", h.getDifferenceMuscleMass() >= 0));
        p.add(new JLabel("Tendencia:"));
        JLabel tend = new JLabel(h.getTrend().getLabel());
        tend.setFont(tend.getFont().deriveFont(java.awt.Font.BOLD));
        p.add(tend);

        if (h.getActiveGoal() != null) {
            p.add(new JLabel("Avance hacia peso objetivo:"));
            p.add(new JLabel(h.getAdvanceWeightPorc() + " %"));
            p.add(new JLabel("Avance hacia grasa objetivo:"));
            p.add(new JLabel(h.getAdvanceFatPorc() + " %"));
        }
        return p;
    }

    private JLabel colored(String texto, boolean positivo) {
        JLabel l = new JLabel(texto);
        l.setForeground(positivo ? new Color(0, 130, 0) : new Color(190, 0, 0));
        return l;
    }

    private String sign(double v) {
        return (v > 0 ? "+" : "") + v;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StatisticsFrame("").setVisible(true));
    }
}
