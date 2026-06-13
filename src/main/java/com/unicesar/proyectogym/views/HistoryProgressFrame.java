package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.service.report.ReporteProgresoService;
import com.unicesar.proyectogym.views.util.DateField;
import com.unicesar.proyectogym.views.util.EvaluacionTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HistoryProgressFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ProgresoController controller;
    private final ReporteProgresoService reporteService = new ReporteProgresoService();

    private final JTextField txtDocumento = new JTextField(14);
    private final DateField txtDesde = new DateField();
    private final DateField txtHasta = new DateField();
    private final JLabel lblNombre = new JLabel("—");
    private final EvaluacionTableModel tableModel = new EvaluacionTableModel();
    private final JTable tabla = new JTable(tableModel);

    public HistoryProgressFrame() {
        this.controller = AppContext.get().newProgresoController();
        initUI();
    }

    public HistoryProgressFrame(String document) {
        this();
        txtDocumento.setText(document);
        load();
    }

    private void initUI() {
        setTitle("Historial de Progreso");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Documento:"));
        top.add(txtDocumento);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> load());
        top.add(btnBuscar);
        top.add(new JLabel("   Desde:"));
        top.add(txtDesde);
        top.add(new JLabel("Hasta:"));
        top.add(txtHasta);
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> load());
        top.add(btnFiltrar);
        JPanel north = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Historial de Progreso", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        north.add(titulo, BorderLayout.NORTH);
        north.add(top, BorderLayout.CENTER);
        lblNombre.setBorder(BorderFactory.createEmptyBorder(0, 10, 4, 0));
        north.add(lblNombre, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(22);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        JButton btnNueva = new JButton("Nueva evaluación");
        JButton btnEstadisticas = new JButton("Ver estadísticas");
        JButton btnPdf = new JButton("Exportar PDF");
        JButton btnCerrar = new JButton("Cerrar");
        btnNueva.addActionListener(e -> openNewEvaluation());
        btnEstadisticas.addActionListener(e -> openStatistics());
        btnPdf.addActionListener(e -> exportPdf());
        btnCerrar.addActionListener(e -> dispose());
        JPanel sur = new JPanel();
        sur.add(btnNueva);
        sur.add(btnEstadisticas);
        sur.add(btnPdf);
        sur.add(btnCerrar);
        add(sur, BorderLayout.SOUTH);
        setSize(760, 460);
        setLocationRelativeTo(null);
    }

    private String doc() {
        return txtDocumento.getText().trim();
    }

    private void load() {
        String document = doc();
        if (document.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un documento.",
                    "Historial", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        var member = controller.searchUser(document);
        if (member.isEmpty()) {
            lblNombre.setText("No existe un usuario con ese documento.");
            lblNombre.setForeground(java.awt.Color.RED);
            tableModel.setData(java.util.Collections.emptyList());
            return;
        }
        lblNombre.setText("Usuario: " + member.get().getFullName());
        lblNombre.setForeground(new java.awt.Color(0, 110, 0));

        java.time.LocalDate from = null;
        java.time.LocalDate until = null;
        try {
            from = txtDesde.getDate();
            until = txtHasta.getDate();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.setData(controller.rangeEvaluations(document, from, until));
    }

    private void openNewEvaluation() {
        new PhysicalEvaluationFrame(doc()).setVisible(true);
    }

    private void openStatistics() {
        if (doc().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un documento.",
                    "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new StatisticsFrame(doc()).setVisible(true);
    }

    private void exportPdf() {
        String document = doc();
        if (document.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un documento.",
                    "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        HistoryProgress h = controller.getHistory(document);
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.",
                    "Exportar PDF", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Reporte generado:\n" + out.getAbsolutePath(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HistoryProgressFrame().setVisible(true));
    }
}
