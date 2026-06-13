package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.model.ImcClassification;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.service.ValidationException;
import com.unicesar.proyectogym.views.util.DateField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class PhysicalEvaluationFrame extends JFrame {

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

    public PhysicalEvaluationFrame() {
        this.controller = AppContext.get().newProgresoController();
        initUI();
    }

    public PhysicalEvaluationFrame(String document) {
        this();
        txtDocumento.setText(document);
        searchUser();
    }

    private void initUI() {
        setTitle("Evaluación Física");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel titulo = new JLabel("Evaluación Física");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 20f));
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        form.add(titulo, c);
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Documento del usuario:"), c);
        c.gridx = 1; form.add(txtDocumento, c);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> searchUser());
        c.gridx = 2; form.add(btnBuscar, c);
        row++;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Usuario:"), c);
        c.gridx = 1; c.gridwidth = 2; form.add(lblNombre, c);
        c.gridwidth = 1; row++;
        addRow(form, c, row++, "Fecha de evaluación:", txtFecha);
        addRow(form, c, row++, "Peso (kg):", txtPeso);
        addRow(form, c, row++, "Altura (cm):", txtAltura);
        txtImc.setEditable(false);
        addRow(form, c, row++, "IMC (automático):", txtImc);
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Clasificación:"), c);
        c.gridx = 1; c.gridwidth = 2;
        lblClasificacion.setFont(lblClasificacion.getFont().deriveFont(java.awt.Font.BOLD));
        form.add(lblClasificacion, c);
        c.gridwidth = 1; row++;

        addRow(form, c, row++, "Grasa corporal (%):", txtGrasa);
        addRow(form, c, row++, "Masa muscular (kg):", txtMasa);
        addRow(form, c, row++, "Circunferencia cintura (cm):", txtCintura);
        addRow(form, c, row++, "Circunferencia cadera (cm):", txtCadera);
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Observaciones:"), c);
        c.gridx = 1; c.gridwidth = 2;
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        form.add(new JScrollPane(txtObs), c);
        c.gridwidth = 1; row++;

        JButton btnGuardar = new JButton("Guardar evaluación");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnCerrar = new JButton("Cerrar");
        btnGuardar.addActionListener(e -> save());
        btnLimpiar.addActionListener(e -> clean());
        btnCerrar.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(btnCerrar);
        buttons.add(btnLimpiar);
        buttons.add(btnGuardar);
        c.gridx = 0; c.gridy = row; c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        form.add(buttons, c);

        txtFecha.setDate(LocalDate.now());
        DocumentListener imcListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { recalculateImc(); }
            @Override public void removeUpdate(DocumentEvent e) { recalculateImc(); }
            @Override public void changedUpdate(DocumentEvent e) { recalculateImc(); }
        };
        txtPeso.getDocument().addDocumentListener(imcListener);
        txtAltura.getDocument().addDocumentListener(imcListener);

        setContentPane(new JScrollPane(form));
        pack();
        setLocationRelativeTo(null);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label,
                        java.awt.Component field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }

    private void searchUser() {
        String doc = txtDocumento.getText().trim();
        if (doc.isEmpty()) {
            return;
        }
        Optional<Member> m = controller.searchUser(doc);
        if (m.isPresent()) {
            lblNombre.setText(m.get().getFullName());
            lblNombre.setForeground(new java.awt.Color(0, 130, 0));
        } else {
            lblNombre.setText("No existe un usuario con ese documento");
            lblNombre.setForeground(java.awt.Color.RED);
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
                    "Evaluación registrada.\nIMC: " + e.getImc()
                    + "  (" + e.getClassification().getLabel() + ")",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clean();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getFormattedMessage(),
                    "Corrija los siguientes datos", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la evaluación:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhysicalEvaluationFrame().setVisible(true));
    }
}
