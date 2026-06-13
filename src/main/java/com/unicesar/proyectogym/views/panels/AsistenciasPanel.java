package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.AttendanceController;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.AttendanceTableModel;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import javax.swing.*;

public class AsistenciasPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AttendanceController controller;
    private final AttendanceTableModel tableModel = new AttendanceTableModel();
    private final JTable tabla = new JTable(tableModel);
    private final JTextField txtDocumento = new JTextField(14);
    private final JLabel lblTotal = new JLabel(" ");

    public AsistenciasPanel() {
        this.controller = AppContext.get().newAsistenciaController();
        initUI();
        cargarTodas();
    }

    private void initUI() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setOpaque(false);

        JLabel titulo = new JLabel("REGISTRO DE ASISTENCIAS");
        ModernComponents.styleLabel(titulo, Font.BOLD, 24f, ModernComponents.CYAN_NEON);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(titulo, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        ModernComponents.stylePanel(top, true);

        JLabel lblDoc = new JLabel("Documento:");
        ModernComponents.styleLabel(lblDoc, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        top.add(lblDoc);

        ModernComponents.styleTextField(txtDocumento);
        top.add(txtDocumento);

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnTodas = new JButton("Ver todas");
        ModernComponents.styleButton(btnFiltrar, true);
        ModernComponents.styleButton(btnTodas, false);

        btnFiltrar.addActionListener(e -> filtrar());
        btnTodas.addActionListener(e -> {
            txtDocumento.setText("");
            cargarTodas();
        });

        top.add(btnFiltrar);
        top.add(btnTodas);
        northPanel.add(top, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(28);
        ModernComponents.styleTable(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        ModernComponents.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        ModernComponents.styleButton(btnCerrar, false);
        btnCerrar.addActionListener(e -> closePanel());

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);

        ModernComponents.styleLabel(lblTotal, Font.BOLD, 14f, ModernComponents.TEXT_WHITE);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        sur.add(lblTotal, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        der.setOpaque(false);
        der.add(btnCerrar);
        sur.add(der, BorderLayout.EAST);

        add(sur, BorderLayout.SOUTH);
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

    private void closePanel() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new WelcomePanel());
        }
    }
}
