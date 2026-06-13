package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.AttendanceController;
import com.unicesar.proyectogym.views.util.AttendanceTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class AsistenciasFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AttendanceController controller;
    private final AttendanceTableModel tableModel = new AttendanceTableModel();
    private final JTable tabla = new JTable(tableModel);
    private final JTextField txtDocumento = new JTextField(14);
    private final JLabel lblTotal = new JLabel(" ");

    public AsistenciasFrame() {
        this.controller = AppContext.get().newAsistenciaController();
        initUI();
        cargarTodas();
    }

    private void initUI() {
        setTitle("Asistencias");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("Registro de Asistencias", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Documento:"));
        top.add(txtDocumento);
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnTodas = new JButton("Ver todas");
        btnFiltrar.addActionListener(e -> filtrar());
        btnTodas.addActionListener(e -> { txtDocumento.setText(""); cargarTodas(); });
        top.add(btnFiltrar);
        top.add(btnTodas);

        JPanel north = new JPanel(new BorderLayout());
        north.add(titulo, BorderLayout.NORTH);
        north.add(top, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(22);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel sur = new JPanel(new BorderLayout());
        lblTotal.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        sur.add(lblTotal, BorderLayout.WEST);
        JPanel der = new JPanel();
        der.add(btnCerrar);
        sur.add(der, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);

        setSize(640, 440);
        setLocationRelativeTo(null);
    }

    private void cargarTodas() {
        var lista = controller.listAll();
        tableModel.setData(lista);
        lblTotal.setText("Total de ingresos: " + lista.size());
    }

    private void filtrar() {
        String doc = txtDocumento.getText().trim();
        if (doc.isEmpty()) {
            cargarTodas();
            return;
        }
        var lista = controller.listDocuments(doc);
        tableModel.setData(lista);
        lblTotal.setText("Ingresos de " + doc + ": " + lista.size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AsistenciasFrame().setVisible(true));
    }
}
