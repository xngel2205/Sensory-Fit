package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.model.ImcClassification;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.service.ValidationException;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.DateField;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PhysicalEvaluationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final ProgresoController controller;

    private final JTextField txtDocumento = new JTextField(15);
    private final JLabel lblNombre = new JLabel("—");
    private final DateField txtFecha = new DateField();
    private final JTextField txtPeso = new JTextField(8);
    private final JTextField txtAltura = new JTextField(8);
    private final JTextField txtImc = new JTextField(8);
    private final JLabel lblClasificacion = new JLabel("—");
    private final JTextField txtGrasa = new JTextField(8);
    private final JTextField txtMasa = new JTextField(8);
    private final JTextField txtCintura = new JTextField(8);
    private final JTextField txtCadera = new JTextField(8);
    private final JTextArea txtObs = new JTextArea(3, 20);

    public PhysicalEvaluationPanel() {
        this.controller = AppContext.get().newProgresoController();
        initUI();
    }

    public PhysicalEvaluationPanel(String document) {
        this();
        txtDocumento.setText(document);
        searchUser();
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
        JLabel titulo = new JLabel("EVALUACIÓN FÍSICA");
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
        btnBuscar.addActionListener(e -> searchUser());
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

        addRow(card, c, row++, "Fecha de evaluación:", txtFecha);
        addRow(card, c, row++, "Peso (kg):", txtPeso);
        addRow(card, c, row++, "Altura (cm):", txtAltura);

        txtImc.setEditable(false);
        addRow(card, c, row++, "IMC (automático):", txtImc);

        c.gridx = 0; c.gridy = row;
        JLabel lblClassTitle = new JLabel("Clasificación:");
        ModernComponents.styleLabel(lblClassTitle, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        card.add(lblClassTitle, c);

        c.gridx = 1; c.gridwidth = 2;
        ModernComponents.styleLabel(lblClasificacion, Font.BOLD, 14f, ModernComponents.CYAN_NEON);
        card.add(lblClasificacion, c);
        c.gridwidth = 1; row++;

        addRow(card, c, row++, "Grasa corporal (%):", txtGrasa);
        addRow(card, c, row++, "Masa muscular (kg):", txtMasa);
        addRow(card, c, row++, "Cintura (cm):", txtCintura);
        addRow(card, c, row++, "Cadera (cm):", txtCadera);

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

        JButton btnGuardar = new JButton("Guardar evaluación");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnCerrar = new JButton("Cerrar");
        ModernComponents.styleButton(btnGuardar, true);
        ModernComponents.styleButton(btnLimpiar, false);
        ModernComponents.styleButton(btnCerrar, false);

        btnGuardar.addActionListener(e -> save());
        btnLimpiar.addActionListener(e -> clean());
        btnCerrar.addActionListener(e -> closePanel());

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(btnCerrar);
        buttons.add(btnLimpiar);
        buttons.add(btnGuardar);

        c.gridx = 0; c.gridy = row; c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        card.add(buttons, c);

        txtFecha.setDate(LocalDate.now());
        DocumentListener imcListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { recalculateImc(); }
            @Override public void removeUpdate(DocumentEvent e) { recalculateImc(); }
            @Override public void changedUpdate(DocumentEvent e) { recalculateImc(); }
        };
        txtPeso.getDocument().addDocumentListener(imcListener);
        txtAltura.getDocument().addDocumentListener(imcListener);

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

    private void searchUser() {
        String doc = txtDocumento.getText().trim();
        if (doc.isEmpty()) {
            return;
        }
        Optional<Member> m = controller.searchUser(doc);
        if (m.isPresent()) {
            lblNombre.setText(m.get().getFullName());
            lblNombre.setForeground(ModernComponents.CYAN_NEON);
        } else {
            lblNombre.setText("No existe un usuario con ese documento");
            lblNombre.setForeground(new Color(255, 100, 100));
        }
    }

    private void recalculateImc() {
        Double weight = parseOrNull(txtPeso.getText());
        Double height = parseOrNull(txtAltura.getText());
        if (weight == null || height == null || height <= 0) {
            txtImc.setText("");
            lblClasificacion.setText("—");
            return;
        }
        double imc = controller.calculateImc(weight, height);
        txtImc.setText(String.format(java.util.Locale.US, "%.2f", imc));
        lblClasificacion.setText(ImcClassification.classify(imc).getLabel());
    }

    private void save() {
        try {
            PhysicalEvaluation e = PhysicalEvaluation.builder()
                    .documentUser(txtDocumento.getText().trim())
                    .date(txtFecha.getDate())
                    .weight(parseRequired(txtPeso.getText(), "Peso"))
                    .height(parseRequired(txtAltura.getText(), "Altura"))
                    .fatPercentage(parseOpcional(txtGrasa.getText()))
                    .muscleMass(parseOpcional(txtMasa.getText()))
                    .waistCircumference(parseOpcional(txtCintura.getText()))
                    .hipCircumference(parseOpcional(txtCadera.getText()))
                    .observations(txtObs.getText().trim())
                    .build();

            if (controller.searchUser(e.getDocumentUser()).isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No existe un usuario con el documento " + e.getDocumentUser() + ".",
                        "Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.registerEvaluation(e);
            JOptionPane.showMessageDialog(this,
                    "Evaluación registrada.\nIMC: " + e.getImc() + "  (" + e.getClassification().getLabel() + ")",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clean();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getFormattedMessage(), "Corrija los siguientes datos", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la evaluación:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clean() {
        txtPeso.setText("");
        txtAltura.setText("");
        txtImc.setText("");
        lblClasificacion.setText("—");
        txtGrasa.setText("");
        txtMasa.setText("");
        txtCintura.setText("");
        txtCadera.setText("");
        txtObs.setText("");
        txtFecha.setDate(LocalDate.now());
    }

    private Double parseOrNull(String s) {
        try {
            return Double.valueOf(s.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private double parseRequired(String s, String field) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + field + "\" es obligatorio.");
        }
        Double v = parseOrNull(s);
        if (v == null) {
            throw new IllegalArgumentException("El campo \"" + field + "\" debe ser numérico.");
        }
        return v;
    }

    private double parseOpcional(String s) {
        Double v = parseOrNull(s);
        return v == null ? 0 : v;
    }

    private void closePanel() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new WelcomePanel());
        }
    }
}
