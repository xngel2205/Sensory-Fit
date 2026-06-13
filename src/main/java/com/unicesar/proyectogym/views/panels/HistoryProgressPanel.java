package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.service.report.ReporteProgresoService;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.DateField;
import com.unicesar.proyectogym.views.util.EvaluacionTableModel;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HistoryProgressPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ProgresoController controller;
    private final ReporteProgresoService reporteService = new ReporteProgresoService();

    private final JTextField txtDocumento = new JTextField(14);
    private final DateField txtDesde = new DateField();
    private final DateField txtHasta = new DateField();
    private final JLabel lblNombre = new JLabel("—");
    private final EvaluacionTableModel tableModel = new EvaluacionTableModel();
    private final JTable tabla = new JTable(tableModel);

    public HistoryProgressPanel() {
        this.controller = AppContext.get().newProgresoController();
        initUI();
    }

    public HistoryProgressPanel(String document) {
        this();
        txtDocumento.setText(document);
        load();
    }

    private void initUI() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setOpaque(false);

        JLabel titulo = new JLabel("HISTORIAL DE PROGRESO");
        ModernComponents.styleLabel(titulo, Font.BOLD, 24f, ModernComponents.CYAN_NEON);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(titulo, BorderLayout.NORTH);

        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        ModernComponents.stylePanel(filtersPanel, true);

        JLabel lblDoc = new JLabel("Documento:");
        ModernComponents.styleLabel(lblDoc, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        filtersPanel.add(lblDoc);

        ModernComponents.styleTextField(txtDocumento);
        filtersPanel.add(txtDocumento);

        JButton btnBuscar = new JButton("Buscar");
        ModernComponents.styleButton(btnBuscar, true);
        btnBuscar.addActionListener(e -> load());
        filtersPanel.add(btnBuscar);

        JLabel lblDesde = new JLabel("Desde:");
        ModernComponents.styleLabel(lblDesde, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        filtersPanel.add(lblDesde);

        ModernComponents.styleTextField(txtDesde);
        filtersPanel.add(txtDesde);

        JLabel lblHasta = new JLabel("Hasta:");
        ModernComponents.styleLabel(lblHasta, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        filtersPanel.add(lblHasta);

        ModernComponents.styleTextField(txtHasta);
        filtersPanel.add(txtHasta);

        JButton btnFiltrar = new JButton("Filtrar");
        ModernComponents.styleButton(btnFiltrar, false);
        btnFiltrar.addActionListener(e -> load());
        filtersPanel.add(btnFiltrar);

        northPanel.add(filtersPanel, BorderLayout.CENTER);

        ModernComponents.styleLabel(lblNombre, Font.BOLD, 14f, ModernComponents.TEXT_WHITE);
        lblNombre.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        northPanel.add(lblNombre, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(28);
        ModernComponents.styleTable(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        ModernComponents.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        JButton btnNueva = new JButton("Nueva evaluación");
        JButton btnEstadisticas = new JButton("Ver estadísticas");
        JButton btnPdf = new JButton("Exportar PDF");
        JButton btnCerrar = new JButton("Cerrar");

        ModernComponents.styleButton(btnNueva, true);
        ModernComponents.styleButton(btnEstadisticas, false);
        ModernComponents.styleButton(btnPdf, false);
        ModernComponents.styleButton(btnCerrar, false);

        btnNueva.addActionListener(e -> openNewEvaluation());
        btnEstadisticas.addActionListener(e -> openStatistics());
        btnPdf.addActionListener(e -> exportPdf());
        btnCerrar.addActionListener(e -> closePanel());

        JPanel surPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        surPanel.setOpaque(false);
        surPanel.add(btnNueva);
        surPanel.add(btnEstadisticas);
        surPanel.add(btnPdf);
        surPanel.add(btnCerrar);
        add(surPanel, BorderLayout.SOUTH);
    }

    private String doc() {
        return txtDocumento.getText().trim();
    }

    private void load() {
        String document = doc();
        if (document.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un documento.", "Historial", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        var member = controller.searchUser(document);
        if (member.isEmpty()) {
            lblNombre.setText("No existe un usuario con ese documento.");
            lblNombre.setForeground(new Color(255, 100, 100));
            tableModel.setData(java.util.Collections.emptyList());
            return;
        }
        lblNombre.setText("Usuario: " + member.get().getFullName());
        lblNombre.setForeground(ModernComponents.CYAN_NEON);

        java.time.LocalDate from = null;
        java.time.LocalDate until = null;
        try {
            from = txtDesde.getDate();
            until = txtHasta.getDate();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.setData(controller.rangeEvaluations(document, from, until));
    }

    private void openNewEvaluation() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new PhysicalEvaluationPanel(doc()));
        }
    }

    private void openStatistics() {
        if (doc().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un documento.", "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new StatisticsPanel(doc()));
        }
    }

    private void exportPdf() {
        String document = doc();
        if (document.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un documento.", "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        HistoryProgress h = controller.getHistory(document);
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.", "Exportar PDF", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("reporte_progreso_" + document + ".pdf"));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File out = chooser.getSelectedFile();
        if (!out.getName().toLowerCase().endsWith(".pdf")) {
            out = new File(out.getParentFile(), out.getName() + ".pdf");
        }
        try {
            reporteService.generar(h, out);
            JOptionPane.showMessageDialog(this, "Reporte generado:\n" + out.getAbsolutePath(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closePanel() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new WelcomePanel());
        }
    }
}
