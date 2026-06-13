package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.model.PhysicalGoal;
import com.unicesar.proyectogym.service.ValidationException;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.DateField;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import java.util.Optional;
import javax.swing.*;

public class PhysicalGoalPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final ProgresoController controller;

    private final JTextField txtDocumento = new JTextField(14);
    private final JLabel lblNombre = new JLabel("—");
    private final JTextField txtPeso = new JTextField(8);
    private final JTextField txtGrasa = new JTextField(8);
    private final DateField txtFecha = new DateField();
    private final JTextArea txtObs = new JTextArea(3, 18);
    private final JLabel lblAvance = new JLabel(" ");

    public PhysicalGoalPanel() {
        this.controller = AppContext.get().newProgresoController();
        initUI();
    }

    public PhysicalGoalPanel(String documento) {
        this();
        txtDocumento.setText(documento);
        buscar();
    }

    private void initUI() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        ModernComponents.stylePanel(card, true);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel titulo = new JLabel("METAS FÍSICAS");
        ModernComponents.styleLabel(titulo, Font.BOLD, 22f, ModernComponents.CYAN_NEON);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        card.add(titulo, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = row;
        JLabel lblDoc = new JLabel("Documento:");
        ModernComponents.styleLabel(lblDoc, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        card.add(lblDoc, c);

        c.gridx = 1;
        ModernComponents.styleTextField(txtDocumento);
        card.add(txtDocumento, c);

        JButton btnBuscar = new JButton("Buscar");
        ModernComponents.styleButton(btnBuscar, true);
        btnBuscar.addActionListener(e -> buscar());
        c.gridx = 2;
        card.add(btnBuscar, c);
        row++;

        c.gridx = 0; c.gridy = row;
        JLabel lblUser = new JLabel("Usuario:");
        ModernComponents.styleLabel(lblUser, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        card.add(lblUser, c);

        c.gridx = 1; c.gridwidth = 2;
        ModernComponents.styleLabel(lblNombre, Font.BOLD, 14f, ModernComponents.TEXT_WHITE);
        card.add(lblNombre, c);
        c.gridwidth = 1; row++;

        addRow(card, c, row++, "Peso objetivo (kg):", txtPeso);
        addRow(card, c, row++, "Grasa objetivo (%):", txtGrasa);
        addRow(card, c, row++, "Fecha estimada:", txtFecha);

        c.gridx = 0; c.gridy = row;
        JLabel lblObsTitle = new JLabel("Observaciones:");
        ModernComponents.styleLabel(lblObsTitle, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        card.add(lblObsTitle, c);

        c.gridx = 1; c.gridwidth = 2;
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        ModernComponents.styleTextArea(txtObs);
        JScrollPane obsScroll = new JScrollPane(txtObs);
        ModernComponents.styleScrollPane(obsScroll);
        card.add(obsScroll, c);
        c.gridwidth = 1; row++;

        c.gridx = 0; c.gridy = row; c.gridwidth = 3;
        ModernComponents.styleLabel(lblAvance, Font.BOLD, 14f, ModernComponents.CYAN_NEON);
        lblAvance.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblAvance, c);
        c.gridwidth = 1; row++;

        JButton btnGuardar = new JButton("Guardar meta");
        JButton btnAvance = new JButton("Ver avance");
        JButton btnCerrar = new JButton("Cerrar");

        ModernComponents.styleButton(btnGuardar, true);
        ModernComponents.styleButton(btnAvance, false);
        ModernComponents.styleButton(btnCerrar, false);

        btnGuardar.addActionListener(e -> guardar());
        btnAvance.addActionListener(e -> verAvance());
        btnCerrar.addActionListener(e -> closePanel());

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(btnCerrar);
        botones.add(btnAvance);
        botones.add(btnGuardar);

        c.gridx = 0; c.gridy = row; c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        card.add(botones, c);

        JScrollPane mainScroll = new JScrollPane(card);
        ModernComponents.styleScrollPane(mainScroll);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.gridx = 0;
        outerGbc.gridy = 0;
        outerGbc.weightx = 1.0;
        outerGbc.weighty = 1.0;
        outerGbc.fill = GridBagConstraints.BOTH;
        outerGbc.insets = new Insets(15, 15, 15, 15);
        add(mainScroll, outerGbc);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String labelText, Component field) {
        JLabel label = new JLabel(labelText);
        ModernComponents.styleLabel(label, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(label, c);

        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        if (field instanceof JTextField) {
            ModernComponents.styleTextField((JTextField) field);
        }
        panel.add(field, c);
        c.gridwidth = 1;
    }

    private String doc() {
        return txtDocumento.getText().trim();
    }

    private void buscar() {
        if (doc().isEmpty()) {
            return;
        }
        var m = controller.searchUser(doc());
        if (m.isPresent()) {
            lblNombre.setText(m.get().getFullName());
            lblNombre.setForeground(ModernComponents.CYAN_NEON);
            Optional<PhysicalGoal> meta = controller.activeGoal(doc());
            if (meta.isPresent()) {
                PhysicalGoal mt = meta.get();
                txtPeso.setText(String.valueOf(mt.getTargetWeight()));
                txtGrasa.setText(String.valueOf(mt.getTargetFat()));
                txtFecha.setDate(mt.getEstimatedDate());
                txtObs.setText(mt.getObservations());
            }
            verAvance();
        } else {
            lblNombre.setText("No existe un usuario con ese documento");
            lblNombre.setForeground(new Color(255, 100, 100));
        }
    }

    private void guardar() {
        try {
            PhysicalGoal meta = PhysicalGoal.builder()
                    .documentUser(doc())
                    .targetWeight(parse(txtPeso.getText(), "Peso objetivo"))
                    .targetFat(parse(txtGrasa.getText(), "Grasa objetivo"))
                    .estimatedDate(txtFecha.getDate())
                    .observations(txtObs.getText().trim())
                    .build();
            if (controller.searchUser(doc()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "No existe un usuario con ese documento.", "Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.registerGoal(meta);
            JOptionPane.showMessageDialog(this, "Meta guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            verAvance();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getFormattedMessage(), "Corrija los siguientes datos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verAvance() {
        if (doc().isEmpty()) {
            return;
        }
        HistoryProgress h = controller.getHistory(doc());
        if (h == null || h.getActiveGoal() == null) {
            lblAvance.setText("Sin meta activa.");
            return;
        }
        lblAvance.setText("Avance → Peso: " + h.getAdvanceWeightPorc() + " %   |   Grasa: " + h.getAdvanceFatPorc() + " %");
    }

    private double parse(String s, String campo) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" es obligatorio.");
        }
        try {
            return Double.parseDouble(s.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" debe ser numérico.");
        }
    }

    private void closePanel() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new WelcomePanel());
        }
    }
}
